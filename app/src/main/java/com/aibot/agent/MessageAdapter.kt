package com.aibot.agent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val messageCard: MaterialCardView = itemView.findViewById(R.id.messageCard)

        fun bind(message: Message) {
            messageText.text = message.text
            if (message.isUser) {
                messageCard.cardBackgroundColor = itemView.context.getColor(R.color.user_message_bg)
                messageText.setTextColor(itemView.context.getColor(R.color.user_message_text))
            } else {
                messageCard.cardBackgroundColor = itemView.context.getColor(R.color.ai_message_bg)
                messageText.setTextColor(itemView.context.getColor(R.color.ai_message_text))
            }
        }
    }
}
