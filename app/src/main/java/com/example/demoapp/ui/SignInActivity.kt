package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.demoapp.R
import com.example.demoapp.models.User
import com.example.demoapp.databinding.ActivitySignInBinding
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
    val db by lazy {
        FirebaseFirestore.getInstance()
    }
    val auth=FirebaseAuth.getInstance()
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
            addUser(user)
        }
    }

    private fun addUser(user: FirebaseUser?) {
        db.collection("users").document(user!!.uid).get().addOnCompleteListener {
            val document = it.getResult()
            if (document != null && document.exists()) {
                updateUI()
            } else {
                user.let {
                    val user = User(name=it.displayName,profile=it.photoUrl.toString(),id=it.uid)
                    db.collection("users").document(it.uid).set(user).addOnSuccessListener {
                        updateUI()
                    }.addOnFailureListener {
                        Toast.makeText(this, "SignIn Failure", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }
    private fun updateUI(){
        db.collection("message").document("group").get().addOnSuccessListener {
            if(it.exists()){
                Log.e("MainActivity","Exist")
                val intent=Intent(this, ChatActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Log.e("MainActivity","Not Exist")
                db.collection("message").document("group").set("").addOnSuccessListener {
                    val intent=Intent(this, ChatActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

    }
}