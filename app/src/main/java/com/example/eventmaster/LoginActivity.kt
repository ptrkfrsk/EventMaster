package com.example.eventmaster
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener{
            val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
            val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
            if (checkOnlineConnection(this))
                loginUser(editTextEmail.text.toString(), editTextPassword.text.toString())
            else
                Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser : FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun loginUser(email : String, password: String) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Puste pole adresu email lub hasła", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                    Toast.makeText(this, "Niepoprawny adres email lub hasło", Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    override fun onBackPressed() {
        return;
    }
}