package com.example.eventmaster.models
import java.io.Serializable

class Ticket(
        val eventId : String,
        val clientEmail : String) : Serializable
{}