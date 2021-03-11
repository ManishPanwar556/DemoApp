package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R
import com.example.demoapp.models.User
import com.example.demoapp.databinding.ActivitySignInBinding
import com.example.demoapp.viewModel.MyViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

const val RC_SIGNIN=123
class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    lateinit var signInClient: GoogleSignInClient

   private val viewModel by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signInBtn.setOnClickListener {
          signIn()
        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, gso)
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGNIN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGNIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            uploadUserToFirebase(account?.idToken)
        } catch (e: ApiException) {
            Log.e("SignIn", "Failed")
        }
    }
    private fun uploadUserToFirebase(token: String?) {

        val credential = GoogleAuthProvider.getCredential(token, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
            val user = it.user
            viewModel.addUserToFireStore(user)
            val intent=Intent(this,ChatActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}