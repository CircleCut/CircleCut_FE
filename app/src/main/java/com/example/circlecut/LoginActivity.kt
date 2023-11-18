package com.example.circlecut

import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import circle.programmablewallet.sdk.WalletSdk
import circle.programmablewallet.sdk.api.Callback
import circle.programmablewallet.sdk.api.ExecuteEvent
import circle.programmablewallet.sdk.result.ExecuteResult
import circle.programmablewallet.sdk.api.ExecuteWarning
import circle.programmablewallet.sdk.presentation.EventListener
import circle.programmablewallet.sdk.presentation.SecurityQuestion
import circle.programmablewallet.sdk.presentation.SettingsManagement
import circle.programmablewallet.sdk.result.ExecuteResultType
import com.example.circlecut.pwcustom.MyLayoutProvider
import com.example.circlecut.pwcustom.MyViewSetterProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), Callback<ExecuteResult> {
    var usertoken:String =""
    var encryptionkey:String?=""
    var challengeid:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val myCallback = this
        val signupButton: Button = findViewById(R.id.signInButton)
        val apiManager = ApiManager()
        signupButton.setOnClickListener {
            val userId = "00001" // Replace with the actual user ID
            GlobalScope.launch(Dispatchers.IO) {
                var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
                val jsonArray = JSONArray(idempotencyKey)
                idempotencyKey=jsonArray.optString(0)
                try{
                    val result = apiManager.createUser(userId)
                    println(result)
                    if (result != null) {
                        val sessiontoken = apiManager.getSessionToken(userId)
                        if (sessiontoken != null) {
                            val jsonObject = JSONObject(sessiontoken)
                            usertoken= jsonObject.optJSONObject("data")?.optString("userToken").toString()
                            encryptionkey=jsonObject.optJSONObject("data")?.optString("encryptionKey").toString()
                            println(usertoken)
                            if(usertoken!=null){
                                val inituser = apiManager.initializeUser(usertoken,idempotencyKey)
                                if(inituser!=null){
                                    println(inituser)
                                    val jsonObject1 = JSONObject(inituser)
                                    challengeid=jsonObject1.optJSONObject("data")?.optString("challengeId").toString()
                                    if(challengeid!=""){
                                        initAndLaunchSdk {
                                            WalletSdk.execute(
                                                this@LoginActivity,
                                                "$usertoken", // Replace with your actual user token
                                                "$encryptionkey", // Replace with your actual encryption key
                                                arrayOf("$challengeid"), // Replace with your actual challenge ID
                                                myCallback
                                            )
                                        }
                                    }
                                }}
                        } else {
                            println("Error occurred during sessiontoken.")
                        }
                    } else {
                        println("Error occurred during create user.")
                    }

                }
                catch (e:Exception){
                    println(e)
                }

            }

            GlobalScope.launch(Dispatchers.IO) {
            }
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
                    "", // Replace with your actual app ID
                    settingsManagement
                )
            )

        } catch (t: Throwable) {
            showSnack(t.message ?: "initSdk catch null")
            return
        }
        launchBlock()
    }
    override fun onResult(result: ExecuteResult) {
        val walletAddress = result.data?.toString()
        Toast.makeText(this, "Wallet initialized successfully. Address: $walletAddress", Toast.LENGTH_SHORT).show()
        println(walletAddress)
        startActivity(Intent(this@LoginActivity, ExpenseActivity::class.java))
    }

    private fun showSnack(message: String) {
        // Show a Snackbar with the given message
    }
    override fun onError(error: Throwable): Boolean {
        // Handle error here
        return true
    }

    override fun onWarning(warning: ExecuteWarning?, result: ExecuteResult?): Boolean {
        // Handle warning here
        return false
    }
}