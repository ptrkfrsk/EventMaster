package com.example.eventmaster.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.example.eventmaster.MainActivity
import com.example.eventmaster.R
import com.example.eventmaster.models.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_manage_profile)

        loadUserData()

        val buttonSave= findViewById<Button>(R.id.buttonManageProfileSave)
        buttonSave.setOnClickListener{
            finish()
        }

        val buttonCancel= findViewById<Button>(R.id.buttonManageProfileCancel)
        buttonCancel.setOnClickListener{
            finish()
        }
    }

    private fun loadUserData() {
        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("People")
        val nameComponent = findViewById<EditText>(R.id.editTextManageProfileName)
        val surnameComponent = findViewById<EditText>(R.id.editTextManageProfileSurname)
        val phoneComponent = findViewById<EditText>(R.id.editTextManageProfilePhone)
        val addressComponent = findViewById<EditText>(R.id.editTextManageProfileAddress)
        val accountComponent = findViewById<EditText>(R.id.editTextManageProfileAccount)
        val emailComponent = findViewById<EditText>(R.id.editTextManageProfileEmail)
        val passwordComponent = findViewById<EditText>(R.id.editTextManageProfilePassword)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var person : Person? = null
                val currentEmail = auth.currentUser?.email
                dataSnapshot.children.forEach{
                    val personObj = it.value as HashMap<*, *>
                    if (currentEmail.equals(personObj ["email"].toString())) {
                        person = Person(
                                name = personObj ["name"].toString(),
                                surname = personObj ["surname"].toString(),
                                phone = personObj ["phone"].toString(),
                                address = personObj ["address"].toString(),
                                account = personObj ["account"].toString(),
                                email = personObj ["email"].toString()
                        )
                        return@forEach
                    }
                }

                nameComponent.setText(person?.name)
                surnameComponent.setText(person?.surname)
                phoneComponent.setText(person?.phone)
                addressComponent.setText(person?.address)
                accountComponent.setText(person?.account)
                emailComponent.setText(person?.email)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(listener)
    }
}