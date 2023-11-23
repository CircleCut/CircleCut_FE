package com.example.circlecut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        // Bind your data to the views here
        holder.nameTextView.text = expense.name
        holder.currencyTextView.text = expense.currency
        holder.descriptionTextView.text = expense.description
        holder.costTextView.text = expense.cost.toString()
        holder.nameLetterView.text = expense.nameletter

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.owe)
        val costTextView: TextView = itemView.findViewById(R.id.cost)
        val nameLetterView: TextView = itemView.findViewById(R.id.nameletter)
        val currencyTextView: TextView = itemView.findViewById(R.id.currency)
    }
}
