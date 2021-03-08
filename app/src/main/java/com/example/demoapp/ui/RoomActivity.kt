package com.example.demoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.models.Message
import com.example.demoapp.adapter.MessageAdapter
import com.example.demoapp.models.MessageTime
import com.example.demoapp.databinding.ActivityRoomBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class RoomActivity : AppCompatActivity() {
    val db by lazy {
        FirebaseFirestore.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
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
        val query = db.collection("message").document(rid).collection("messages").orderBy(
            "createdAt",
            Query.Direction.ASCENDING
        )
        val options=FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()
        adapter= MessageAdapter(options)
        binding.rev.adapter=adapter
        binding.rev.layoutManager=LinearLayoutManager(this)
        binding.sendBtn.setOnClickListener {
            if (!binding.messageEditText.text.isEmpty()) {
                val message = binding.messageEditText.text.toString()
                binding.messageEditText.text.clear()
                db.collection("users").document(auth.currentUser.uid).get().addOnSuccessListener {
                    val name = it.get("name").toString()
                    uploadMessage(name, rid, message)
                }
            }
        }
    }

    private fun uploadMessage(name: String, rid: String, text: String) {
        val time = System.currentTimeMillis()
        val message = Message(text, name, time)
        val messageTime = MessageTime(
            text,name,time,rid
        )
        db.collection("message").document(rid).set(messageTime).addOnSuccessListener {
            val messageId = UUID.randomUUID().toString()
            db.collection("message").document(rid).collection("messages").document(messageId)
                .set(message).addOnSuccessListener {
                    Toast.makeText(this, "Message send", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
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