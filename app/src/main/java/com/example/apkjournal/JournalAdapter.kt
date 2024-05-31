package com.example.apkjournal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JournalAdapter(private val journalList: List<Journal>) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>(){

    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val note: TextView = itemView.findViewById(R.id.tvNote)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalAdapter.JournalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_journal,parent,false)
        return JournalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: JournalAdapter.JournalViewHolder, position: Int) {
       val currentItem = journalList[position]
        holder.title.text = currentItem.title
        holder.date.text = currentItem.date
        holder.note.text = currentItem.note
    }

    override fun getItemCount(): Int = journalList.size
}