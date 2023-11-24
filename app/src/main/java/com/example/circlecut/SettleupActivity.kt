package com.example.circlecut

import ExpenseD
import SessionManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import circle.programmablewallet.sdk.WalletSdk
import circle.programmablewallet.sdk.api.Callback
import circle.programmablewallet.sdk.api.ExecuteEvent
import circle.programmablewallet.sdk.api.ExecuteWarning
import circle.programmablewallet.sdk.presentation.EventListener
import circle.programmablewallet.sdk.presentation.SecurityQuestion
import circle.programmablewallet.sdk.presentation.SettingsManagement
import circle.programmablewallet.sdk.result.ExecuteResult
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class SettleupActivity: AppCompatActivity(), EventListener, Callback<ExecuteResult> {
    private val supabase = supabaseinit().getsupa(R.string.supabaseurl.toString(), R.string.supabasekey.toString())
    lateinit var uid1:String;
    lateinit var uid2:String;
    lateinit var amt:String;
    lateinit var currency:String
//    lateinit var rvname:TextView;
//    lateinit var amtview:TextView;
//    lateinit var swal:String;
//    lateinit var rwal:String;
//    lateinit var swalT:TextView;
//    lateinit var rwalT:TextView
    lateinit var amtT:TextView
    val apiManager = ApiManager()
    lateinit var sessionManager:SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settleup)
        val expenseId = intent.getLongExtra("EXPENSE_ID", -1)
        amtT = findViewById(R.id.amount)
        println("exid: "+expenseId)

        GlobalScope.launch(Dispatchers.IO) {
            getactivitydets(expenseId.toString())
            launchapi()
            // Switch to the main thread before updating UI
            withContext(Dispatchers.Main) {
                amtT.text = "$amt$currency"
            }
        }
    }

    private suspend fun getactivitydets(exid: String) {
        try {
            val result = supabase.postgrest["expenses"].select(columns = Columns.list()) {
                eq("expenseid", exid)
            }.decodeList<ExpenseD>()

            if (result.isNotEmpty()) {
                println(result)
                val firstUser: ExpenseD = result.first()
                uid1 = firstUser.uid1.toString()
                uid2 = firstUser.uid2.toString()
                amt = firstUser.amount.toString()
                currency = firstUser.currency.toString()
                println("First user " + getwallet(uid1))
                println("2 user " + getwallet(uid2))
                var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
                println(apiManager.makeTransferRequest(listOf("2"),idempotencyKey.toString(),"7adb2b7d-c9cd-5164-b2d4-b73b088274dc","baa51c8f-cf84-52cb-a1df-b26f629e3ae8","0x2de2dd730a1237455a2231623343e98362d324ba","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXZlbG9wZXJFbnRpdHlFbnZpcm9ubWVudCI6IlRFU1QiLCJlbnRpdHlJZCI6IjE1NGZhN2Q5LTUxMDItNDk2NC1hMGVjLTcxNzY4MTIzMDE5MiIsImV4cCI6MTcwMDc4NTE2NCwiaWF0IjoxNzAwNzgxNTY0LCJpbnRlcm5hbFVzZXJJZCI6IjcwODMzNTIzLWMzZjMtNTE4YS1hZGFlLTVmZWE3YTI3ZGViMSIsImlzcyI6Imh0dHBzOi8vcHJvZ3JhbW1hYmxlLXdhbGxldC5jaXJjbGUuY29tIiwianRpIjoiOTQ4MjBmNmQtN2U5Yi00NTY1LTg4YTItMzc3Y2NiMzA1ZjJmIiwic3ViIjoiMTEwMzAifQ.hdxfNLzjeYH9bqa9Uvd96WFcO4sKWhvV9CCK3CKvvXtNUDp52_WmqjQF3lDM7vXOsZNeJJk-1MyLWqSVpcT_GbvVf0aFveKskOVH0gVHelwWECWjr7dJQPePxrSGFcvIJpWjogv6hiO6yj6GYr6qayDUsWd2s190HyJrcaX6kWF9eRk1SQFOXMfhPA8L4eud-ghXn4ueRhjyjRPtn4OyYbmYbzJc8a9eV0Q2BZtTQZKiuFJtdNa2BRQRcs-wbtwb59akSjM0_7H4EOhwTh_fJMsB7IV0Q8dxlgRqoXdDjSUeo7qhbqYt14T3rSg7wlqPU3asQG9GfCBCqCRQLxLpsQ"))

            } else {
                println("Username Not Found")
            }
        } catch (e: Exception) {
            println("errorrrrr" + e)
        }
    }
    suspend fun getwallet(userId:String):String{
        if (sessionManager.checkLogin()) {
            val body=apiManager.getWalletDetails(userId);
            if(body!=null)
                return body;
        }
        return ""
    }
    private suspend fun launchapi(){
        val myCallback = this
        val apiManager = ApiManager()
        var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
        val jsonArray = JSONArray(idempotencyKey)
        idempotencyKey=jsonArray.optString(0)
        try{
//            val result = apiManager.createUser(userId)
//            println(result)
//            if (result != null) {
//                val sessiontoken = apiManager.getSessionToken(userId)
//                if (sessiontoken != null) {
//                    val jsonObject = JSONObject(sessiontoken)

                val userDetails = sessionManager.getUserDetails()
                val usertoken= userDetails[SessionManager.KEY_SESSION_TOKEN].toString()
                val encryptionkey = userDetails[SessionManager.KEY_ENCRYPTION_ID].toString()
                    println(usertoken)
                    if(usertoken!=null){
                        val inituser = apiManager.initializeUser(usertoken,idempotencyKey)
                        if(inituser!=null){
                            println(inituser)
                            val challengeid=""
                            if(challengeid!=""){
                                println("Enterd")
                                initAndLaunchSdk {
                                    WalletSdk.execute(
                                        this@SettleupActivity,
                                        usertoken,
                                        encryptionkey, // Replace with your actual encryption key
                                        arrayOf(challengeid), // Replace with your actual challenge ID
                                        myCallback
                                    )
                                }



                            }
                        }}
//                } else {
//                    println("Error occurred during sessiontoken.")
//                }
//            } else {
//                println("Error occurred during create user.")
//            }

        }
        catch (e:Exception){
            println(e)
        }
    }
    private inline fun initAndLaunchSdk(launchBlock: () -> Unit) {
        try {
            val settingsManagement = SettingsManagement()
            settingsManagement.isEnableBiometricsPin = false // Set your desired value

            WalletSdk.init(
                applicationContext,
                WalletSdk.Configuration(
                    "https://api.circle.com/v1/w3s/", // Replace with your actual endpoint
                    "2f5d4fb7-fafd-59a3-b3a2-64de18dca1b9", // Replace with your actual app ID
                    settingsManagement
                )
            )

        } catch (t: Throwable) {
//            showSnack(t.message ?: "initSdk catch null")
            return
        }
        launchBlock()
    }

    override fun onEvent(p0: ExecuteEvent?) {
        TODO("Not yet implemented")
    }

    override fun onError(p0: Throwable?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onWarning(p0: ExecuteWarning?, p1: ExecuteResult?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onResult(p0: ExecuteResult?) {
        TODO("Not yet implemented")
    }
}