package com.example.eventmaster.ui.tickets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.checkOnlineConnection
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.lang.Boolean
import java.lang.StringBuilder

class TicketsActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); // will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_tickets)

        if (!checkOnlineConnection(this))
            Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val buttonBack = findViewById<Button>(R.id.buttonMyTicketsBack)
        buttonBack.setOnClickListener{
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }

        loadTicketsData()
    }

    private fun loadTicketsData() {
        val ref = database.getReference("/Tickets")
        val scrollLayout = findViewById<LinearLayout>(R.id.layoutScrollMyTickets)
        val context = this
        val ticketList = arrayListOf<Ticket>()
        var counter = 0

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var order = 0
                dataSnapshot.children.forEach {
                    val ticketObj = it.value as HashMap<*, *>
                    val ticketId = it.key
                    val clientEmailFromTicket = ticketObj["clientEmail"].toString()
                    if (clientEmailFromTicket == auth.currentUser?.email) {
                        order++
                        val ticket = Ticket(
                                eventId = ticketObj["eventId"].toString(),
                                clientEmail = clientEmailFromTicket
                        )
                        ticketList.add(ticket)
                        val singleTicketLayout = LinearLayout(context)
                        singleTicketLayout.setPadding(0, 30, 0, 30)
                        singleTicketLayout.tag = counter
                        singleTicketLayout.orientation = LinearLayout.VERTICAL
                        var textViewSingleTicket = TextView(context)
                        val id2Show = StringBuilder()
                        for (i in ticketId!!.indices) {
                            if (ticketId[i] == '-')
                                break
                            id2Show.append(ticketId[i])
                        }
                        textViewSingleTicket.text = id2Show
                        textViewSingleTicket.textSize = 23f
                        textViewSingleTicket.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textViewSingleTicket.typeface = ResourcesCompat.getFont(context, R.font.advent_pro_semibold);
                        textViewSingleTicket.setPadding(15, 5, 15, 5)
                        textViewSingleTicket.background = ResourcesCompat.getDrawable(resources, R.drawable.my_single_layout_ticket_bg, null)
                        singleTicketLayout.addView(textViewSingleTicket)
                        textViewSingleTicket = TextView(context)
                        val text = "Bilet nr $order"
                        textViewSingleTicket.text = text
                        textViewSingleTicket.textSize = 18f
                        textViewSingleTicket.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textViewSingleTicket.typeface = ResourcesCompat.getFont(context, R.font.advent_pro_medium);
                        singleTicketLayout.addView(textViewSingleTicket)
                        singleTicketLayout.setOnClickListener{
                            passTicketObject(ticketList[singleTicketLayout.tag as Int])
                        }
                        scrollLayout.addView(singleTicketLayout)
                        counter++
                    }
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

    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}