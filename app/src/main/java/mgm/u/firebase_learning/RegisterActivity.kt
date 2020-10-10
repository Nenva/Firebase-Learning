package mgm.u.firebase_learning

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private val personCollectionRef = Firebase.firestore.collection("persons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {
            registerUser()
            uploadUserInfo()
        }

        textLoginActivity.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun uploadUserInfo() {
        val username = editUsernameRegister.text.toString()
        val firstName = editNameRegister.text.toString()
        val lastName = editLastNameRegister.text.toString()
        val age = editAgeRegister.text.toString().toInt()
        val email = editEmailRegister.text.toString()
        val password = editPasswordRegister.text.toString()
        val person = Person(username, firstName, lastName, age, email, password)
        savePerson(person)
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch {
        try {
            personCollectionRef.add(person).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Data was successfully stored", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerUser() {
        val email = editEmailRegister.text.toString()
        val password = editPasswordRegister.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main) {
                        transitionToMainActivity()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun transitionToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}