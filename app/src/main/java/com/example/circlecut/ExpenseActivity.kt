package com.example.circlecut
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseActivity : AppCompatActivity() {
    // Inside any other activity or fragment where you want to retrieve the user ID
    val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val userId = sharedPrefs.getString("userId", "") // Default value "" will be used if userId is not found
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        // Fetch expense data (replace this with your data fetching logic)
        val expenses = getDummyExpenseData()
        val avty:TextView =findViewById(R.id.activity)
        val group: TextView = findViewById(R.id.group)
        val floatbutton : FloatingActionButton = findViewById(R.id.floatingActionButton2)

        floatbutton.setOnClickListener{
            startActivity(Intent(this@ExpenseActivity,AddExpense::class.java))
        }
        group.setOnClickListener {

            startActivity(Intent(this@ExpenseActivity, Expensegroup::class.java))
        }
        avty.setOnClickListener {

            startActivity(Intent(this@ExpenseActivity, Expenseavty::class.java))
        }

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExpenseAdapter(expenses)
    }

    private fun getDummyExpenseData(): List<Expense> {
        // Replace this with your data fetching logic
        return listOf(
            Expense("Adriel", "You owe","A", 300),
            Expense("Romario", "You owe","R", 300),
            Expense("Gabriel", "Owes you","G", 100),
            Expense("Romario", "You owe","R", 300),
            Expense("Adriel", "You owe","A", 300),
            // Add more expense items as needed
        )
    }
}