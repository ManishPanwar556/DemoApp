package com.example.demoapp.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeFormat {
    fun formatTime(time:Long):String{
        val format= SimpleDateFormat("hh:mm a")
        val date= Date(time)
        return format.format(date)
    }
}