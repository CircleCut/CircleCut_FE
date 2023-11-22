package com.example.circlecut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendsExpense : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends_expense, container, false)
        // Inflate the layout for this fragment
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val expenses = getDummyExpenseData()
        recyclerView.adapter = ExpenseAdapter(expenses)
        return view
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