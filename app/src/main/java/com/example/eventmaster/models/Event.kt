package com.example.eventmaster.models
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

class Event(
        val name: String,
        val description: String,
        val date: String,
        val location: String,
        val participantNumber: Int,
        val paid: Boolean,
        val price: Double?) : Serializable {
}