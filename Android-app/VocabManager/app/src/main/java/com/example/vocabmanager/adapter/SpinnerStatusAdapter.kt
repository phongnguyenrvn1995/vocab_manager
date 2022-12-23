package com.example.vocabmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.vocabmanager.databinding.ItemSpinnerStatusViewBinding
import com.example.vocabmanager.databinding.ItemStatusBinding
import com.example.vocabmanager.entities.Status

class SpinnerStatusAdapter(val context: Context, private var listStatus: List<Status?>) :
    BaseAdapter() {
    fun updateStatus(listStatus: List<Status?>) {
        this.listStatus = listStatus
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listStatus.size
    }

    override fun getItem(position: Int): Status? {
        return listStatus[position]
    }

    override fun getItemId(position: Int) = listStatus[position]?.statusId?.toLong() ?: -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            viewHolder = ViewHolderItem()
            viewHolder.binding =
                ItemSpinnerStatusViewBinding.inflate(inflater, parent, false)
            view = viewHolder.binding.root
            view.tag = viewHolder
//            Log.d(TAG, "getView: convertView NULL $viewHolder")
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolderItem
//            Log.d(TAG, "getView: convertView NOT NULL = $convertView")
        }
        viewHolder.binding.txtStatusName.text = listStatus[position]?.statusDescription

        return view
    }


    private lateinit var dropDownViewBinding: ItemStatusBinding
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        dropDownViewBinding = ItemStatusBinding.inflate(inflater, parent, false)
        listStatus[position]?.let { status ->
            dropDownViewBinding.txtStatusName.text = status.statusDescription
            dropDownViewBinding.activeDot.setImageResource(
                when (status.statusId) {
                    Status.ACTIVE -> android.R.color.holo_green_dark
                    Status.DE_ACTIVE -> android.R.color.holo_red_dark
                    else -> android.R.color.holo_blue_dark
                }
            )
        }
        return dropDownViewBinding.root
    }

    class ViewHolderItem {
        lateinit var binding: ItemSpinnerStatusViewBinding
    }

    companion object {
        const val TAG = "SpinnerStatusAdapter"
    }
}