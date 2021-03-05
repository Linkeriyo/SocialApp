package com.example.socialapp.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.BleepsActivity
import com.example.socialapp.R
import com.example.socialapp.data.AppData
import com.example.socialapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var gSignInOptions: GoogleSignInOptions
    private lateinit var gSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            readyForNextActivity()
        }
        gSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()
    }

    private fun setup() {
        emailTextView = binding.loginEmailTextView
        passwordTextView = binding.loginPasswordTextView
    }

    fun googleButtonPress(view: View) {
        gSignInClient = GoogleSignIn.getClient(this, gSignInOptions)
        startActivityForResult(gSignInClient.signInIntent, RC_SIGN_IN)
    }

    fun loginButtonPress(view: View) {
        if (binding.loginEmailTextView.text.isNotEmpty()
                && binding.loginPasswordTextView.text.isNotEmpty()) {
            auth.signInWithEmailAndPassword(
                    emailTextView.text.toString(),
                    passwordTextView.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                            this,
                            "Se ha iniciado sesión satisfactoriamente.",
                            Toast.LENGTH_SHORT
                    ).show()
                    readyForNextActivity()
                } else {
                    Toast.makeText(
                            this,
                            "Error al iniciar sesión.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                    this,
                    "Los campos no deben estar vacíos.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun registerButtonPress(view: View) {
        if (emailTextView.text.isNotBlank()
                && passwordTextView.text.isNotBlank()) {
            auth.createUserWithEmailAndPassword(
                    emailTextView.text.toString(),
                    passwordTextView.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                            this,
                            "Se ha registrado satisfactoriamente.",
                            Toast.LENGTH_SHORT
                    ).show()
                    readyForNextActivity()
                } else {
                    Toast.makeText(
                            this,
                            "Error al registrarse.",
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                    this,
                    "Los campos no deben estar vacíos.",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<*> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = (task.getResult(ApiException::class.java) as GoogleSignInAccount?)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
                Thread.sleep(2000)
                Toast.makeText(
                        this,
                        "Se ha iniciado sesión satisfactoriamente.",
                        Toast.LENGTH_SHORT
                ).show()
                readyForNextActivity()
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(
                        this,
                        "Error al iniciar sesión",
                        Toast.LENGTH_SHORT
                ).show()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
            } else {
                Log.w(TAG, "signInWithCredential:failure", it.exception)
            }
        }
    }

    private fun readyForNextActivity() {
        if (isUserRegistered) {
            startActivity(Intent(context, BleepsActivity::class.java))
        } else {
            startActivity(Intent(context, RegisterActivity::class.java))
        }
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 1
        private const val TAG = "LoginActivity"
        val isUserRegistered: Boolean
            get() {
                val userId = FirebaseAuth.getInstance().uid
                for (user in AppData.userList) {
                    if (user.uid == userId) {
                        return true
                    }
                }
                return false
            }
    }
}