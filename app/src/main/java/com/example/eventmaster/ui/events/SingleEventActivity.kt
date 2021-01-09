package com.example.eventmaster.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Ticket
import com.example.eventmaster.ui.tickets.TicketsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.Serializable
import java.lang.Integer.parseInt
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
        val authEmail = auth.currentUser?.email!!
        //Toast.makeText(this, extrasEvent.name, Toast.LENGTH_SHORT).show()
        val showNameComponent = findViewById<TextView>(R.id.textViewSingleEventName)
        showNameComponent.text = extrasEvent.name
        val showDescriptionComponent = findViewById<TextView>(R.id.textViewSingleEventDescription)
        showDescriptionComponent.text = extrasEvent.description
        val showDateComponent = findViewById<TextView>(R.id.textViewSingleEventDate)
        showDateComponent.text = extrasEvent.date.toString()
        val showLocationComponent = findViewById<TextView>(R.id.textViewSingleEventLocation)
        showLocationComponent.text = extrasEvent.location
        // taken places component
        val ticketNumberComponent = findViewById<EditText>(R.id.editNumberSingleEventTicketNumber)

        val buttonCancel = findViewById<Button>(R.id.buttonSingleEventCancel)
        buttonCancel.setOnClickListener{
            finish()
        }

        val buttonJoin = findViewById<Button>(R.id.buttonSingleEventJoin)
        buttonJoin.setOnClickListener{
            var amount = 0
            if (ticketNumberComponent.text.toString().length == 1) {
                amount = parseInt(ticketNumberComponent.text.toString())
                if (amount > 3 || amount < 1) {
                    ticketNumberComponent.error = "Można kupić od 1 do 3 biletów"
                } else {
                    checkJoinPossibilityAndJoin(extrasID as String, extrasEvent, authEmail, amount)
                }
            } else {
                ticketNumberComponent.error = "Można kupić od 1 do 3 biletów"
            }
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode: Int, data : Intent?) {
        // Success == 11
        // Failure == 7
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode && data != null)
            createTicket(data.extras?.get("eventId") as String, data.extras?.get("event") as Event, data.extras?.get("amount") as Int)
        else
            Toast.makeText(this, "Anulowano transakcję", Toast.LENGTH_SHORT).show()
    }

    private fun checkJoinPossibilityAndJoin(eventId : String, event : Event, email : String, amount : Int) {
        val ref = database.getReference("/Tickets")
        val context = this
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var ticketsAssigned = 0
                dataSnapshot.children.forEach {
                    val ticketObj = it.value as HashMap<*, *>
                    if (ticketObj["clientEmail"].toString() == email && ticketObj["eventId"].toString() == eventId)
                        ticketsAssigned++
                }
                if (checkUserLimit(ticketsAssigned, amount)) {
                    if (checkEventAvailability(event.participantNumber, event.takenPlaces, amount))
                        joinEvent(eventId, event, amount)
                    else
                        Toast.makeText(context, "Niestety, ale pozostało miejsc mniej niż: $amount", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(context, "Jeden użytkownik może posiadać maksymalnie 3 bilety na jedno wydarzenie. Masz już $ticketsAssigned.", Toast.LENGTH_SHORT).show()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Problem przy walidacji uzyskiwania biletów", Toast.LENGTH_SHORT).show()
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    private fun checkUserLimit(assigned : Int, amount : Int) : Boolean {
        if (assigned + amount > 3)
            return false
        return true
    }

    private fun checkEventAvailability(max : Int, current : Int, new : Int) : Boolean {
        if (max - current - new >= 0)
            return true
        return false
    }


    private fun joinEvent(eventId: String, event: Event, amount : Int) {
        if (eventId.isEmpty())
            return
        if (!event.paid) {
            createTicket(eventId, event, amount)
            return
        }

        // Paylike.io link parameters
        if (event.price == null) {
            Toast.makeText(this, "Coś poszło nie tak: błąd ceny biletu", Toast.LENGTH_SHORT).show()
            return
        }

        val price = event.price * 100 * amount // price
        val text = StringBuilder() // visible text
        for (i in event.name.indices) {
            if (event.name[i] == ' ')
                text.append("%20")
            else
                text.append(event.name[i])
        }
        val redirect = "https%3A%2F%2Fwww.paylike.io" // whatever
        val url = "https://pos.paylike.io/?key=9a2e2ab4-05a9-4f19-9b3b-b32351b7b02e&currency=PLN&amount=$price&reference=$eventId&text=$text&redirect=$redirect"
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("amount", amount)
        intent.putExtra("eventId", eventId)
        intent.putExtra("event", event as Serializable)

        startActivityForResult(intent, 11)

    }

    private fun createTicket(eventId : String, event : Event, amount : Int) {
        val ticketsRef = database.getReference("/Tickets")
        val ticket = Ticket(eventId, auth.currentUser?.email!!)
        val createdKey = generateTicketKey(event.date, event.name)
        for (i in 0 until amount) { // for more than one buy
            var key = ticketsRef.push().key
            if (key != null) {
                key = "$createdKey$key"
                ticketsRef.child(key).setValue(ticket).addOnCompleteListener {
                    val eventsRef = database.getReference("/Events")
                    eventsRef.child(eventId).child("takenPlaces").setValue(event.takenPlaces + amount).addOnCompleteListener{
                        Toast.makeText(this, "Dołączono do wydarzenia!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, TicketsActivity::class.java))
                        finish()
                    }
                }
            }
        }
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