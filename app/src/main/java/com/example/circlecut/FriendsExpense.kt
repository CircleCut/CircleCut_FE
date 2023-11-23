package com.example.circlecut

import ExpenseD
import SessionManager
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsExpense : Fragment() {
    private val supabase = supabaseinit().getsupa(R.string.supabaseurl.toString(), R.string.supabasekey.toString())
    private lateinit var userid: String
    private lateinit var sessionManager: SessionManager
    private val mainScope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends_expense, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        sessionManager = SessionManager(requireContext())

        if (sessionManager.checkLogin()) {
            val userDetails = sessionManager.getUserDetails()
            userid = userDetails[SessionManager.KEY_USER_ID].toString()
            println("urid" + userid)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mainScope.launch {
            var expense = getOwed(userid)
            var owings = getOwing(userid)
            val expenses = expense + owings
            recyclerView.adapter = ExpenseAdapter(expenses){ position ->
                // Handle item click
                val clickedExpenseId = expenses[position].exid

                // Create an Intent to start the new activity
                val intent = Intent(requireContext(), SettleupActivity::class.java).apply {
                    putExtra("EXPENSE_ID", clickedExpenseId)
                }

                // Start the new activity
                startActivity(intent)
            }
        }

        return view
    }

    private suspend fun getOwed(id: String): List<Expense> = withContext(Dispatchers.IO) {
        try {
            val result = supabase.postgrest["expenses"].select(columns = Columns.list()) {
                eq("uid1", id)
            }.decodeList<ExpenseD>()

            if (result.isNotEmpty()) {
                return@withContext result.map { supabaseExpense ->
                    Expense(
                        name = supabaseExpense.name ?: "",
                        description = "Owes you",
                        nameletter = "G",
                        cost = supabaseExpense.amount,
                        currency = supabaseExpense.currency.toString(),
                        exid = supabaseExpense.expenseId

                    )
                }
            } else {
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    private suspend fun getOwing(id: String): List<Expense> = withContext(Dispatchers.IO) {
        try {
            val result = supabase.postgrest["expenses"].select(columns = Columns.list()) {
                eq("uid2", id)
            }.decodeList<ExpenseD>()

            if (result.isNotEmpty()) {
                return@withContext result.map { supabaseExpense ->
                    Expense(
                        name = supabaseExpense.name ?: "",
                        description = "You Owe",
                        nameletter = "G",
                        cost = supabaseExpense.amount,
                        currency = supabaseExpense.currency.toString(),
                        exid = supabaseExpense.expenseId
                        // Map other fields accordingly
                        // For example, "You owe", "Owes you" logic can be handled here
                    )
                }
            } else {
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel() // Cancel the coroutine scope to avoid memory leaks
    }
}
