package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ExtractedContact
import com.example.ai.GeminiContactExtractor
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActiveCallState(
    val contactName: String,
    val phoneNumber: String,
    val isIncoming: Boolean = false,
    val isConnected: Boolean = false,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isKeypadOpen: Boolean = false,
    val inCallDialedString: String = ""
)

class PhoneViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PhoneDatabase.getDatabase(application, viewModelScope)
    private val repository = PhoneRepository(db.phoneDao())

    // Navigation & View State
    val selectedTab = MutableStateFlow(3) // Default to Keypad (Tab 3) for dialer experience
    val recentsFilterMissed = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")

    // Dialpad state
    val dialedNumber = MutableStateFlow("")

    // Contacts & Call Logs Flow
    val allContacts = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.allContacts
        else repository.searchContacts(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteContacts = repository.favoriteContacts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs = recentsFilterMissed.flatMapLatest { missedOnly ->
        if (missedOnly) repository.missedCallLogs
        else repository.allCallLogs
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voicemails = repository.allVoicemails.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Contact Detail
    val selectedContact = MutableStateFlow<ContactEntity?>(null)

    // Active Call
    val activeCall = MutableStateFlow<ActiveCallState?>(null)
    private var callTimerJob: Job? = null

    // AI Status
    val isAiExtracting = MutableStateFlow(false)
    val aiExtractError = MutableStateFlow<String?>(null)
    val extractedContactResult = MutableStateFlow<ExtractedContact?>(null)

    // Keypad actions
    fun appendDialDigit(digit: String) {
        dialedNumber.value += digit
    }

    fun deleteDialDigit() {
        if (dialedNumber.value.isNotEmpty()) {
            dialedNumber.value = dialedNumber.value.dropLast(1)
        }
    }

    fun clearDialedNumber() {
        dialedNumber.value = ""
    }

    // Call Placement
    fun initiateCall(number: String, name: String = "") {
        if (number.isBlank()) return
        val displayName = if (name.isNotBlank()) name else number

        activeCall.value = ActiveCallState(
            contactName = displayName,
            phoneNumber = number,
            isIncoming = false,
            isConnected = false
        )

        // Simulate connecting call
        viewModelScope.launch {
            delay(1500)
            activeCall.value = activeCall.value?.copy(isConnected = true)
            startCallTimer()
        }
    }

    fun receiveIncomingCall(number: String, name: String) {
        activeCall.value = ActiveCallState(
            contactName = name,
            phoneNumber = number,
            isIncoming = true,
            isConnected = false
        )
    }

    fun acceptIncomingCall() {
        activeCall.value = activeCall.value?.copy(
            isIncoming = false,
            isConnected = true
        )
        startCallTimer()
    }

    fun endCall() {
        val currentCall = activeCall.value
        callTimerJob?.cancel()
        callTimerJob = null

        if (currentCall != null && currentCall.isConnected) {
            // Log outgoing/incoming call
            viewModelScope.launch {
                val callType = if (currentCall.isIncoming) CallType.INCOMING else CallType.OUTGOING
                val callId = repository.addCallLog(
                    CallLogEntity(
                        contactName = currentCall.contactName,
                        phoneNumber = currentCall.phoneNumber,
                        callType = callType,
                        durationSeconds = currentCall.durationSeconds
                    )
                )

                // Summarize with Gemini
                val summary = GeminiContactExtractor.summarizeCallLog(
                    contactName = currentCall.contactName,
                    callType = callType.name,
                    notes = "Call lasted ${currentCall.durationSeconds} seconds."
                )
                repository.updateCallLogSummary(callId, summary)
            }
        } else if (currentCall != null && currentCall.isIncoming && !currentCall.isConnected) {
            // Log missed call
            viewModelScope.launch {
                repository.addCallLog(
                    CallLogEntity(
                        contactName = currentCall.contactName,
                        phoneNumber = currentCall.phoneNumber,
                        callType = CallType.MISSED,
                        durationSeconds = 0,
                        aiSummary = "Missed call from ${currentCall.contactName}"
                    )
                )
            }
        }

        activeCall.value = null
        dialedNumber.value = ""
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                activeCall.value = activeCall.value?.let { call ->
                    call.copy(durationSeconds = call.durationSeconds + 1)
                }
            }
        }
    }

    fun toggleMute() {
        activeCall.value = activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        activeCall.value = activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleInCallKeypad() {
        activeCall.value = activeCall.value?.let { it.copy(isKeypadOpen = !it.isKeypadOpen) }
    }

    fun appendInCallDigit(digit: String) {
        activeCall.value = activeCall.value?.let { it.copy(inCallDialedString = it.inCallDialedString + digit) }
    }

    // Contact CRUD
    fun saveContact(contact: ContactEntity) {
        viewModelScope.launch {
            if (contact.id == 0L) {
                repository.insertContact(contact)
            } else {
                repository.updateContact(contact)
            }
            if (selectedContact.value?.id == contact.id) {
                selectedContact.value = contact
            }
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            if (selectedContact.value?.id == contact.id) {
                selectedContact.value = null
            }
        }
    }

    fun toggleFavorite(contact: ContactEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(contact.id, contact.isFavorite)
            selectedContact.value = selectedContact.value?.copy(isFavorite = !contact.isFavorite)
        }
    }

    // Gemini AI Smart Contact Extractor
    fun extractContactWithAi(rawText: String, onExtracted: (ExtractedContact) -> Unit) {
        if (rawText.isBlank()) return
        isAiExtracting.value = true
        aiExtractError.value = null

        viewModelScope.launch {
            val result = GeminiContactExtractor.extractContactFromText(rawText)
            isAiExtracting.value = false
            result.onSuccess { extracted ->
                extractedContactResult.value = extracted
                onExtracted(extracted)
            }.onFailure { err ->
                aiExtractError.value = err.message ?: "Failed to parse contact."
            }
        }
    }

    fun clearExtractedContactResult() {
        extractedContactResult.value = null
    }

    fun deleteCallLog(id: Long) {
        viewModelScope.launch {
            repository.deleteCallLog(id)
        }
    }

    fun clearAllCallLogs() {
        viewModelScope.launch {
            repository.clearAllCallLogs()
        }
    }

    fun markVoicemailRead(id: Long) {
        viewModelScope.launch {
            repository.markVoicemailRead(id)
        }
    }

    fun deleteVoicemail(id: Long) {
        viewModelScope.launch {
            repository.deleteVoicemail(id)
        }
    }
}
