package com.example.demoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoapp.interfaces.ClickInterface
import com.example.demoapp.R
import com.example.demoapp.models.User
import com.example.demoapp.databinding.UserItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MyAdapter(options: FirestoreRecyclerOptions<User>, var clickInterface: ClickInterface) :
    FirestoreRecyclerAdapter<User, MyAdapter.MyViewHolder>(options) {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val binding = UserItemBinding.bind(view)
            binding.item.setOnClickListener {
                clickInterface.onClick(snapshots[adapterPosition].id)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: User) {
        val binding = UserItemBinding.bind(holder.itemView)
        Glide.with(holder.itemView).load(model.profile).into(binding.profileImage)
        binding.userName.text = model.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return MyViewHolder(view)
    }
}