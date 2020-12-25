package com.example.eventmaster
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        val buttonSuSignUp = findViewById<Button>(R.id.buttonSuSignUp)
        buttonSuSignUp.setOnClickListener(){
            val editTextSuEmail = findViewById<EditText>(R.id.editTextSuEmail)
            val editTextSuPassword = findViewById<EditText>(R.id.editTextSuPassword)
            createUser(editTextSuEmail.text.toString(), editTextSuPassword.text.toString())
        }
    }

    private fun createUser(email : String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Puste pole adresu email lub hasła", Toast.LENGTH_SHORT).show()
            return
        }

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