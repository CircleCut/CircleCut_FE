package com.example.circlecut

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject

class WalletInitializationActivity : AppCompatActivity(), EventListener, Callback<ExecuteResult> {
    var usertoken:String =""
    var encryptionkey:String=""
    var challengeid:String=""
    var userId:String=""
    val supabase=supabaseinit().getsupa(R.string.supabaseurl.toString(),R.string.supabasekey.toString())
    val mycallback=this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)
        val signupButton: Button = findViewById(R.id.signInButton)
        val namet:EditText=findViewById(R.id.name)
        val emailt:EditText=findViewById(R.id.email)
        val passt:EditText=findViewById(R.id.passwd)
        val mobnot:EditText=findViewById(R.id.mob)
        signupButton.setOnClickListener {
            val name = namet.text.toString()
            val email = emailt.text.toString()
            val pass = passt.text.toString()
            val mobno = mobnot.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val uid =insertuser(name,email,mobno,pass)
                println("uidup : $uid")
                if(uid!=""){
                launchapi(uid)}
            }
        }

    }
    private suspend fun insertuser(name: String,email: String,mobno:String,pass:String):String{
        val usr= com.example.circlecut.User(
            name = name,
            email = email,
            number = mobno,
            password = pass
        )
        try{
            var json = Json { ignoreUnknownKeys = true }
            val result = supabase.postgrest["users"].insert(usr).decodeList<User>()
            println("reultttt $result")
            try {
                if (result.isNotEmpty()) {
                    val firstUser: User = result.first()
                    userId = firstUser.uid.toString()
                    println("UserID: $userId")
                } else {
                    println("User list is empty")
                }
            } catch (e: Exception) {
                println("Error parsing JSON: ${e.message}")
            }
            return userId
        }
        catch (e:Exception){
            println(e)
            return ""
        }
    }
        private suspend fun launchapi(userId:String){
            val myCallback = this
            val apiManager = ApiManager()
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
                                    println("Enterd")
                                    initAndLaunchSdk {
                                        WalletSdk.execute(
                                            this@WalletInitializationActivity,
                                            "$usertoken", // Replace with your actual user token
                                            "$encryptionkey", // Replace with your actual encryption key
                                            arrayOf("$challengeid"), // Replace with your actual challenge ID
                                            myCallback
                                        )
                                        WalletSdk.setSecurityQuestions(
                                            arrayOf(
                                                SecurityQuestion("What is your father’s middle name?"),
                                                SecurityQuestion("What is your favorite sports team?"),
                                                SecurityQuestion("What is your mother’s maiden name?"),
                                                SecurityQuestion("What is the name of your first pet?"),
                                                SecurityQuestion("What is the name of the city you were born in?"),
                                                SecurityQuestion("What is the name of the first street you lived on?"),
                                                SecurityQuestion(
                                                    "When is your father’s birthday?",
                                                    SecurityQuestion.InputType.datePicker
                                                )
                                            )
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
            showSnack(t.message ?: "initSdk catch null")
            return
        }
        launchBlock()
    }
    override fun onResult(result: ExecuteResult) {
        val sessionManager = SessionManager(this);
        sessionManager.createLoginSession(userId, usertoken,encryptionkey)
        Toast.makeText(this, "Account initialized successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@WalletInitializationActivity, ExpenseActivity::class.java))
    }

    private fun showSnack(message: String) {
        // Show a Snackbar with the given message
    }

    override fun onEvent(event: ExecuteEvent?) {
        // Handle ExecuteEvent here
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
