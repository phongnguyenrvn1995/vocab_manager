package com.example.vocabmanager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.databinding.ItemPosBinding
import com.example.vocabmanager.entities.PoS

class PoSAdapter(var listPoS: MutableList<PoS>?) :
    RecyclerView.Adapter<PoSAdapter.ViewHolder>() {

    interface ItemEvent {
        fun onItemClickListener(poS: PoS)
        fun onItemLongClickListener(poS: PoS): Boolean
    }

    var event: ItemEvent? = null

    class ViewHolder(val binding: ItemPosBinding, var poS: PoS? = null) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = listPoS?.get(position)
        holder.poS = pos
        holder.binding.apply {
            txtPosName.text = pos?.vocabTypeName

            pos?.let {
                root.setOnClickListener {
                    event?.onItemClickListener(pos)
                }

                root.setOnLongClickListener {
                    event?.onItemLongClickListener(pos) ?: false
                }
            }
        }
    }

    override fun getItemCount(): Int = listPoS?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updatePoSs(list: MutableList<PoS>) {
        this.listPoS = list
        notifyDataSetChanged()
    }

    fun updatePoSs(oldPoS: PoS, newPoS: PoS) {
        val index = listPoS?.indexOf(oldPoS)
        if (index == null || index == -1)
            return

        this.listPoS?.set(index, newPoS)
        this.listPoS?.indexOf(newPoS)?.let {
            notifyItemChanged(it)
        }
    }

    fun delPoS(poS: PoS) {
        val position = this.listPoS?.indexOf(poS)
        this.listPoS?.remove(poS)
        position?.let {
            notifyItemRemoved(position)
        }
    }
}

fun PoSAdapter.notifyItemChanged(poS: PoS?) {
    listPoS?.indexOf(poS)?.let {
        notifyItemChanged(it)
    }
}