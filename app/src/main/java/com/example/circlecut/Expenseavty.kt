package com.example.circlecut
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Expenseavty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeavty)

        // Fetch expense data (replace this with your data fetching logic)
        val expenses = getDummyExpenseData()
        val frnd: TextView =findViewById(R.id.friends)
        val group: TextView = findViewById(R.id.group)
        val floatbutton : FloatingActionButton = findViewById(R.id.floatingActionButton2)
        floatbutton.setOnClickListener{
            startActivity(Intent(this@Expenseavty,AddExpense::class.java))
        }
        group.setOnClickListener {

            startActivity(Intent(this@Expenseavty, Expensegroup::class.java))
        }
        frnd.setOnClickListener {

            startActivity(Intent(this@Expenseavty, ExpenseActivity::class.java))
        }
        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ExpenseAdapter(expenses)
    }

    private fun getDummyExpenseData(): List<Expense> {
        // Replace this with your data fetching logic
        return listOf(
            Expense("Adriel", "Dinner - You owe","H", 500), // Add more expense items as needed
            Expense("Gabriel", "Settled Up","H", 100), // Add more expense items as needed
        )
    }
}