package com.example.synclient.entities

import java.io.Serializable

data class ItemChannel(
    var name: String,
    var isActive: Boolean
) : Serializable