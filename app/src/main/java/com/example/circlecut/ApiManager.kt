package com.example.circlecut

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ApiManager {
    private val apiKey =""
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
            return null
        }
    }
    suspend fun initializeUser(apiKey: String, userToken: String, idempotencyKey: String, blockchain: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val mediaType = "application/json".toMediaTypeOrNull()
                val body = "{\"idempotencyKey\":\"$idempotencyKey\",\"blockchains\":\"[$blockchain]\"}".toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://api.circle.com/v1/w3s/user/initalize")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("X-User-Token", userToken)
                    .build()

                val response = client.newCall(request).execute()
                return@withContext response.body?.string()
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }
    suspend fun getWalletDetails(userId: String, apiKey: String): String? {
        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.circle.com/v1/w3s/wallets/$userId")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            return response.body?.string()

        } catch (e: Exception) {
            return null
        }
    }
    private fun readApiKey(): String {
        return "YOUR_API_KEY"
    }
}
