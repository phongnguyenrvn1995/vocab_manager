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
import com.example.vocabmanager.adapter.CourseAdapter
import com.example.vocabmanager.adapter.notifyItemChanged
import com.example.vocabmanager.databinding.FragmentCourseBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.ui.dialog.AddCourseFragment
import com.example.vocabmanager.ui.dialog.DeleteCourseFragment
import com.example.vocabmanager.ui.dialog.NotifyFragment
import com.example.vocabmanager.ui.dialog.UpdateCourseFragment
import com.example.vocabmanager.viewmodel.CourseViewModel

class CourseFragment : Fragment(), MenuProvider, CourseAdapter.ItemEvent {
    companion object {
        const val TAG = "CourseFragment"
    }

    private var _binding: FragmentCourseBinding? = null

    // This property is only valid between onCreateView ands
    // onDestroyView.
    private val binding get() = _binding!!
    private val courseViewModel: CourseViewModel by viewModels ()
    private lateinit var courseAdapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initSwipeEvent()
        onViewStateRestored(savedInstanceState)
    }

    private fun initSwipeEvent() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                courseAdapter.notifyItemChanged(viewHolder.adapterPosition)
                if (viewHolder is CourseAdapter.ViewHolder) {
                    viewHolder.course?.let {
                        showDeleteCourseDialog(it)
                    }
                }
            }
        }

        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun initObserver() {
        courseViewModel.courseData?.observe(viewLifecycleOwner) {
            courseAdapter.updateCourses(it.toMutableList())
        }

        courseViewModel.progressData?.observe(viewLifecycleOwner) {
            binding.layoutProgress.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun initView() {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        courseAdapter = CourseAdapter(null)
        courseAdapter.event = this
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = courseAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        courseViewModel.setQuery()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                courseViewModel.setQuery(query ?: "")
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.trim()?.isEmpty() == true)
                    courseViewModel.setQuery(newText)
                return true
            }
        })

        with(binding) {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                courseViewModel.setQuery(searchView.query.toString())
            }
        }
    }

    override fun onItemClickListener(course: Course) {
        showUpdateCourseDialog(course)
    }

    override fun onItemLongClickListener(course: Course): Boolean {
        return false
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.course_menu_context, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_add -> {
                showAddCourseDialog()
            }
        }
        return false
    }

    private fun showUpdateCourseDialog(oldCourse: Course) {
        val dialog = UpdateCourseFragment.newInstance(oldCourse)
        dialog.listener = object : UpdateCourseFragment.UpdateCourseListener {
            override fun onSuccess(course: Course) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_update)}\n${course.courseName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                courseAdapter.updateCourse(oldCourse, course)
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
        dialog.show(childFragmentManager, UpdateCourseFragment.TAG)
    }

    private fun showDeleteCourseDialog(course: Course) {
        val dialog = DeleteCourseFragment.newInstance(course)
        dialog.listener = object : DeleteCourseFragment.DelCourseListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_delete)}\n${course.courseName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                courseAdapter.delCourse(course)
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
        dialog.show(childFragmentManager, DeleteCourseFragment.TAG)
        courseAdapter.notifyItemChanged(course)
    }

    private fun showAddCourseDialog() {
        val addCourseFragment = AddCourseFragment()
        addCourseFragment.listener = object : AddCourseFragment.AddCourseListener {
            override fun onSuccess(course: Course) {
                Log.d(TAG, "onSuccess: ")
                addCourseFragment.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_add)}\n${course.courseName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                courseViewModel.setQuery(binding.searchView.query.toString())
//                binding.swipeRefresh.post { binding.swipeRefresh.isRefreshing = true}
//                courseAdapter.addCourse(course)
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
        addCourseFragment.show(childFragmentManager, AddCourseFragment.TAG)
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
        val search = _binding?.searchView?.query?.toString() ?: ""
        Log.d(TAG, "onSaveInstanceState: $search")
        outState.putString("search", search)
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