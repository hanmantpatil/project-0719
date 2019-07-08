package com.example.project0719

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.project0719.admin.AdminHome
import com.example.project0719.user.UserHome
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var isLogin = true
    private lateinit var signAlternate: TextView
    private lateinit var signButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (auth.currentUser != null) {
            takeToUserHome()
        }

        signButton = findViewById(R.id.sign_button)
        signAlternate = findViewById(R.id.sign_alternate)

        signButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@MainActivity, R.string.empty_email_error, Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@MainActivity, R.string.empty_password_error, Toast.LENGTH_SHORT)
                    .show()
            } else if (!isValid(email)) {
                Toast.makeText(this@MainActivity, R.string.invalid_email_error, Toast.LENGTH_SHORT)
                    .show()
            }else {
                if (isLogin) {
                    signIn(email, password)
                } else {
                    signUp(email, password)
                }
            }
        }

        signButton.setOnLongClickListener {
            takeToAdminHome()
            true
        }

        signAlternate.setOnClickListener {
            if (isLogin) {
                signAlternate.text = getString(R.string.existing_user)
                signButton.text = getString(R.string.sign_up)
                isLogin = false
            } else {
                signAlternate.text = getString(R.string.add_account)
                signButton.text = getString(R.string.sign_in)
                isLogin = true
            }
        }
    }

    private fun takeToAdminHome() {
        startActivity(Intent(this, AdminHome::class.java))
        finish()
    }

    private fun takeToUserHome() {
        startActivity(Intent(this, UserHome::class.java))
        finish()
    }

    private fun signUp(email: String, password: String) {
        showLoader()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideLoader()
                if (task.isSuccessful) {
                    takeToUserHome()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        showLoader()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideLoader()
                if (task.isSuccessful) {
                    takeToUserHome()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun isValid(email: String): Boolean {
        val regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$"
        return email.matches(regex.toRegex())
    }
}
