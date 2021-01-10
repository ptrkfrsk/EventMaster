package com.example.eventmaster.ui.events

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.checkOnlineConnection
import com.example.eventmaster.models.Event
import com.google.firebase.database.FirebaseDatabase
import java.lang.Integer.parseInt
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_add_event)
        database = FirebaseDatabase.getInstance();

        val buttonAdd = findViewById<Button>(R.id.buttonAddEventAdd);
        buttonAdd.setOnClickListener{
            if (checkOnlineConnection(this))
                addEventToDatabase(database, buttonAdd)
            else
                Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
        }

        val buttonCancel = findViewById<Button>(R.id.buttonAddEventCancel);
        buttonCancel.setOnClickListener{
            finish()
        }

        val isPaidComponent = findViewById<CheckBox>(R.id.checkBoxAddEventPaid)
        val priceComponent = findViewById<EditText>(R.id.editDecimalAddEventPrice)
        val priceComponentLabel = findViewById<TextView>(R.id.textViewAddEventPrice)

        isPaidComponent.setOnClickListener{
            priceComponent.isEnabled = isPaidComponent.isChecked
            priceComponentLabel.isEnabled = isPaidComponent.isChecked

            // Disabled/enabled styles
            if (isPaidComponent.isChecked) {
                priceComponentLabel.setTextColor(Color.parseColor("#000000"))
                priceComponent.background =  ContextCompat.getDrawable(this, R.drawable.my_edit_bg)
            } else {
                priceComponentLabel.setTextColor(Color.parseColor("#aaaaaa"))
                priceComponent.error = null
                priceComponent.background =  ContextCompat.getDrawable(this, R.drawable.my_disabled_edit_bg)
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun addEventToDatabase(database: FirebaseDatabase, button : Button) {
        val nameComponent = findViewById<EditText>(R.id.editTextAddEventName)
        val descriptionComponent = findViewById<EditText>(R.id.editMultilineAddEventDescription)
        val dateComponent = findViewById<EditText>(R.id.editTextAddEventDate)
        val locationComponent = findViewById<EditText>(R.id.editTextAddEventPlace)
        val participantNumberComponent = findViewById<EditText>(R.id.editNumberAddEventAmount)
        val isPaidComponent = findViewById<CheckBox>(R.id.checkBoxAddEventPaid)
        val priceComponent = findViewById<EditText>(R.id.editDecimalAddEventPrice)

        // Validation
        var eventPrice : Double? = null
        var isBlocked = false
        if (nameComponent.text.isNullOrEmpty()) {
            nameComponent.error = "Pusta nazwa"
            isBlocked = true
        }

        if (descriptionComponent.text.isNullOrEmpty()) {
            descriptionComponent.error = "Pusty opis"
            isBlocked = true
        }
        if (locationComponent.text.isNullOrEmpty()) {
            locationComponent.error = "Pusta lokalizacja"
            isBlocked = true
        }
        if (participantNumberComponent.text.isNullOrEmpty() || parseInt(participantNumberComponent.text.toString()) <= 0) {
            participantNumberComponent.error = "Zła ilość uczestników"
            isBlocked = true
        }
        if (isPaidComponent.isChecked) {
            if (priceComponent.text.isNullOrEmpty()) {
                priceComponent.error = "Podaj cenę biletu"
                isBlocked = true
            } else {
                eventPrice = priceComponent.text.toString().toDouble()
            }
        }

        val dateString = dateComponent.text.toString()
         try {
             SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(dateString)
         } catch (exception : ParseException) {
             dateComponent.error = "Zły format lub brak daty"
             isBlocked = true
         } finally {
             if (dateString.length != 16 || dateString[4] != '-' || dateString[7] != '-' || dateString[13] != ':') {
                 dateComponent.error = "Zły format lub brak daty"
                 isBlocked = true
             } else {
                 if (parseInt(dateString.substring(5, 7)) < 1 || parseInt(dateString.substring(5, 7)) > 12
                         || parseInt(dateString.substring(8, 10)) < 1 || parseInt(dateString.substring(8, 10)) > 31
                         || parseInt(dateString.substring(11, 13)) < 0 || parseInt(dateString.substring(11, 13)) > 23
                         || parseInt(dateString.substring(14)) < 0 || parseInt(dateString.substring(14)) > 59) {
                     dateComponent.error = "Zły format daty"
                     isBlocked = true
                 }
             }
         }

        if (isBlocked)
            return

        button.isEnabled = false
        val event = Event(
                nameComponent.text.toString(),
                descriptionComponent.text.toString(),
                dateComponent.text.toString(),
                locationComponent.text.toString(),
                participantNumberComponent.text.toString().toInt(),
                0,
                isPaidComponent.isChecked,
                eventPrice
        )

        val eventsRef = database.getReference("/Events");
        val key = eventsRef.push().key
        if (key != null) {
            eventsRef.child(key).setValue(event).addOnCompleteListener{
                Toast.makeText(this, "Dodano do bazy", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else
            button.isEnabled = true
    }
}