package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Ticket
import com.example.eventmaster.ui.tickets.TicketsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime

class SingleEventActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_single_event)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val extrasEvent = intent.extras?.get("event") as Event
        val extrasID = intent.extras?.get("eventId")
        val authEmail = auth.currentUser?.email
        //Toast.makeText(this, extrasEvent.name, Toast.LENGTH_SHORT).show()
        val showNameComponent = findViewById<TextView>(R.id.textViewSingleEventName)
        showNameComponent.text = extrasEvent.name
        val showDescriptionComponent = findViewById<TextView>(R.id.textViewSingleEventDescription)
        showDescriptionComponent.text = extrasEvent.description
        val showDateComponent = findViewById<TextView>(R.id.textViewSingleEventDate)
        showDateComponent.text = extrasEvent.date.toString()
        val showLocationComponent = findViewById<TextView>(R.id.textViewSingleEventLocation)
        showLocationComponent.text = extrasEvent.location


        val buttonCancel = findViewById<Button>(R.id.buttonSingleEventCancel)
        buttonCancel.setOnClickListener{
            startActivity(Intent(this, SearchEventActivity::class.java))
        }

        val buttonJoin = findViewById<Button>(R.id.buttonSingleEventJoin)
        buttonJoin.setOnClickListener{
            createTicketAndJoin(extrasID as String, auth.currentUser?.email as String)
        }
    }

    private fun createTicketAndJoin(eventId : String, clientEmail : String) {
        if (eventId.isEmpty() || clientEmail.isEmpty())
            return
        val ticketsRef = database.getReference("/Tickets")
        val ticket = Ticket(eventId, clientEmail)
        val key = ticketsRef.push().key
        if (key != null) {
            ticketsRef.child(key).setValue(ticket).addOnCompleteListener{
                Toast.makeText(this, "Dołączono do wydarzenia!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, TicketsActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SearchEventActivity::class.java))
    }
}