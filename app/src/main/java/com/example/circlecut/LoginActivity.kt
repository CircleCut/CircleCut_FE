package com.example.circlecut

import SessionManager
import android.accessibilityservice.GestureDescription.StrokeDescription
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
import circle.programmablewallet.sdk.result.ExecuteResultType
import com.example.circlecut.pwcustom.MyLayoutProvider
import com.example.circlecut.pwcustom.MyViewSetterProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity(){
    var usertoken:String =""
    var encryptionkey:String =""
    val supabase=supabaseinit().getsupa(R.string.supabaseurl.toString(),R.string.supabasekey.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        val sessionManager = SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val emailt: EditText =findViewById(R.id.email)
        val passt: EditText =findViewById(R.id.passwd)
        val signupButton: Button = findViewById(R.id.signInButton)
        signupButton.setOnClickListener {
            val email = emailt.text.toString()
            val pass = passt.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val passfetch=getPass(email)
                println(passfetch)
                if(pass==passfetch){
                    val uid =getUid(email)
                    println(uid)
                    getSessiontoken(uid)
                    println(usertoken)
                    println(encryptionkey)
                    sessionManager.createLoginSession(uid, usertoken,encryptionkey)
                    startActivity(Intent(this@LoginActivity,ExpenseActivity::class.java))
                }
            }

            GlobalScope.launch(Dispatchers.IO) {
            }
        }
    }
    suspend fun getPass(username:String):String{
        var pass=""
        try{
            val result = supabase.postgrest["users"].select(columns = Columns.list("password")){
                eq("email", username)
            }.decodeList<User>()
            println(result)
            if (result.isNotEmpty()) {
                val firstUser: User = result.first()
                pass = firstUser.password.toString()
                return pass
            } else {
               return "Username Not Found"
            }
        }
        catch (e:Exception){
            return "Username Not Found ${e.toString()}"

        }
    }
    suspend fun getUid(username:String):String{
        var uid=""
        try{
            val result = supabase.postgrest["users"].select(columns = Columns.list("uid")){
                eq("email", username)
            }.decodeList<User>()
            println(result)
            if (result.isNotEmpty()) {
                println(result)
                val firstUser: User = result.first()
                uid = firstUser.uid.toString()
                return uid
            } else {
                return "Username Not Found"
            }
        }
        catch (e:Exception){
            return "DB error"

        }
    }
    suspend fun getSessiontoken(userId:String){
        try{
            val apiManager = ApiManager()
            val sessiontoken = apiManager.getSessionToken(userId)
            if (sessiontoken != null) {
                val jsonObject = JSONObject(sessiontoken)
                usertoken= jsonObject.optJSONObject("data")?.optString("userToken").toString()
                encryptionkey=jsonObject.optJSONObject("data")?.optString("encryptionKey").toString()
                println(usertoken)
            } else {
                println("Error occurred during sessiontoken.")
            }

        }
        catch (e:Exception){
            println(e)
        }
    }
}