package com.example.circlecut

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("uid")
    val uid: Int,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("number")
    val number: String,

    @SerialName("wallet")
    val wallet: String,

    @SerialName("password")
    val password: String
)
