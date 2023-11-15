package com.example.circlecut
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Expensegroup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homegroup)

        // Fetch expense data (replace this with your data fetching logic)
        val expenses = getDummyExpenseData()
        val frnd: TextView =findViewById(R.id.friends)
        val avty: TextView = findViewById(R.id.activity)
        avty.setOnClickListener {

            startActivity(Intent(this@Expensegroup, Expenseavty::class.java))
        }
        frnd.setOnClickListener {

            startActivity(Intent(this@Expensegroup, ExpenseActivity::class.java))
        }

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExpenseAdapter(expenses)
    }

    private fun getDummyExpenseData(): List<Expense> {
        // Replace this with your data fetching logic
        return listOf(
            Expense("Hong kong trip", "You owe","H", 500),
            Expense("Flatmates", "You owe","F", 10),
            // Add more expense items as needed
        )
    }
}