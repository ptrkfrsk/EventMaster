package com.example.eventmaster.ui.tickets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Person
import com.example.eventmaster.models.Ticket
import com.example.eventmaster.ui.events.SingleEventActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.lang.Boolean

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

        val ticket = intent.extras?.get("ticket") as Ticket

        val intentEvent = Intent(this, SingleEventActivity::class.java) // for the sake of go to specific event transition
        loadProperEvent(ticket.eventId, intentEvent)

        val buttonBack = findViewById<Button>(R.id.buttonSingleTicketCancel)
        buttonBack.setOnClickListener{
            startActivity(Intent(this, TicketsActivity::class.java))
        }

    }

    private fun loadProperEvent(id : String, intent: Intent) {
        val ref = database.getReference("/Events")
        var returnObject : Event? = null
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val eventObj = it.value as HashMap<*, *>
                    val eventId = it.key
                    if (eventId == id) {
                        val textViewEventName = findViewById<TextView>(R.id.textViewSingleTIcketEventName)
                        val event = Event(
                                name = eventObj["name"].toString(),
                                description = eventObj["description"].toString(),
                                date = eventObj["date"].toString(),
                                location = eventObj["location"].toString(),
                                participantNumber = Integer.parseInt(eventObj["participantNumber"].toString()),
                                paid = Boolean.parseBoolean(eventObj["paid"].toString()),
                                price = null
                        )

                        val buttonGoToEvent = findViewById<Button>(R.id.buttonSingleTicketGoToEvent)
                        buttonGoToEvent.setOnClickListener{
                            intent.putExtra("event", event as Serializable)
                            intent.putExtra("eventId",  eventId)
                            startActivity(intent)
                        }

                        textViewEventName.text = event.name
                        return@forEach
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, TicketsActivity::class.java))
    }
}