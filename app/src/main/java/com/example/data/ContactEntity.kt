package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phoneMobile: String,
    val phoneWork: String = "",
    val phoneHome: String = "",
    val email: String = "",
    val company: String = "",
    val jobTitle: String = "",
    val colorHex: String = "#007AFF",
    val isFavorite: Boolean = false,
    val notes: String = "",
    val tag: String = "Mobile",
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val displayInitials: String
        get() {
            val f = firstName.take(1).uppercase()
            val l = lastName.take(1).uppercase()
            return if (f.isNotEmpty() || l.isNotEmpty()) "$f$l" else "#"
        }
}
