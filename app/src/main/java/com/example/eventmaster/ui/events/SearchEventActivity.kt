package com.example.eventmaster.ui.events

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R

class SearchEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_search_event)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}