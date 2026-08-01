package com.example.data

import kotlinx.coroutines.flow.Flow

class PhoneRepository(private val dao: PhoneDao) {

    val allContacts: Flow<List<ContactEntity>> = dao.getAllContacts()
    val favoriteContacts: Flow<List<ContactEntity>> = dao.getFavoriteContacts()
    val allCallLogs: Flow<List<CallLogEntity>> = dao.getAllCallLogs()
    val missedCallLogs: Flow<List<CallLogEntity>> = dao.getMissedCallLogs()
    val allVoicemails: Flow<List<VoicemailEntity>> = dao.getAllVoicemails()

    fun searchContacts(query: String): Flow<List<ContactEntity>> = dao.searchContacts(query)

    suspend fun getContactById(id: Long): ContactEntity? = dao.getContactById(id)

    suspend fun getContactByPhone(phone: String): ContactEntity? = dao.getContactByPhone(phone)

    suspend fun insertContact(contact: ContactEntity): Long = dao.insertContact(contact)

    suspend fun updateContact(contact: ContactEntity) = dao.updateContact(contact)

    suspend fun deleteContact(contact: ContactEntity) = dao.deleteContact(contact)

    suspend fun deleteContactById(id: Long) = dao.deleteContactById(id)

    suspend fun toggleFavorite(id: Long, current: Boolean) = dao.updateFavorite(id, !current)

    suspend fun addCallLog(callLog: CallLogEntity): Long = dao.insertCallLog(callLog)

    suspend fun deleteCallLog(id: Long) = dao.deleteCallLog(id)

    suspend fun clearAllCallLogs() = dao.clearAllCallLogs()

    suspend fun updateCallLogSummary(id: Long, summary: String) = dao.updateCallLogSummary(id, summary)

    suspend fun markVoicemailRead(id: Long) = dao.markVoicemailRead(id)

    suspend fun deleteVoicemail(id: Long) = dao.deleteVoicemail(id)
}
