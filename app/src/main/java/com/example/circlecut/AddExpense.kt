package com.example.circlecut

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class AddExpense : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addexpense)
        val back:ImageView = findViewById(R.id.back)
        val currencyspin: Spinner = findViewById(R.id.currency)
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
        val datespin: Spinner = findViewById(R.id.date)
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
        val personspin: Spinner = findViewById(R.id.person)
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