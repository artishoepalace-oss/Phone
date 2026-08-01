package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voicemails")
data class VoicemailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 15,
    val isRead: Boolean = false,
    val transcript: String = "",
    val audioCategory: String = "Voicemail"
)
