package com.example.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.interfaces.ClickInterface
import com.example.demoapp.adapter.MyAdapter
import com.example.demoapp.models.User
import com.example.demoapp.databinding.ActivityUsersBinding
import com.example.demoapp.viewModel.MyViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class UsersActivity : AppCompatActivity(), ClickInterface {
    lateinit var binding: ActivityUsersBinding
    private val db by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth by lazy{
        FirebaseAuth.getInstance()
    }
    private val viewModel by lazy{
        ViewModelProvider(this).get(MyViewModel::class.java)
    }
    lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Users")
        val query = viewModel.queryListOfUser()
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