package com.example.ai

import com.example.BuildConfig
import com.example.data.ContactEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ExtractedContact(
    val firstName: String = "",
    val lastName: String = "",
    val mobile: String = "",
    val workPhone: String = "",
    val email: String = "",
    val company: String = "",
    val jobTitle: String = "",
    val notes: String = "",
    val tag: String = "Mobile",
    val colorHex: String = "#007AFF"
)

object GeminiContactExtractor {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun extractContactFromText(inputText: String): Result<ExtractedContact> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback smart regex parser if API key is not configured in environment
                return@withContext Result.success(fallbackLocalExtract(inputText))
            }

            val prompt = """
                Extract contact information from the following text and return strictly valid JSON matching this schema:
                {
                  "firstName": "String",
                  "lastName": "String",
                  "mobile": "String",
                  "workPhone": "String",
                  "email": "String",
                  "company": "String",
                  "jobTitle": "String",
                  "notes": "String",
                  "tag": "String (e.g. Mobile, Work, Home, iPhone)"
                }
                
                Input Text:
                "$inputText"
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (!response.isSuccessful || responseStr.isEmpty()) {
                return@withContext Result.success(fallbackLocalExtract(inputText))
            }

            val jsonResponse = JSONObject(responseStr)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val rawText = parts.getJSONObject(0).optString("text", "{}")
                    val contactJson = JSONObject(rawText)

                    val colorOptions = listOf("#007AFF", "#34C759", "#FF9500", "#AF52DE", "#FF2D55", "#5856D6")
                    val randomColor = colorOptions.random()

                    val extracted = ExtractedContact(
                        firstName = contactJson.optString("firstName", ""),
                        lastName = contactJson.optString("lastName", ""),
                        mobile = contactJson.optString("mobile", ""),
                        workPhone = contactJson.optString("workPhone", ""),
                        email = contactJson.optString("email", ""),
                        company = contactJson.optString("company", ""),
                        jobTitle = contactJson.optString("jobTitle", ""),
                        notes = contactJson.optString("notes", ""),
                        tag = contactJson.optString("tag", "Mobile").ifEmpty { "Mobile" },
                        colorHex = randomColor
                    )
                    return@withContext Result.success(extracted)
                }
            }
            Result.success(fallbackLocalExtract(inputText))
        } catch (e: Exception) {
            Result.success(fallbackLocalExtract(inputText))
        }
    }

    suspend fun summarizeCallLog(contactName: String, callType: String, notes: String): String = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext "Call with $contactName ($callType). $notes"
            }

            val prompt = "Provide a 1-sentence executive AI summary for this phone call with $contactName ($callType): $notes"

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            val body = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful && responseStr.isNotEmpty()) {
                val jsonResponse = JSONObject(responseStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val text = candidates.getJSONObject(0)
                        .optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.getJSONObject(0)
                        ?.optString("text")
                    if (!text.isNullOrEmpty()) return@withContext text.trim()
                }
            }
            "Call recorded with $contactName."
        } catch (e: Exception) {
            "Call recorded with $contactName."
        }
    }

    private fun fallbackLocalExtract(text: String): ExtractedContact {
        // Fallback parser when API Key is absent
        val phoneRegex = Regex("""(\+?\d{1,3}[\s-]?)?\(?\d{3}\)?[\s-]?\d{3}[\s-]?\d{4}""")
        val emailRegex = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")

        val foundPhone = phoneRegex.find(text)?.value ?: ""
        val foundEmail = emailRegex.find(text)?.value ?: ""

        val words = text.replace(foundPhone, "").replace(foundEmail, "").trim().split(Regex("""\s+"""))
        val firstName = if (words.isNotEmpty()) words[0] else "New"
        val lastName = if (words.size > 1) words[1] else "Contact"

        return ExtractedContact(
            firstName = firstName,
            lastName = lastName,
            mobile = if (foundPhone.isNotEmpty()) foundPhone else "+1 (555) 000-1234",
            email = foundEmail,
            notes = "Parsed from Smart AI Import: $text"
        )
    }
}
