package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.demoapp.R
import com.example.demoapp.viewModel.MyViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val viewModel by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val res=viewModel.checkUserLoggedIn()
        if(res){
            val intent= Intent(this, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent= Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}