package com.example.demoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.models.Message
import com.example.demoapp.adapter.MessageAdapter
import com.example.demoapp.models.MessageTime
import com.example.demoapp.databinding.ActivityRoomBinding
import com.example.demoapp.viewModel.MyViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class RoomActivity : AppCompatActivity() {

    private val viewModel by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    lateinit var adapter: MessageAdapter
    lateinit var binding: ActivityRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Chat")
        val extras = intent.extras
        val id = extras?.get("id").toString()
        val rid = extras?.get("rid").toString()
        val query = viewModel.queryMessage(rid)
        val options=FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
        adapter= MessageAdapter(options)
        binding.rev.adapter=adapter
        binding.rev.layoutManager=LinearLayoutManager(this)
        binding.sendBtn.setOnClickListener {
            if (!binding.messageEditText.text.isEmpty()) {
                val message = binding.messageEditText.text.toString()
                binding.messageEditText.text.clear()
                val time=System.currentTimeMillis()
                viewModel.sendMessageToFireStore(rid,time,message)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}