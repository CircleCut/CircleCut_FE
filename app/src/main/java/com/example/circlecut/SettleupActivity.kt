package com.example.circlecut

import ExpenseD
import SessionManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettleupActivity:AppCompatActivity() {
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
}