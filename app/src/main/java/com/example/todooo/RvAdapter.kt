package com.example.todooo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todooo.databinding.EachItemBinding

class RvAdapter : RecyclerView.Adapter<RvAdapter.viewholder>(){
    private var listnote =ArrayList<Note>()
    fun initnote(list : ArrayList<Note>){
        listnote=list
    }
    fun getnote(position: Int): Note{
        return listnote[position]
    }
    inner class viewholder(val binding: EachItemBinding) : RecyclerView.ViewHolder(binding.root){
        init{
            binding.constraint1.setOnClickListener {
                val note = listnote[adapterPosition]
                note.visi=!note.visi
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding = EachItemBinding.inflate(LayoutInflater.from(parent.context)
            ,parent, false)
        return viewholder(binding)

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
       val note = listnote[position]
        holder.itemView.apply {
            holder.binding.txtnote.setText(note.name)
            holder.binding.txtdes.setText(note.des)
            if(note.visi){
                holder.binding.constraint2.visibility= View.VISIBLE
            }
            else holder.binding.constraint2.visibility= View.GONE
        }
    }

    override fun getItemCount(): Int {
        return listnote.size
    }
}