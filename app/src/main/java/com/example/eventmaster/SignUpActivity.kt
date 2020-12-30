package com.example.eventmaster
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.example.eventmaster.models.Event
import com.example.eventmaster.models.Person
import com.google.android.gms.common.data.DataBufferObserverSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance();

        val buttonSuSignUp = findViewById<Button>(R.id.buttonSuSignUp)
        buttonSuSignUp.setOnClickListener(){
            val editTextSuEmail = findViewById<EditText>(R.id.editTextSuEmail)
            val editTextSuPassword = findViewById<EditText>(R.id.editTextSuPassword)
            checkDataAndCreateUser(database)
        }
    }

    private fun checkDataAndCreateUser(database: FirebaseDatabase) {
        val nameComponent = findViewById<EditText>(R.id.editTextSuName)
        val surnameComponent = findViewById<EditText>(R.id.editTextSuSurname)
        val phoneComponent = findViewById<EditText>(R.id.editTextSuPhone)
        val addressComponent = findViewById<EditText>(R.id.editTextSuAddress)
        val accountComponent = findViewById<EditText>(R.id.editTextSuAccount)
        val emailComponent = findViewById<EditText>(R.id.editTextSuEmail)
        val passwordComponent = findViewById<EditText>(R.id.editTextSuPassword)

        if (
                nameComponent.text.isNullOrEmpty() ||
                surnameComponent.text.isNullOrEmpty() ||
                phoneComponent.text.isNullOrEmpty() ||
                addressComponent.text.isNullOrEmpty() ||
                accountComponent.text.isNullOrEmpty() ||
                emailComponent.text.isNullOrEmpty() ||
                passwordComponent.text.isNullOrEmpty()
        ) {
            Toast.makeText(this, "Puste pola - nie udało się zarejestrować", Toast.LENGTH_SHORT).show()
            return
        }
        val person = Person(
                nameComponent.text.toString(),
                surnameComponent.text.toString(),
                phoneComponent.text.toString(),
                addressComponent.text.toString(),
                accountComponent.text.toString(),
                emailComponent.text.toString()
        )
        val peopleRef = database.getReference("/People");

        val key = peopleRef.push().key
        if (key != null) {
            peopleRef.child(key).setValue(person)
        }

        createAuthUser(emailComponent.text.toString(), passwordComponent.text.toString())
    }

    private fun createAuthUser(email : String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Pomyślnie zarejestrowano", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, "Nie udało się zarejestrować", Toast.LENGTH_SHORT).show()
                    }
                }
    }


    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}