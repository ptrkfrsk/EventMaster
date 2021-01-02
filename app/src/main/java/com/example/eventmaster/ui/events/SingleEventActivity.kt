package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.ui.tickets.TicketsActivity

class SingleEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_single_event)

        val buttonCancel = findViewById<Button>(R.id.buttonSingleEventCancel)
        buttonCancel.setOnClickListener{
            startActivity(Intent(this, SearchEventActivity::class.java))
        }

        val buttonJoin = findViewById<Button>(R.id.buttonSingleEventJoin)
        buttonJoin.setOnClickListener{
            Toast.makeText(this, "Dołączono!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TicketsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, SearchEventActivity::class.java))
    }
}