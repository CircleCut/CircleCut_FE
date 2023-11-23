package com.example.circlecut

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class ApiManager  {
    private var apiKey="";

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
             jsonObject.optJSONObject("data")?.optString("userToken")
            return responseBody
        } catch (e: Exception) {
                throw e
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
            throw e
        }
    }
    suspend fun initializeUser(usertoken:String,idemkey:String): String? {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = "{\"blockchains\":[\"ETH-GOERLI\"],\"accountType\":\"SCA\",\"idempotencyKey\":\"$idemkey\"}".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.circle.com/v1/w3s/user/initialize")
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("X-User-Token", "$usertoken")
            .addHeader("content-type", "application/json")
            .addHeader("authorization", "Bearer $apiKey")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getWalletDetails(userId: String): String? {
        return try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.circle.com/v1/w3s/wallets?userId=$userId&pageSize=10")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            return response.body?.string()
        } catch (e: Exception) {
            // Handle exceptions appropriately, e.g., log or return null
            e.printStackTrace()
            null
        }
    }
    private fun readApiKey(): String {
        return "YOUR_API_KEY"
    }
    suspend fun makeGetRequest(url: String): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun UserPin(userToken: String, idempotencyKey: String): String? {
        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()
        val requestBody = "{\"idempotencyKey\":\"$idempotencyKey\"}".toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.circle.com/v1/w3s/user/pin")
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("X-User-Token", userToken)
            .addHeader("content-type", "application/json")
            .addHeader("authorization", "Bearer $apiKey")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: Exception) {
            null
        }
    }

}
