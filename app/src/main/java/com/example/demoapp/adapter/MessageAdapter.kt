package com.example.demoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demoapp.models.Message
import com.example.demoapp.R
import com.example.demoapp.databinding.MessageItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MessageAdapter(options: FirestoreRecyclerOptions<Message>) :
    FirestoreRecyclerAdapter<Message, MessageAdapter.MessageViewHolder>(options) {
inner class MessageViewHolder(view: View):RecyclerView.ViewHolder(view){

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.message_item,parent,false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
        val binding=MessageItemBinding.bind(holder.itemView)
        binding.message.text=model.message
        binding.userName.text=model.name
    }

}