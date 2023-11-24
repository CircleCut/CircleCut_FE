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
    suspend fun makeTransferRequest(
        amounts: List<String>,
        idempotencyKey: String,
        tokenId: String,
        walletId: String,
        destinationAddress: String,
        userToken: String,
    ):String {
        val client = OkHttpClient()

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val requestBody = """
        {
            "amounts": $amounts,
            "idempotencyKey": "$idempotencyKey",
            "tokenId": "$tokenId",
            "walletId": "$walletId",
            "destinationAddress": "$destinationAddress",
            "feeLevel", "HIGH"
        }
    """.trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.circle.com/v1/w3s/user/transactions/transfer")
            .post(requestBody)
            .addHeader("accept", "application/json")
            .addHeader("X-User-Token", userToken)
            .addHeader("content-type", "application/json")
            .addHeader("authorization", "Bearer $apiKey")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        println(responseBody)
        val jsonObject = JSONObject(responseBody)
        val transactionResponse = jsonObject.getJSONObject("transactionResponse")
        val challengeId = transactionResponse.getJSONObject("data").getString("challengeId")

        return challengeId
    }

    suspend fun getUsdcTokenId(walletId: String,): String {
        val client = OkHttpClient()

        val url = "https://api.circle.com/v1/w3s/wallets/$walletId/balances?pageSize=10"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("accept", "application/json")
            .addHeader("authorization", "Bearer $apiKey")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""

        val jsonObject = JSONObject(responseBody)
        val dataObject = jsonObject.getJSONObject("data")
        val tokenBalancesArray = dataObject.getJSONArray("tokenBalances")

        for (i in 0 until tokenBalancesArray.length()) {
            val tokenObject = tokenBalancesArray.getJSONObject(i).getJSONObject("token")
            val name = tokenObject.getString("name")
            if (name == "USD Coin") {
                return tokenObject.getString("id")
            }
        }

        return "" // Return empty string if USD Coin is not found
    }

}
