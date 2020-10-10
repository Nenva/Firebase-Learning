package mgm.u.firebase_learning

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        buttonUpdate.setOnClickListener {
            updateProfile()
        }

        textLogout.setOnClickListener {
            auth.signOut()
            transitionToLoginActivity()
        }
    }

    private fun updateProfile() {
            auth.currentUser?.let { user ->
                val username = editUsernameUpdated.text.toString()
            val photoURI = Uri.parse("android.resources://$packageName/${R.drawable.random}")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdates).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity, "Successfully updated!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun checkLoggedInState() {
        val user = auth.currentUser
        val username = editUsernameUpdated.text.toString()
        if (user == null) {
            textResult.text = "You are not logged in"
        } else {
            textResult.text = "Welcome $username"
            editImageUpdated.setImageURI(user.photoUrl)
        }
    }

    private fun transitionToLoginActivity() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
        }
    }
}