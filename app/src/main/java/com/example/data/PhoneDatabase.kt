package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ContactEntity::class, CallLogEntity::class, VoicemailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PhoneDatabase : RoomDatabase() {
    abstract fun phoneDao(): PhoneDao

    companion object {
        @Volatile
        private var INSTANCE: PhoneDatabase? = null

        fun getDatabase(context: Context, externalScope: CoroutineScope): PhoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhoneDatabase::class.java,
                    "ios_phone_database"
                )
                .addCallback(PhoneDatabaseCallback(externalScope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class PhoneDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.phoneDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: PhoneDao) {
                val now = System.currentTimeMillis()
                val hour = 3600000L
                val day = 86400000L

                // Prepopulate Sample iOS Contacts
                val contacts = listOf(
                    ContactEntity(
                        firstName = "Alex",
                        lastName = "Rivera",
                        phoneMobile = "+1 (555) 234-5678",
                        phoneWork = "+1 (555) 987-0011",
                        email = "alex.rivera@designstudio.io",
                        company = "Apple Design Studio",
                        jobTitle = "Product Designer",
                        colorHex = "#34C759",
                        isFavorite = true,
                        notes = "Met at iOS WWDC. Prefers FaceTime audio calls.",
                        tag = "Mobile"
                    ),
                    ContactEntity(
                        firstName = "Sarah",
                        lastName = "Jenkins",
                        phoneMobile = "+1 (555) 345-6789",
                        phoneHome = "+1 (555) 111-2233",
                        email = "sarah.j@techcorp.com",
                        company = "Tech Corp AI",
                        jobTitle = "VP of Engineering",
                        colorHex = "#007AFF",
                        isFavorite = true,
                        notes = "Leading the Gemini Integration team.",
                        tag = "Work"
                    ),
                    ContactEntity(
                        firstName = "Tim",
                        lastName = "Cook",
                        phoneMobile = "+1 (408) 996-1010",
                        phoneWork = "+1 (408) 974-2020",
                        email = "tcook@apple.com",
                        company = "Apple Inc.",
                        jobTitle = "CEO",
                        colorHex = "#AF52DE",
                        isFavorite = true,
                        notes = "Cupertino HQ executive office.",
                        tag = "Mobile"
                    ),
                    ContactEntity(
                        firstName = "David",
                        lastName = "Miller",
                        phoneMobile = "+1 (555) 876-5432",
                        email = "david@architects.org",
                        company = "Apex Architects",
                        jobTitle = "Lead Architect",
                        colorHex = "#FF9500",
                        isFavorite = false,
                        notes = "Consultant for home renovation project.",
                        tag = "Mobile"
                    ),
                    ContactEntity(
                        firstName = "Dr. Elena",
                        lastName = "Rostova",
                        phoneMobile = "+1 (555) 432-1098",
                        phoneWork = "+1 (555) 888-9900",
                        email = "dr.rostova@healthclinic.org",
                        company = "City Medical Center",
                        jobTitle = "Cardiologist",
                        colorHex = "#FF2D55",
                        isFavorite = false,
                        notes = "Clinic hours: Mon-Thu 9am-4pm.",
                        tag = "Work"
                    ),
                    ContactEntity(
                        firstName = "Mom",
                        lastName = "",
                        phoneMobile = "+1 (555) 777-8899",
                        email = "mom@family.net",
                        colorHex = "#FF3B30",
                        isFavorite = true,
                        notes = "Birthday: October 14. Favorite flowers: Tulips.",
                        tag = "iPhone"
                    ),
                    ContactEntity(
                        firstName = "Michael",
                        lastName = "Scott",
                        phoneMobile = "+1 (555) 444-3322",
                        company = "Dunder Mifflin",
                        jobTitle = "Regional Manager",
                        colorHex = "#5856D6",
                        isFavorite = false,
                        notes = "Scranton Branch Manager.",
                        tag = "Work"
                    )
                )

                for (c in contacts) {
                    dao.insertContact(c)
                }

                // Prepopulate Sample Recents / Call Logs
                val callLogs = listOf(
                    CallLogEntity(
                        contactName = "Sarah Jenkins",
                        phoneNumber = "+1 (555) 345-6789",
                        callType = CallType.INCOMING,
                        timestamp = now - (15 * 60000L),
                        durationSeconds = 245,
                        aiSummary = "Discussed Q3 mobile architecture strategy and Gemini API rollout timeline."
                    ),
                    CallLogEntity(
                        contactName = "Mom",
                        phoneNumber = "+1 (555) 777-8899",
                        callType = CallType.OUTGOING,
                        timestamp = now - (2 * hour),
                        durationSeconds = 620,
                        aiSummary = "Checked in for weekend dinner plans. Confirmed 6:30 PM Sunday."
                    ),
                    CallLogEntity(
                        contactName = "Alex Rivera",
                        phoneNumber = "+1 (555) 234-5678",
                        callType = CallType.MISSED,
                        timestamp = now - (5 * hour),
                        durationSeconds = 0,
                        aiSummary = "Missed call regarding iOS 26 UI design tokens."
                    ),
                    CallLogEntity(
                        contactName = "Unknown Caller",
                        phoneNumber = "+1 (800) 555-0199",
                        callType = CallType.MISSED,
                        timestamp = now - (1 * day),
                        durationSeconds = 0,
                        aiSummary = "Potential spam / telemarketer."
                    ),
                    CallLogEntity(
                        contactName = "David Miller",
                        phoneNumber = "+1 (555) 876-5432",
                        callType = CallType.OUTGOING,
                        timestamp = now - (1 * day + 3 * hour),
                        durationSeconds = 180,
                        aiSummary = "Reviewed draft building permits and site survey reports."
                    )
                )

                for (cl in callLogs) {
                    dao.insertCallLog(cl)
                }

                // Prepopulate Sample Voicemails
                val voicemails = listOf(
                    VoicemailEntity(
                        contactName = "Alex Rivera",
                        phoneNumber = "+1 (555) 234-5678",
                        timestamp = now - (5 * hour),
                        durationSeconds = 24,
                        isRead = false,
                        transcript = "Hey! Just wanted to share the latest iOS 26 liquid glass keypad animations and contact card designs. Call me back when you get a chance!"
                    ),
                    VoicemailEntity(
                        contactName = "Dr. Elena Rostova",
                        phoneNumber = "+1 (555) 432-1098",
                        timestamp = now - (2 * day),
                        durationSeconds = 38,
                        isRead = true,
                        transcript = "Hello, this is City Medical calling to confirm your annual health checkup appointment for Thursday at 10:00 AM. Please call us back if you need to reschedule."
                    )
                )

                for (v in voicemails) {
                    dao.insertVoicemail(v)
                }
            }
        }
    }
}
