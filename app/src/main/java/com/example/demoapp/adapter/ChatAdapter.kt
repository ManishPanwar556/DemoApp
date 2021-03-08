package com.example.demoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.interfaces.ClickInterface
import com.example.demoapp.models.MessageTime
import com.example.demoapp.R
import com.example.demoapp.utils.TimeFormat
import com.example.demoapp.databinding.UserItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(options: FirestoreRecyclerOptions<MessageTime>, var clickInterface: ClickInterface) :
    FirestoreRecyclerAdapter<MessageTime, ChatAdapter.MyViewHolder>(options) {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            val binding = UserItemBinding.bind(view)
            binding.item.setOnClickListener {
                clickInterface.onClick(snapshots[adapterPosition].id)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: MessageTime) {
        val binding = UserItemBinding.bind(holder.itemView)
        binding.userName.text = model.id
        binding.latestMessage.text="${model.lastMessage}  ${TimeFormat.formatTime(model.time)}"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return MyViewHolder(view)
    }
}