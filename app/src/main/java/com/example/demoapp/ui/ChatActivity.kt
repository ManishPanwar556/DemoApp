package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.*
import com.example.demoapp.adapter.ChatAdapter
import com.example.demoapp.databinding.ActivityChatBinding
import com.example.demoapp.interfaces.ClickInterface
import com.example.demoapp.models.MessageTime
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity(), ClickInterface {
    val auth = FirebaseAuth.getInstance()
    val db by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var adapter: ChatAdapter
    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("RecentChats")
        makeGroup()
//        val query=db.collection("messages").orderBy("updateTime", Query.Direction.ASCENDING)
//        query.addSnapshotListener { value, error ->
//
//        }
        val query=db.collection("message").orderBy("time",Query.Direction.DESCENDING)
        val options=FirestoreRecyclerOptions.Builder<MessageTime>().setQuery(query, MessageTime::class.java).build()
        adapter= ChatAdapter(options,this)
        binding.chatRecyclerView.adapter=adapter
        binding.chatRecyclerView.layoutManager=LinearLayoutManager(this)
        binding.userBtn.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
        }
    }
    private fun makeGroup(){
        db.collection("message").document("group").get().addOnSuccessListener {
            if (it.exists()) {
                Log.e("MainActivity", "Exist")
            } else {
                val map = hashMapOf<String, Any>(
                    "id" to "group",
                    "lastMessage" to "",
                    "name" to "",
                    "time" to 0L
                )
                Log.e("MainActivity", "Not Exist")
                db.collection("message").document("group").set(map).addOnSuccessListener {
                    Log.e("MainActivity", "Complete")
                }
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOut -> {
                auth.signOut()
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }

    override fun onClick(id: String) {
        val intent=Intent(this, RoomActivity::class.java)
        intent.putExtra("rid",id)
        startActivity(intent)
    }
}