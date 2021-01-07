package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.eventmaster.LoginActivity
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Person
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.Serializable
import java.lang.Boolean.parseBoolean
import java.lang.Integer.parseInt
import java.time.LocalDateTime

class SearchEventActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_search_event)

        database = FirebaseDatabase.getInstance()

        val buttonBack = findViewById<Button>(R.id.buttonSearchEventsBack)
        buttonBack.setOnClickListener{
            finish()
        }

        loadEventsData()
    }

    private fun loadEventsData() {
        val ref = database.getReference("Events")
        val scrollLayout = findViewById<LinearLayout>(R.id.layoutScrollSearchEvent)
        val intent = Intent(this, SingleEventActivity::class.java)
        val context = this
        val eventList = arrayListOf<Event>()

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var counter = 0
                dataSnapshot.children.forEach {
                    val eventObj = it.value as HashMap<*, *>
                    val eventId = it.key
                    val event = Event(
                        name = eventObj["name"].toString(),
                        description = eventObj["description"].toString(),
                        date = eventObj["date"].toString(),
                        location = eventObj["location"].toString(),
                        participantNumber =  parseInt(eventObj["participantNumber"].toString()),
                        paid = parseBoolean(eventObj["paid"].toString()),
                        price = eventObj["price"].toString().toDoubleOrNull(),
                    )
                    eventList.add(event)
                    val singleEventLayout = LinearLayout(context)
                    singleEventLayout.setPadding(0,30,0,50)
                    singleEventLayout.tag = counter
                    singleEventLayout.orientation = LinearLayout.VERTICAL
                    var textViewSingleEvent = TextView(context)
                    textViewSingleEvent.text = event.name
                    textViewSingleEvent.textSize = 23f
                    textViewSingleEvent.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    singleEventLayout.addView(textViewSingleEvent)
                    textViewSingleEvent = TextView(context)
                    textViewSingleEvent.text = event.date
                    textViewSingleEvent.textSize = 18f
                    textViewSingleEvent.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    singleEventLayout.addView(textViewSingleEvent)
                    singleEventLayout.setOnClickListener{
                        passEventObject(intent, eventList[singleEventLayout.tag as Int], eventId as String)
                    }
                    scrollLayout.addView(singleEventLayout)
                    counter++
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    private fun createSingleEventComponent() {

    }

    private fun passEventObject(intent : Intent, event : Event, eventId : String) {
        //Toast.makeText(this, event.name, Toast.LENGTH_SHORT).show()
        intent.putExtra("event", event as Serializable)
        intent.putExtra("eventId",  eventId)

        startActivity(intent)
    }
}