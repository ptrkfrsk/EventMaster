package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
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
import java.lang.Boolean.parseBoolean
import java.lang.Integer.parseInt
import java.time.LocalDateTime

class SearchEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_search_event)

//        val scrollLayout = findViewById<LinearLayout>(R.id.layoutScrollSearchEvent)
//        for (i in 1..100) {
//            val singleEventLayout = LinearLayout(this)
//            singleEventLayout.setPadding(0,30,0,30)
//            val textViewSingleEvent = TextView(this)
//            textViewSingleEvent.text = "Nazwa wydarzenia\n01.01.2021"
//            textViewSingleEvent.textSize = 20f
//            textViewSingleEvent.textAlignment = View.TEXT_ALIGNMENT_CENTER
//            singleEventLayout.addView(textViewSingleEvent)
//            singleEventLayout.setOnClickListener{
//                startActivity(Intent(this, SingleEventActivity::class.java))
//            }
//            scrollLayout.addView(singleEventLayout)
//        }
        loadEventsData()

    }

    private fun loadEventsData() {
        val ref = FirebaseDatabase.getInstance().getReference("Events")
        val scrollLayout = findViewById<LinearLayout>(R.id.layoutScrollSearchEvent)
        val intent = Intent(this, SingleEventActivity::class.java)
        val context = this

        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var person: Person? = null
                var event : Event? = null
                dataSnapshot.children.forEach {
                    val eventObj = it.value as HashMap<*, *>
//                    event = Event(
//                        name = eventObj["name"].toString(),
//                        description = eventObj["description"].toString(),
//                        date = null,/*eventObj["date"].toString(),*/
//                        location = eventObj["location"].toString(),
//                        participantNumber =  parseInt(eventObj["participantNumber"].toString()),
//                        private = parseBoolean(eventObj["private"].toString()),
//                        paid = parseBoolean(eventObj["paid"].toString()),
//                    )
                    val singleEventLayout = LinearLayout(context)
                    singleEventLayout.setPadding(0,30,0,30)
                    val textViewSingleEvent = TextView(context)
                    textViewSingleEvent.text = eventObj["name"].toString()
                    textViewSingleEvent.textSize = 20f
                    textViewSingleEvent.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    singleEventLayout.addView(textViewSingleEvent)
                    singleEventLayout.setOnClickListener{
                        startActivity(intent)
                    }
                    scrollLayout.addView(singleEventLayout)
                }
            }

//            class Event(
//                val name: String,
//                val description: String,
//                val date: LocalDateTime?,
//                val location: String,
//                val participantNumber: Int,
//                val private: Boolean,
//                val paid: Boolean,
//                val price: Double?) {
//            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }

    private fun createSingleEventComponent() {

    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}