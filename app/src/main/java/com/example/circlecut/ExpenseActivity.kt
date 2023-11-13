package com.example.circlecut
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class ExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Fetch expense data (replace this with your data fetching logic)
        val expenses = getDummyExpenseData()

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