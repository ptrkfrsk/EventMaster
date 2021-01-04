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
import com.example.eventmaster.models.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.lang.Boolean

class TicketsActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_tickets)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val buttonBack = findViewById<Button>(R.id.buttonMyTicketsBack)
        buttonBack.setOnClickListener{
            finish()
        }

        loadTicketsData()
    }

    private fun loadTicketsData() {
        val ref = database.getReference("/Tickets")
        val scrollLayout = findViewById<LinearLayout>(R.id.layoutScrollMyTickets)
        //val intent = Intent(this, TicketsActivity::class.java)
        val context = this
        val ticketList = arrayListOf<Ticket>()
        var counter = 0

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val ticketObj = it.value as HashMap<*, *>
                    val ticketId = it.key
                    val clientEmailFromTicket = ticketObj["clientEmail"].toString()
                    if (clientEmailFromTicket == auth.currentUser?.email) {
                        val ticket = Ticket(
                                eventId = ticketObj["eventId"].toString(),
                                clientEmail = clientEmailFromTicket
                        )
                        ticketList.add(ticket)
                        val singleTicketLayout = LinearLayout(context)
                        singleTicketLayout.setPadding(0, 30, 0, 30)
                        singleTicketLayout.tag = counter
                        val textViewSingleTicket = TextView(context)
                        textViewSingleTicket.text = ticketId
                        textViewSingleTicket.textSize = 20f
                        textViewSingleTicket.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        singleTicketLayout.addView(textViewSingleTicket)
                        singleTicketLayout.setOnClickListener{
                            passTicketObject(ticketList[singleTicketLayout.tag as Int])
                        }
                        scrollLayout.addView(singleTicketLayout)
                    }
                    counter++
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    private fun passTicketObject(ticket : Ticket) {
        val intent = Intent(this, SingleTicketActivity::class.java)
        intent.putExtra("ticket", ticket as Serializable)
        startActivity(intent)
    }
}