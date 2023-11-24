package com.example.circlecut

import ExpenseD
import SessionManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import com.example.circlecut.pwcustom.MyLayoutProvider
import com.example.circlecut.pwcustom.MyViewSetterProvider
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
    lateinit var usertoken:String
    lateinit var walletId1:String
    lateinit var walletadd1:String
    lateinit var walletadd2:String
    lateinit var c:Context

    lateinit var tokenid:String
    lateinit var swal:String;
    lateinit var rwal:String;
    lateinit var swalT:TextView;
    lateinit var rwalT:TextView
    lateinit var amtT:TextView
    val apiManager = ApiManager()
    lateinit var sessionManager:SessionManager
    val myCallback = this
    override fun onCreate(savedInstanceState: Bundle?) {
        c =this
        sessionManager = SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settleup)
        val expenseId = intent.getLongExtra("EXPENSE_ID", -1)
        amtT = findViewById(R.id.amount)
        val settlebut:Button = findViewById(R.id.settleButton)
        swalT =findViewById(R.id.swalT)
        rwalT=findViewById(R.id.rwalT)
        GlobalScope.launch(Dispatchers.IO) {
            getactivitydets(expenseId.toString())
            println("uid1"+uid1)
            println("uid2"+uid2)

            swal  = getwallet(uid1)
            rwal =getwallet(uid2)
            println("Sender wallet: "+swal)
            println("Reciver wallet: "+rwal)
            try {
                val jsonObject = JSONObject(swal)
                val walletsArray = jsonObject.getJSONObject("data").getJSONArray("wallets")
                if (walletsArray.length() > 0) {
                    // Assuming there's only one wallet in the array, you can adjust if needed
                    val walletObject = walletsArray.getJSONObject(0)
                    walletId1 = walletObject.getString("id")
                    walletadd1 = walletObject.getString("address")
                    println("Sender :"+walletId1+walletadd1)
                }
            } catch (e: Exception) {
                // Handle JSON parsing exception if needed
                println("Sender problem")
                e.printStackTrace()
            }
            try {
                val jsonObject = JSONObject(rwal)
                val walletsArray = jsonObject.getJSONObject("data").getJSONArray("wallets")

                if (walletsArray.length() > 0) {
                    // Assuming there's only one wallet in the array, you can adjust if needed
                    val walletObject = walletsArray.getJSONObject(0)
                    walletadd2 = walletObject.getString("address")
                    println("Reciver :"+walletadd2)

                }
            } catch (e: Exception) {
                // Handle JSON parsing exception if needed
                println("Reciver problem")
                e.printStackTrace()
            }
            tokenid=apiManager.getUsdcTokenId(walletId1)
            withContext(Dispatchers.Main) {
                amtT.text = "$amt$currency"
                swalT.text =walletadd1
                rwalT.text=walletadd2
            }
        }
        val userDetails = sessionManager.getUserDetails()
        usertoken= userDetails[SessionManager.KEY_SESSION_TOKEN].toString()
        val encryptionkey = userDetails[SessionManager.KEY_ENCRYPTION_ID].toString()
        val convertbut:Button =findViewById(R.id.covertbutton)
        convertbut.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
            var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
            val jsonArray = JSONArray(idempotencyKey)
            idempotencyKey=jsonArray.optString(0).toString()
                try {
                    println("Transaction Parameters")
                    println(idempotencyKey.toString())
                    println(walletId1)
                    println(usertoken)
                    println("____________________")
                    val challid=apiManager.makeContractExecutionRequest(walletId1,usertoken,idempotencyKey)
                    println("Challenge ID = "+challid)
                    if(challid!=null)
                        try{
                            launchapi(usertoken,encryptionkey,challid)
                        }
                        catch (e:Exception){
                            println("Transaction failure : "+e)
                        }
                    println(" ")
                }
                catch (e:Exception){
                    println(e);

                }
            }
        }
        settlebut.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO) {
                var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
                val jsonArray = JSONArray(idempotencyKey)
                idempotencyKey=jsonArray.optString(0)
                idempotencyKey=idempotencyKey.toString()
                try {
                    println("Transaction Parameters")
                    println(listOf("2"))
                    println(idempotencyKey.toString())
                    println("Token id: "+tokenid)
                    println("Wallet id : "+walletId1)
                    println("Wallet Address :"+walletadd2)
                    println("Usertoken : "+usertoken)
                    println("____________________")
                    val challid=apiManager.makeTransfer("1",
                        idempotencyKey!!,tokenid,walletId1,walletadd2,usertoken)

                    println("Challenge ID = "+challid)
                    if(challid!=null)
                        try{
                        launchapi(usertoken,encryptionkey,challid)
                        }
                        catch (e:Exception){
                            println("Transaction failure : "+e)
                        }
                        println(" ")
                }
                catch (e:Exception){
                    println(e);

                }                }

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
                uid2 = firstUser.uid1.toString()
                uid1 = firstUser.uid2.toString()
                amt = firstUser.amount.toString()
                currency = firstUser.currency.toString()
                println("First user " + getwallet(uid1))
                println("2 user " + getwallet(uid2))
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
    private suspend fun launchapi(utoke:String,enkey:String,challid:String){
        val apiManager = ApiManager()
        var idempotencyKey = apiManager.makeGetRequest("https://www.uuidtools.com/api/generate/v4")
        val jsonArray = JSONArray(idempotencyKey)
        idempotencyKey=jsonArray.optString(0)
        try{
                val usertoken= utoke
                val encryptionkey = enkey
                println(usertoken)
                    if(usertoken!=null){
                        val inituser = apiManager.initializeUser(usertoken,idempotencyKey)
                        if(inituser!=null){
                            println(inituser)
                            val challengeid=challid
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
                                    WalletSdk.setViewSetterProvider(MyViewSetterProvider(c))
                                    WalletSdk.setLayoutProvider(MyLayoutProvider(c))

                                }



                            }
                        }}
        }
        catch (e:Exception){
            println(e)
        }
    }

    private inline fun initAndLaunchSdk(launchBlock: () -> Unit) {
        try {
            val settingsManagement = SettingsManagement()
            settingsManagement.isEnableBiometricsPin = false

            WalletSdk.init(
                applicationContext,
                WalletSdk.Configuration(
                    "https://api.circle.com/v1/w3s/",
                    "2f5d4fb7-fafd-59a3-b3a2-64de18dca1b9",
                    settingsManagement
                )
            )

        } catch (t: Throwable) {
            return
        }
        launchBlock()
    }

    override fun onEvent(p0: ExecuteEvent?) {
        startActivity(Intent(this@SettleupActivity, transSuccess::class.java))
    }

    override fun onError(p0: Throwable?): Boolean {
        println("Failed")
        println(p0)
        return false

    }

    override fun onWarning(p0: ExecuteWarning?, p1: ExecuteResult?): Boolean {
        println("Failed")
        println(p0)
        println(p1)
        startActivity(Intent(this@SettleupActivity, transSuccess::class.java))
        return false

    }

    override fun onResult(p0: ExecuteResult?) {
        startActivity(Intent(this@SettleupActivity, transSuccess::class.java))
    }
}