package com.example.vocabmanager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.databinding.ItemVocabBinding
import com.example.vocabmanager.entities.Vocab

class VocabAdapter(var listVocabs: MutableList<Vocab>?) :
    RecyclerView.Adapter<VocabAdapter.ViewHolder>() {

    interface ItemEvent {
        fun onItemClickListener(vocab: Vocab)
        fun onAudioClickListener(vocab: Vocab)
        fun onItemLongClickListener(vocab: Vocab): Boolean
    }

    var event: ItemEvent? = null

    class ViewHolder(val binding: ItemVocabBinding, var vocab: Vocab? = null) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVocabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocab = listVocabs?.get(position)
        holder.vocab = vocab
        holder.binding.apply {
            txtEn.text = vocab?.vocabEn
            txtIpa.text = vocab?.vocabIpa
            txtLesson.text = vocab?.lessonName

            vocab?.let {
                root.setOnClickListener {
                    event?.onItemClickListener(vocab)
                }

                root.setOnLongClickListener {
                    event?.onItemLongClickListener(vocab) ?: false
                }

                btnSound.setOnClickListener {
                    event?.onAudioClickListener(vocab)
                }
            }
        }
    }

    override fun getItemCount(): Int = listVocabs?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateVocabs(list: MutableList<Vocab>) {
        this.listVocabs = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addVocabs(list: MutableList<Vocab>) {
        if(this.listVocabs == null)
            this.listVocabs = mutableListOf()
        this.listVocabs?.addAll(list)
        notifyDataSetChanged()
    }

    fun updateVocabs(oldVocab: Vocab, newVocab: Vocab) {
        val index = listVocabs?.indexOf(oldVocab)
        if (index == null || index == -1)
            return

        this.listVocabs?.set(index, newVocab)
        this.listVocabs?.indexOf(newVocab)?.let {
            notifyItemChanged(it)
        }
    }

    fun delVocab(vocab: Vocab) {
        val position = this.listVocabs?.indexOf(vocab)
        this.listVocabs?.remove(vocab)
        position?.let {
            notifyItemRemoved(position)
        }
    }
}

fun VocabAdapter.notifyItemChanged(vocab: Vocab?) {
    listVocabs?.indexOf(vocab)?.let {
        notifyItemChanged(it)
    }
}