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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var buttonSuSignUp : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        supportActionBar?.hide(); // hide the title bar
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance();

        buttonSuSignUp = findViewById<Button>(R.id.buttonSuSignUp)
        buttonSuSignUp.setOnClickListener{
            if (checkOnlineConnection(this))
                checkDataAndCreateUser(database)
            else
                Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
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

        val name = nameComponent.text.toString()
        val surname = surnameComponent.text.toString()
        val phoneNumber = phoneComponent.text.toString()
        val address = addressComponent.text.toString()
        val accountNumber = accountComponent.text.toString()
        val email = emailComponent.text.toString()
        val password = passwordComponent.text.toString()

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
        if (email.isEmpty()) {
            emailComponent.error = "Podaj adres email"
            isBlocked = true
        }
        if (password.isEmpty()) {
            passwordComponent.error = "Podaj hasło"
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
                email
        )
        createAuthUser(emailComponent, passwordComponent, person)
    }

    private fun createAuthUser(emailComponent : EditText, passwordComponent: EditText, person : Person){
        val email = emailComponent.text.toString()
        val password = passwordComponent.text.toString()
        buttonSuSignUp.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val realPerson = createRealPerson(person)
                        if (realPerson) {
                            Toast.makeText(baseContext, "Pomyślnie zarejestrowano", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        else {
                            Toast.makeText(baseContext, "Wystąpił problem z danymi użytkownika. Skontaktuj się z pomocą techniczną", Toast.LENGTH_SHORT).show()
                            buttonSuSignUp.isEnabled = true
                        }
                    } else {
                        try {
                            throw task.exception as Throwable
                        } catch(exception : FirebaseAuthWeakPasswordException) {
                            passwordComponent.error = "Hasło musi składać się z co najmniej 6 znaków"
                        } catch(exception : FirebaseAuthInvalidCredentialsException) {
                            emailComponent.error = "Niepoprawny format adresu email"
                        } catch(exception : FirebaseAuthUserCollisionException) {
                            emailComponent.error = "Użytkownik o podanym adresie email już istnieje"
                        } catch (exception : Exception) {
                            Toast.makeText(baseContext, "Nie udało się zarejestrować", Toast.LENGTH_SHORT).show()
                        }
                        buttonSuSignUp.isEnabled = true
                    }
                }
    }

    private fun createRealPerson(person : Person) : Boolean {
        val peopleRef = database.getReference("/People");
        val key = peopleRef.push().key ?: return false
        peopleRef.child(key).setValue(person)
        return true
    }


    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}