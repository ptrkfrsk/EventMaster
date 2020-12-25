package com.example.eventmaster.models
import java.time.LocalDateTime

class Event(
    val name: String,
    val description: String,
    val date: LocalDateTime?,
    val location: String,
    val participantNumber: Int,
    val private: Boolean,
    val paid: Boolean,
    val price: Double?) {
}