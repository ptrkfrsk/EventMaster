package com.example.eventmaster.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Ticket
import com.example.eventmaster.ui.tickets.TicketsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
            finish()
        }

        val buttonJoin = findViewById<Button>(R.id.buttonSingleEventJoin)
        buttonJoin.setOnClickListener{
            val amount = 1
            createTicketAndJoin(extrasID as String, extrasEvent, auth.currentUser?.email as String, amount)
        }
    }

    private fun createTicketAndJoin(eventId: String, event: Event, clientEmail: String, amount : Int) {
        if (eventId.isEmpty() || clientEmail.isEmpty())
            return
        val intent = Intent(this, PaymentActivity::class.java)
        val price = 23 * 100
        val ticketId = "qwerty"
        val text = "Testowy%20opis"
        val redirect = "https%3A%2F%2Fwww.paylike.io"
        val url = "https://pos.paylike.io/?key=9a2e2ab4-05a9-4f19-9b3b-b32351b7b02e&currency=PLN&amount=$price&reference=$ticketId&text=$text&redirect=$redirect"
        intent.putExtra("url", url)
//        val ticketsRef = database.getReference("/Tickets")
//        val ticket = Ticket(eventId, clientEmail)
//        val createdKey = generateTicketKey(event.date, event.name)
//        for (i in 0..amount-1) { // for more than one buy
//            var key = ticketsRef.push().key
//            if (key != null) {
//                key = "$createdKey$key"
//                ticketsRef.child(key).setValue(ticket).addOnCompleteListener {
//                    Toast.makeText(this, "Dołączono do wydarzenia!", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, PaymentActivity::class.java))
//                }
//            }
//        }
        startActivityForResult(intent, 11)

    }

    override fun onActivityResult(requestCode : Int, resultCode: Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode)
            Toast.makeText(this, "Brawo!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Nie!", Toast.LENGTH_SHORT).show()
    }

    private fun generateTicketKey(dateTimeString: String, name: String) : String? {
        val key = StringBuilder()
        for (i in dateTimeString.indices) {
            if (dateTimeString[i] == ':' || dateTimeString[i] == '-')
                key.append("")
            else if (dateTimeString[i] == ' ')
                key.append('_')
            else
                key.append(dateTimeString[i])
        }
        key.append('_')

        for (i in name.indices) {
            if (name[i].isUpperCase()) {
                key.append(name[i])
            }
        }
        return key.toString()
    }
}