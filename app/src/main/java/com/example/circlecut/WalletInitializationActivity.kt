package com.example.circlecut

import android.os.Bundle
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

class WalletInitializationActivity : AppCompatActivity(), EventListener, Callback<ExecuteResult> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        // Your activity initialization code here

        // Example: Initialize the user wallet
        initAndLaunchSdk {
            WalletSdk.execute(
                this@WalletInitializationActivity,
                "", // Replace with your actual user token
                "", // Replace with your actual encryption key
                arrayOf(""), // Replace with your actual challenge ID
                this
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

//        if (result.resultType == ExecuteResultType.CREATE_WALLET) {
//            val walletAddress = result.data?.toString()
//            Toast.makeText(this, "Wallet initialized successfully. Address: $walletAddress", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Wallet Failed", Toast.LENGTH_SHORT).show()
//        }
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
