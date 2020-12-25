package com.example.eventmaster.ui.tickets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eventmaster.LoginActivity
import com.example.eventmaster.R
import com.google.firebase.auth.FirebaseAuth

class TicketsFragment : Fragment() {

    private lateinit var ticketsViewModel: TicketsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ticketsViewModel =
            ViewModelProvider(this).get(TicketsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tickets, container, false)
        return root
    }
}