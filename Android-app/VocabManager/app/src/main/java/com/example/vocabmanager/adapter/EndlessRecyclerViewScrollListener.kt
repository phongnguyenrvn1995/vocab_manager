package com.example.vocabmanager.adapter

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewScrollListener(private val mLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    var loading = false
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) { //check for scroll down
            visibleItemCount = mLayoutManager.childCount
            totalItemCount = mLayoutManager.itemCount
            pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()

            if (!loading) {
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loading = true
                    Log.d("...", "Last Item Wow !")
                    // Do pagination.. i.e. fetch new data
                    loadMore()
//                    loading = true
                }
            }
        }
    }

    abstract fun loadMore()
}