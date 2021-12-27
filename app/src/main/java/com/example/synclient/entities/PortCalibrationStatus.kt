package com.example.synclient.entities

import java.io.Serializable

data class PortCalibrationStatus(
    var open:Boolean,
    var short: Boolean,
    var load: Boolean,
    var thru: Boolean

) : Serializable