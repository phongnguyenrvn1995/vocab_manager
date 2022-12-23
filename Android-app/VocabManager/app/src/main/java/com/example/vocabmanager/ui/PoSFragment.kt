package com.example.vocabmanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.R
import com.example.vocabmanager.adapter.PoSAdapter
import com.example.vocabmanager.adapter.notifyItemChanged
import com.example.vocabmanager.databinding.FragmentPosBinding
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.ui.dialog.AddPoSFragment
import com.example.vocabmanager.ui.dialog.DeletePoSFragment
import com.example.vocabmanager.ui.dialog.NotifyFragment
import com.example.vocabmanager.ui.dialog.UpdatePoSFragment
import com.example.vocabmanager.viewmodel.PoSViewModel

class PoSFragment : Fragment(), MenuProvider, PoSAdapter.ItemEvent {
    companion object {
        const val TAG = "PoSFragment"
    }

    private var _binding: FragmentPosBinding? = null
    private val binding get() = _binding!!
    private val poSViewModel: PoSViewModel by viewModels()
    private lateinit var adapter: PoSAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initSwipeEvent()
        initObserver()
        onViewStateRestored(savedInstanceState)
    }

    private fun initObserver() {
        with(poSViewModel) {
            searchData?.observe(viewLifecycleOwner) {
                getData(it)
            }

            poSData?.observe(viewLifecycleOwner) {
                adapter.updatePoSs(it.toMutableList())
            }

            progressData?.observe(viewLifecycleOwner) {
                binding.layoutProgress.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initSwipeEvent() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is PoSAdapter.ViewHolder) {
                    viewHolder.poS?.let {
                        showDeletePoSDialog(it)
                    }
                }
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun initView() {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        adapter = PoSAdapter(null)
        adapter.event = this
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = this@PoSFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        with(binding) {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                poSViewModel.getData(poSViewModel.getCurrentSearch())
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    poSViewModel.searchQuery(query ?: "")
                    binding.searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.trim()?.isEmpty() == true)
                        poSViewModel.searchQuery(newText)
                    return true
                }
            })
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.pos_menu_context, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemSelected: ${menuItem.itemId}")
        when (menuItem.itemId) {
            R.id.action_add -> showAddPoSDialog()
        }
        return false
    }

    override fun onItemClickListener(poS: PoS) {
        showUpdatePoSDialog(poS)
    }

    override fun onItemLongClickListener(poS: PoS): Boolean {
        return false
    }

    private fun showDeletePoSDialog(poS: PoS) {
        val dialog = DeletePoSFragment.newInstance(poS)
        dialog.listener = object : DeletePoSFragment.DelPoSListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_delete)}\n${poS.vocabTypeName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                adapter.delPoS(poS)
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
                dialog.dismiss()
            }
        }
        dialog.show(childFragmentManager, DeletePoSFragment.TAG)
        adapter.notifyItemChanged(poS)
    }

    private fun showUpdatePoSDialog(oldPoS: PoS) {
        val dialog = UpdatePoSFragment.newInstance(oldPoS)
        dialog.listener = object : UpdatePoSFragment.UpdatePoSListener {
            override fun onSuccess(poS: PoS) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_update)}\n${poS.vocabTypeName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                adapter.updatePoSs(oldPoS, poS)
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
            }
        }
        dialog.show(childFragmentManager, UpdatePoSFragment.TAG)
    }

    private fun showAddPoSDialog() {
        val dialog = AddPoSFragment()
        dialog.listener = object : AddPoSFragment.AddPoSListener {
            override fun onSuccess(poS: PoS) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_add)}\n${poS.vocabTypeName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                poSViewModel.searchQuery(binding.searchView.query.toString())
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
            }
        }
        dialog.show(childFragmentManager, AddPoSFragment.TAG)
    }

    private fun showSuccessDialog(title: String?, message: String?) {
        val notifyFragment = NotifyFragment.newInstance(title ?: "", message ?: "")
        notifyFragment.show(childFragmentManager, NotifyFragment.TAG)
    }

    private fun showErrorDialog(title: String?, message: String?) {
        val notifyFragment =
            NotifyFragment.newInstance(title ?: "", message ?: "", NotifyFragment.DialogType.FAILED)
        notifyFragment.show(childFragmentManager, NotifyFragment.TAG)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val search = _binding?.searchView?.query?.toString()
        Log.d(TAG, "onSaveInstanceState: $search")
        outState.putString("search", search ?: "")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val search = savedInstanceState?.getString("search")
        Log.d(TAG, "onViewStateRestored: $search")
        handler.postDelayed({
            _binding?.searchView?.setQuery(search, true)
        }, 50)
    }

    private val handler = Handler(Looper.myLooper()!!)
}