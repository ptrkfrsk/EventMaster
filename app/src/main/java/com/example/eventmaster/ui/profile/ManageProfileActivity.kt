package com.example.eventmaster.ui.profile

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventmaster.R
import com.example.eventmaster.checkOnlineConnection
import com.example.eventmaster.models.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ManageProfileActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private var userId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_manage_profile)
        if (!checkOnlineConnection(this))
            Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        loadUserData()

        val buttonSave= findViewById<Button>(R.id.buttonManageProfileSave)
        buttonSave.setOnClickListener{
            if (checkOnlineConnection(this))
                saveNewUserData()
            else
                Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
        }

        val buttonCancel= findViewById<Button>(R.id.buttonManageProfileCancel)
        buttonCancel.setOnClickListener{
            finish()
        }
    }

    private fun loadUserData() {
        val ref = database.getReference("/People")
        val nameComponent = findViewById<EditText>(R.id.editTextManageProfileName)
        val surnameComponent = findViewById<EditText>(R.id.editTextManageProfileSurname)
        val phoneComponent = findViewById<EditText>(R.id.editTextManageProfilePhone)
        val addressComponent = findViewById<EditText>(R.id.editTextManageProfileAddress)
        val accountComponent = findViewById<EditText>(R.id.editTextManageProfileAccount)
        val emailComponent = findViewById<EditText>(R.id.editTextManageProfileEmail)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var person : Person? = null
                val currentEmail = auth.currentUser?.email
                dataSnapshot.children.forEach{
                    val personObj = it.value as HashMap<*, *>
                    if (currentEmail.equals(personObj["email"].toString())) {
                        userId = it.key
                        person = Person(
                                name = personObj["name"].toString(),
                                surname = personObj["surname"].toString(),
                                phone = personObj["phone"].toString(),
                                address = personObj["address"].toString(),
                                account = personObj["account"].toString(),
                                email = personObj["email"].toString()
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

    private fun saveNewUserData() {
        val nameComponent = findViewById<EditText>(R.id.editTextManageProfileName)
        val surnameComponent = findViewById<EditText>(R.id.editTextManageProfileSurname)
        val phoneComponent = findViewById<EditText>(R.id.editTextManageProfilePhone)
        val addressComponent = findViewById<EditText>(R.id.editTextManageProfileAddress)
        val accountComponent = findViewById<EditText>(R.id.editTextManageProfileAccount)

        val name = nameComponent.text.toString()
        val surname = surnameComponent.text.toString()
        val phoneNumber = phoneComponent.text.toString()
        val address = addressComponent.text.toString()
        val accountNumber = accountComponent.text.toString()

        var isBlocked = false
        if (name.isEmpty()) {
            nameComponent.error = "Podaj imię"
            isBlocked = true
        }
        if (surname.isEmpty()) {
            surnameComponent.error = "Podaj nazwisko"
            isBlocked = true
        }
        if (phoneNumber.length != 9) {
            phoneComponent.error = "Podaj poprawny numer telefonu (9 cyfr)"
            isBlocked = true
        }
        if (address.isEmpty()) {
            addressComponent.error = "Podaj adres zamieszkania"
            isBlocked = true
        }
        if (accountNumber.length != 26) {
            accountComponent.error = "Podaj prawidłowy numer konta (podano ${accountNumber.length} zamiast 26 cyfr)"
            isBlocked = true
        }

        if (isBlocked)
            return

        val person = Person(
                name,
                surname,
                phoneNumber,
                address,
                accountNumber,
                auth.currentUser?.email!!
        )

        val peopleRef = database.getReference("/People")
        val key = userId
        if (key != null) {
            peopleRef.child(key).setValue(person)
            Toast.makeText(this, "Zaktualizowano dane profilu", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Błąd klucza identyfikacyjnego", Toast.LENGTH_SHORT).show()
        }
    }
}