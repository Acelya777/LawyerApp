package com.acelya.lawyerapp.models

import android.location.Address

data class Client(
    val clientId: String = "",
    val name: String = "",
    val surname: String = "",
    val city: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val tc:String = "")
