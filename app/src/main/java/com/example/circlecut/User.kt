package com.example.circlecut

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("uid")
    val uid: Int? = null,  // Make uid nullable for it to be optional

    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("name")
    val name: String?=null,

    @SerialName("email")
    val email: String?=null,

    @SerialName("number")
    val number: String?=null,

    @SerialName("password")
    val password: String?=null
)
