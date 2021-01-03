package com.example.eventmaster.ui.tickets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SingleTicketActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_single_ticket)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val textViewEventName = findViewById<TextView>(R.id.textViewSingleTIcketEventName)
        val ticket = intent.extras?.get("ticket") as Ticket
        textViewEventName.text = ticket.eventId

    }

    override fun onBackPressed() {
        startActivity(Intent(this, TicketsActivity::class.java))
    }
}