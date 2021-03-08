package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.interfaces.ClickInterface
import com.example.demoapp.adapter.MyAdapter
import com.example.demoapp.models.User
import com.example.demoapp.databinding.ActivityUsersBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UsersActivity : AppCompatActivity(), ClickInterface {
    lateinit var binding: ActivityUsersBinding
    val db by lazy {
        FirebaseFirestore.getInstance()
    }
    val auth by lazy{
        FirebaseAuth.getInstance()
    }
    lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Users")
        val uid=auth.currentUser.uid
        val query = db.collection("users").whereNotEqualTo("id",uid)
        val options =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()
        adapter = MyAdapter(options,this)
        binding.userRecyclerView.adapter = adapter
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onClick(id: String) {
        db.collection("users").document(id).get().addOnSuccessListener {
            if(it.get(auth.currentUser.uid)==null){
                val rid=UUID.randomUUID().toString()
                db.collection("users").document(id).update(auth.currentUser.uid,rid).addOnSuccessListener {
                    db.collection("users").document(auth.currentUser.uid).update(id,rid).addOnSuccessListener {
                        navigateToRoomActivity(id,rid)
                    }
                }
            }
            else{
                val rid=it.get(auth.currentUser.uid).toString()
                navigateToRoomActivity(id,rid)
            }
        }
    }
    private fun navigateToRoomActivity(id:String,rid:String){
        val intent= Intent(this, RoomActivity::class.java)
        intent.putExtra("id",id)
        intent.putExtra("rid",rid)
        startActivity(intent)
    }
}