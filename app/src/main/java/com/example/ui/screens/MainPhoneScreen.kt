package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ContactEntity
import com.example.ui.components.IosBottomNavBar
import com.example.ui.theme.IosDarkBackground
import com.example.viewmodel.PhoneViewModel

@Composable
fun MainPhoneScreen(viewModel: PhoneViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val dialedNumber by viewModel.dialedNumber.collectAsStateWithLifecycle()
    val contacts by viewModel.allContacts.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteContacts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val callLogs by viewModel.callLogs.collectAsStateWithLifecycle()
    val isMissedOnly by viewModel.recentsFilterMissed.collectAsStateWithLifecycle()
    val voicemails by viewModel.voicemails.collectAsStateWithLifecycle()
    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
    val selectedContact by viewModel.selectedContact.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiExtracting.collectAsStateWithLifecycle()

    var isAddEditSheetOpen by remember { mutableStateOf(false) }
    var isAiDialogOpen by remember { mutableStateOf(false) }
    var initialPhoneForAdd by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
    ) {
        // Active Call Screen Fullscreen Overlay
        if (activeCall != null) {
            val call = activeCall!!
            ActiveCallScreen(
                contactName = call.contactName,
                phoneNumber = call.phoneNumber,
                isIncoming = call.isIncoming,
                isConnected = call.isConnected,
                durationSeconds = call.durationSeconds,
                isMuted = call.isMuted,
                isSpeakerOn = call.isSpeakerOn,
                isKeypadOpen = call.isKeypadOpen,
                inCallDialedString = call.inCallDialedString,
                onAcceptCall = { viewModel.acceptIncomingCall() },
                onEndCall = { viewModel.endCall() },
                onToggleMute = { viewModel.toggleMute() },
                onToggleSpeaker = { viewModel.toggleSpeaker() },
                onToggleKeypad = { viewModel.toggleInCallKeypad() },
                onInCallDigitClick = { digit -> viewModel.appendInCallDigit(digit) }
            )
        } else if (selectedContact != null) {
            // Selected Contact Detail View
            ContactDetailScreen(
                contact = selectedContact!!,
                onBackClick = { viewModel.selectedContact.value = null },
                onEditClick = { isAddEditSheetOpen = true },
                onCallClick = { phone -> viewModel.initiateCall(phone, selectedContact?.fullName ?: "") },
                onFavoriteToggle = { viewModel.toggleFavorite(selectedContact!!) },
                onDeleteClick = {
                    viewModel.deleteContact(selectedContact!!)
                    viewModel.selectedContact.value = null
                }
            )
        } else if (isAddEditSheetOpen) {
            // Add or Edit Contact Sheet
            AddEditContactSheet(
                initialContact = selectedContact,
                initialPhone = initialPhoneForAdd,
                onDismiss = {
                    isAddEditSheetOpen = false
                    initialPhoneForAdd = ""
                },
                onSave = { contact ->
                    viewModel.saveContact(contact)
                    isAddEditSheetOpen = false
                    initialPhoneForAdd = ""
                },
                onExtractAi = { text, callback ->
                    viewModel.extractContactWithAi(text, callback)
                },
                isAiLoading = isAiLoading
            )
        } else {
            // Main Bottom Tabbed Interface
            Scaffold(
                bottomBar = {
                    IosBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectedTab.value = it }
                    )
                },
                containerColor = IosDarkBackground
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> FavoritesScreen(
                            favorites = favorites,
                            onContactClick = { contact -> viewModel.selectedContact.value = contact },
                            onCallClick = { phone -> viewModel.initiateCall(phone) }
                        )
                        1 -> RecentsScreen(
                            callLogs = callLogs,
                            isMissedOnly = isMissedOnly,
                            onFilterChanged = { missed -> viewModel.recentsFilterMissed.value = missed },
                            onCallClick = { phone, name -> viewModel.initiateCall(phone, name) },
                            onClearAll = { viewModel.clearAllCallLogs() }
                        )
                        2 -> ContactsScreen(
                            contacts = contacts,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { query -> viewModel.searchQuery.value = query },
                            onContactClick = { contact -> viewModel.selectedContact.value = contact },
                            onAddContactClick = {
                                viewModel.selectedContact.value = null
                                isAddEditSheetOpen = true
                            },
                            onAiImportClick = { isAiDialogOpen = true }
                        )
                        3 -> KeypadScreen(
                            dialedNumber = dialedNumber,
                            onDigitClick = { digit -> viewModel.appendDialDigit(digit) },
                            onDeleteClick = { viewModel.deleteDialDigit() },
                            onCallClick = { number -> viewModel.initiateCall(number) },
                            onAddNumberClick = { number ->
                                initialPhoneForAdd = number
                                viewModel.selectedContact.value = null
                                isAddEditSheetOpen = true
                            }
                        )
                        4 -> VoicemailScreen(
                            voicemails = voicemails,
                            onCallClick = { phone, name -> viewModel.initiateCall(phone, name) },
                            onDeleteClick = { id -> viewModel.deleteVoicemail(id) },
                            onMarkRead = { id -> viewModel.markVoicemailRead(id) }
                        )
                    }
                }
            }
        }

        // Gemini AI Assistant Dialog
        if (isAiDialogOpen) {
            AiContactAssistantDialog(
                onDismiss = { isAiDialogOpen = false },
                onExtract = { text, callback ->
                    viewModel.extractContactWithAi(text, callback)
                },
                onSaveContact = { extracted ->
                    val contact = ContactEntity(
                        firstName = extracted.firstName,
                        lastName = extracted.lastName,
                        phoneMobile = extracted.mobile,
                        phoneWork = extracted.workPhone,
                        email = extracted.email,
                        company = extracted.company,
                        jobTitle = extracted.jobTitle,
                        colorHex = extracted.colorHex,
                        notes = extracted.notes,
                        tag = extracted.tag
                    )
                    viewModel.saveContact(contact)
                    isAiDialogOpen = false
                },
                isLoading = isAiLoading
            )
        }
    }
}
