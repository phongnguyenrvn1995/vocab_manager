package com.example.vocabmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.vocabmanager.databinding.ItemPosBinding
import com.example.vocabmanager.databinding.ItemSpinnerPosViewBinding
import com.example.vocabmanager.entities.PoS

class SpinnerPoSAdapter(val context: Context, private var poSs: List<PoS?>) :
    BaseAdapter() {
    fun updatePoSs(poSs: List<PoS?>) {
        this.poSs = poSs
        notifyDataSetChanged()
    }

    fun indexOf(poS: PoS?) = this.poSs.indexOf(poS)

    fun indexOf(id: Int?): Int {
        var poS: PoS? = null
        this.poSs.filter {
            it?.vocabTypeId == id
        }.forEach {
            poS = it
            return@forEach
        }
        return indexOf(poS)
    }

    override fun getCount(): Int {
        return poSs.size
    }

    override fun getItem(position: Int): PoS? {
        return poSs[position]
    }

    override fun getItemId(position: Int) = poSs[position]?.vocabTypeId?.toLong() ?: -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            viewHolder = ViewHolderItem()
            viewHolder.binding =
                ItemSpinnerPosViewBinding.inflate(inflater, parent, false)
            view = viewHolder.binding.root
            view.tag = viewHolder
//            Log.d(TAG, "set: convertView NULL $viewHolder")
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolderItem
//            Log.d(TAG, "get: convertView NOT NULL $convertView")
        }

        viewHolder.binding.txtPosName.text = poSs[position]?.vocabTypeName

        return view
    }


    private lateinit var dropDownViewBinding: ItemPosBinding
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        dropDownViewBinding = ItemPosBinding.inflate(inflater, parent, false)
        poSs[position]?.let { poS ->
            dropDownViewBinding.txtPosName.text = poS.vocabTypeName
        }
        return dropDownViewBinding.root
    }

    class ViewHolderItem {
        lateinit var binding: ItemSpinnerPosViewBinding
    }

    companion object {
        const val TAG = "SpinnerPoSAdapter"
    }
}