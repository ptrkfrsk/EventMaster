package com.example.eventmaster.ui.tickets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R

class SingleTicketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_ticket)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, TicketsActivity::class.java))
    }
}