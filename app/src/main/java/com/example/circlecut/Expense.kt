package com.example.circlecut

import java.util.Currency

// Expense.kt
data class Expense(val name: String, val description: String,val nameletter:String, val cost: Double?=null,val currency: String,val exid:Long?=null)
