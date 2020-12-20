package com.example.eventmaster.ui.tickets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicketsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tickets Fragment"
    }
    val text: LiveData<String> = _text
}