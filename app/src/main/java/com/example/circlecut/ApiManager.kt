package com.example.circlecut

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ApiManager {
    private val apiKey =""
    // Function to get the session token
    suspend fun getSessionToken(userId: String): String? {
        try {
            val client = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = "{\"userId\":\"$userId\"}".toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://api.circle.com/v1/w3s/users/token")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            // Parse JSON response and extract userToken
            val jsonObject = JSONObject(responseBody)
            return jsonObject.optJSONObject("data")?.optString("userToken")
        } catch (e: Exception) {
            // Handle exceptions or errors here
            return null
        }
    }
    suspend fun createUser(userId: String): String? {
        try {
            val client = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = "{\"userId\":\"$userId\"}".toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://api.circle.com/v1/w3s/users")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
//            return response.body?.string()
            return response.body?.string()

        } catch (e: Exception) {
            // Handle exceptions or errors here
            return null
        }
    }

    private fun readApiKey(): String {
        // Implement the logic to read the API key from local.properties file
        // (similar to the previous example)
        // ...
        return "YOUR_API_KEY"
    }
}
