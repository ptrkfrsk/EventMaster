package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.eventmaster.LoginActivity
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Event
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_add_event)
        val database = FirebaseDatabase.getInstance();

        val buttonAdd = findViewById<Button>(R.id.buttonAddEventAdd);
        buttonAdd.setOnClickListener{
            addEventToDatabase(database)
        }

        val buttonCancel = findViewById<Button>(R.id.buttonAddEventCancel);
        buttonCancel.setOnClickListener{
            updateUI()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun addEventToDatabase(database: FirebaseDatabase) {
        val nameComponent = findViewById<EditText>(R.id.editTextAddEventName)
        val descriptionComponent = findViewById<EditText>(R.id.editMultilineAddEventDescription)
        val dateComponent = findViewById<EditText>(R.id.editTextAddEventDate)
        val locationComponent = findViewById<EditText>(R.id.editTextAddEventPlace)
        val participantNumberComponent = findViewById<EditText>(R.id.editNumberAddEventAmount)
        val isPrivateComponent = findViewById<CheckBox>(R.id.checkBoxAddEventPrivate)
        val isPaidComponent = findViewById<CheckBox>(R.id.checkBoxAddEventPaid)
        val priceComponent = findViewById<EditText>(R.id.editDecimalAddEventPrice)

        if (
            nameComponent.text.isNullOrEmpty() ||
            descriptionComponent.text.isNullOrEmpty() ||
            locationComponent.text.isNullOrEmpty() ||
            participantNumberComponent.text.isNullOrEmpty()
        ) {
            Toast.makeText(this, "Puste pola - nie można dodać wydarzenia", Toast.LENGTH_SHORT).show()
            return
        }


        val event = Event(
            nameComponent.text.toString(),
            descriptionComponent.text.toString(),
            null,
            locationComponent.text.toString(),
            participantNumberComponent.text.toString().toInt(),
            isPrivateComponent.isChecked,
            isPaidComponent.isChecked,
            null
        )

        val eventsRef = database.getReference("/Events");
        val key = eventsRef.push().key
        if (key != null) {
            eventsRef.child(key).setValue(event).addOnCompleteListener{
                Toast.makeText(this, "Dodano do bazy", Toast.LENGTH_SHORT).show()
            }
        }
        //eventsRef.setValue(event)
        updateUI()
    }

    private fun updateUI() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}