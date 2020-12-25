package com.example.eventmaster.ui.events

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.eventmaster.LoginActivity
import com.example.eventmaster.R

class EventsFragment : Fragment() {

    private lateinit var eventsViewModel: EventsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        eventsViewModel =
            ViewModelProvider(this).get(EventsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_events, container, false)

        val buttonSearch: Button = root.findViewById(R.id.buttonSearchEvent)
        buttonSearch.setOnClickListener{
            goToSearchEvent()
        }

        val buttonAdd: Button = root.findViewById(R.id.buttonAddEvent)
        buttonAdd.setOnClickListener{
            goToAddEvent()
        }

        return root
    }

    fun goToSearchEvent() {
        val intent = Intent (activity, SearchEventActivity::class.java)
        activity?.startActivity(intent)
    }

    fun goToAddEvent() {
        val intent = Intent (activity, AddEventActivity::class.java)
        activity?.startActivity(intent)
    }
}