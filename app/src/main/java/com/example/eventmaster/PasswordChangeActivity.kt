package com.example.eventmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class PasswordChangeActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_password_change)

        auth = FirebaseAuth.getInstance()

        val buttonCancel = findViewById<Button>(R.id.buttonChangePasswordCancel)
        buttonCancel.setOnClickListener{
            finish()
        }

        val buttonChange = findViewById<Button>(R.id.buttonChangePasswordSave)
        buttonChange.setOnClickListener{
            if (checkOnlineConnection(this))
                compareAndChangePassword()
            else
                Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compareAndChangePassword() {
        val editPasswordOldComponent = findViewById<EditText>(R.id.editPasswordChangeActual)
        val editPasswordNewComponent = findViewById<EditText>(R.id.editPasswordChangeNew)
        val editPasswordConfirmedComponent = findViewById<EditText>(R.id.editPasswordChangeRepeat)

        val old = editPasswordOldComponent.text.toString()
        val new = editPasswordNewComponent.text.toString()
        val confirmed = editPasswordConfirmedComponent.text.toString()

        var isBlocked = false
        if (old.isEmpty()) {
            editPasswordOldComponent.error = "Podaj aktualne hasło"
            isBlocked = true
        }
        if (old == new) {
            editPasswordNewComponent.error = "Nowe hasło jest identyczne jak poprzednie"
            isBlocked = true
        }
        if (new.length < 6) {
            editPasswordNewComponent.error = "Nowe hasło jest za krótkie"
            isBlocked = true
        }
        if (new != confirmed) {
            editPasswordConfirmedComponent.error = "Powtórzone hasło musi się zgadzać z nowym"
            isBlocked = true
        }
        if (isBlocked)
            return
        changePassword(old, new)
    }

    private fun changePassword(old : String, new : String) {
        val user = auth.currentUser
        val credential: AuthCredential = EmailAuthProvider.getCredential(user?.email!!, old);

        user.reauthenticate(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    user.updatePassword(new)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else
                                Toast.makeText(this, "Nie udało się zmienić hasła", Toast.LENGTH_SHORT).show()
                        }
                } else
                    Toast.makeText(this, "Podano błędne aktualne hasło", Toast.LENGTH_SHORT).show()
            }
    }
}