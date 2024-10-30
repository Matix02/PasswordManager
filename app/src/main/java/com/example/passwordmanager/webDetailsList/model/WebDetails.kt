package com.example.passwordmanager.webDetailsList.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebDetails(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val icon: String = "",
    val belongToAdmin: Boolean = false,
    val shouldExpand: Boolean = false,
) : Parcelable