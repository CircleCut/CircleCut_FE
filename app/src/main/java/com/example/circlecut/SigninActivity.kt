package com.example.circlecut

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

class SigninActivity : AppCompatActivity() {
    private val apiManager = ApiManager(R.string.apikey.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        val signupButton: Button = findViewById(R.id.signInButton)

        signupButton.setOnClickListener {
            // Execute the Coroutine when the button is clicked
            val userId = "11111119" // Replace with the actual user ID
//            GlobalScope.launch(Dispatchers.IO) {
//                val result = apiManager.createUser(userId)
//                // Handle the result here, update UI or perform any other action
//                if (result != null) {
//                    println(result)
//                } else {
//                    println("Error occurred during the HTTP call.")
//                }
//            }
            GlobalScope.launch(Dispatchers.IO) {
                val result = apiManager.getSessionToken(userId)
                // Handle the result here, update UI or perform any other action
                if (result != null) {
                    println(result)
                } else {
                    println("Error occurred during the HTTP call.")
                }
            }
        }
    }


}
