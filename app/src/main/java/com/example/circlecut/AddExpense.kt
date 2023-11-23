package com.example.circlecut

import ExpenseD
import SessionManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Currency

class AddExpense : AppCompatActivity() {
    lateinit var currencyspin: Spinner
    lateinit var datespin: Spinner
    lateinit var personspin: Spinner
    lateinit var person:String
    lateinit var uid2:String
    lateinit var sessionManager:SessionManager
    lateinit var uid1:String
    lateinit var currency: String
    lateinit var wen:String

    val supabase=supabaseinit().getsupa(R.string.supabaseurl.toString(),R.string.supabasekey.toString())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addexpense)
        sessionManager = SessionManager(this)
        if (sessionManager.checkLogin()) {
            val userDetails = sessionManager.getUserDetails()
            uid1= userDetails[SessionManager.KEY_USER_ID].toString()
        }
        spinners()
        personspin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position).toString()
                person =selectedItem
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }
        currencyspin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position).toString()
                currency =selectedItem
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }
        datespin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position).toString()
                wen =selectedItem
            }
            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }


        val done:Button = findViewById(R.id.settleButton)
        val expnse:EditText=findViewById(R.id.expnse)
        val amt:EditText=findViewById(R.id.amount)
        done.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO) {
                if(person!=null&&expnse.text.toString()!=null&&amt.text.toString()!=null){
                    uid2=getUid(person)
                    println(uid1)
                    println(uid2)
                    println(currency)
                    println(expnse.text)
                    println(amt.text)
                    println(wen)
                    addexpense(uid1,uid2,currency,expnse.text.toString(),amt.text.toString().toFloat(),wen)
                    startActivity(Intent(this@AddExpense,ExpenseActivity::class.java))

                }
            }
        }

    }
    suspend fun getUid(name:String):String{
        var pass=""
        try{
            val result = supabase.postgrest["users"].select(columns = Columns.list("uid")){
                eq("name", name)
            }.decodeList<User>()
            println(result)
            if (result.isNotEmpty()) {
                val firstUser: User = result.first()
                pass = firstUser.uid.toString()
                return pass
            } else {
                return "Username Not Found"
            }
        }
        catch (e:Exception){
            return "Username Not Found ${e.toString()}"

        }
    }
    private suspend fun addexpense(uid1:String,uid2: String,currency:String,desc:String,amt:Float,wen:String){
        val expns= ExpenseD(
            uid1 = uid1.toInt(),
            uid2 = uid2.toInt(),
            currency = currency,
            name = desc,
            amount = amt.toDouble(),
            paidby = uid1.toInt(),
            whenDate = wen
        )
        try{
            var json = Json { ignoreUnknownKeys = true }
            val result = supabase.postgrest["expenses"].insert(expns).decodeList<ExpenseD>()
            println("reultttt $result")
        }
        catch (e:Exception){
            println(e)
        }
    }
    fun spinners(){
        val back:ImageView = findViewById(R.id.back)
        currencyspin = findViewById(R.id.currency)
// Create an ArrayAdapter using the string array and a default spinner layout.
        back.setOnClickListener{
            startActivity(Intent(this@AddExpense,ExpenseActivity::class.java))

        }
        ArrayAdapter.createFromResource(
            this,
            R.array.Currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            currencyspin.adapter = adapter
        }
        datespin = findViewById(R.id.date)
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.Date,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            datespin.adapter = adapter
        }
        personspin = findViewById(R.id.person)
// Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.Friends,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            personspin.adapter = adapter
        }
    }
}