package com.example.vocabmanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.R
import com.example.vocabmanager.adapter.LessonAdapter
import com.example.vocabmanager.adapter.SpinnerCourseAdapter
import com.example.vocabmanager.adapter.SpinnerStatusAdapter
import com.example.vocabmanager.adapter.notifyItemChanged
import com.example.vocabmanager.databinding.FragmentLessonBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Status
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.ui.dialog.*
import com.example.vocabmanager.viewmodel.CourseViewModel
import com.example.vocabmanager.viewmodel.LessonViewModel
import com.example.vocabmanager.viewmodel.StatusViewModel

class LessonFragment : Fragment(), MenuProvider, AdapterView.OnItemSelectedListener, LessonAdapter.ItemEvent {
    companion object {
        const val TAG = "LessonFragment"
    }

    private var _binding: FragmentLessonBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val lessonViewModel: LessonViewModel by activityViewModels()
    private val courseViewModel: CourseViewModel by activityViewModels()
    private val statusViewModel: StatusViewModel by activityViewModels()
    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var spinnerCourseAdapter: SpinnerCourseAdapter
    private lateinit var spinnerStatusAdapter: SpinnerStatusAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initSwipeEvent()
        initObserver()
        onViewStateRestored(savedInstanceState)
    }

    private fun initSwipeEvent() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder is LessonAdapter.ViewHolder) {
                    viewHolder.lesson?.let {
                        showDeleteLessonDialog(it)
                    }
                }
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun initObserver() {
        lessonViewModel.apply {
            searchData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: $it")
                lessonViewModel.getData(it)
            }

            lessonData?.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: adapter")
                fillOutCourseName(*it.toTypedArray())
                lessonAdapter.updateLessons(it.toMutableList())
            }

            progressData?.observe(viewLifecycleOwner) {
                binding.layoutProgress.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        courseViewModel.apply {
            courseData?.observe(viewLifecycleOwner) {
                val list = it.toMutableList()
                val allCourse = Course(
                    courseName = context?.getString(R.string.tv_hint_all_courses),
                    courseDateCreat = context?.getString(R.string.tv_hint_all_dates)
                )
                list.add(0, allCourse)
                spinnerCourseAdapter.updateCourses(list)
            }
        }

        statusViewModel.apply {
            statusData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: $it")
                val list = it.toMutableList()
                val allStatus =
                    Status(statusDescription = context?.getString(R.string.tv_hint_all_status))
                list.add(0, allStatus)
                spinnerStatusAdapter.updateStatus(list)
            }
        }
    }

    private fun fillOutCourseName(vararg listLesson: Lesson) {
        listLesson.forEach {
            val courseId = it.lessonCourse ?: return
            val course = courseViewModel.getLocalCourseById(courseId)
            it.courseName = course?.courseName
        }
    }

    private fun initView() {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        lessonAdapter = LessonAdapter(null)
        lessonAdapter.event = this
        spinnerCourseAdapter =
            SpinnerCourseAdapter(requireActivity(), listOf<Course>())
        spinnerStatusAdapter = SpinnerStatusAdapter(requireActivity(), listOf())
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recyclerView.adapter = lessonAdapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                lessonViewModel.getData(lessonViewModel.getCurrentSearchBean())
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "onQueryTextSubmit: $query")
                    lessonViewModel.searchQuery(query ?: "")
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "onQueryTextChange: $newText")
                    if (newText?.isEmpty() == true)
                        lessonViewModel.searchQuery(newText)
                    return true
                }
            })

            spinnerCourse.adapter = spinnerCourseAdapter
            spinnerCourse.onItemSelectedListener = this@LessonFragment

            spinnerStatus.adapter = spinnerStatusAdapter
            spinnerStatus.onItemSelectedListener = this@LessonFragment
        }
        courseViewModel.setQuery("")
    }

    override fun onItemSelected(
        spinner: AdapterView<*>?,
        view: View?,
        position: Int,
        itemId: Long
    ) {
        with(binding) {
            if (spinner == spinnerCourse)
                lessonViewModel.filterCourse(itemId.toString())
            else if (spinner == spinnerStatus)
                lessonViewModel.filterStatus(itemId.toString())
        }
    }

    override fun onNothingSelected(spinner: AdapterView<*>?) {
        with(binding) {
            if (spinner == spinnerCourse)
                lessonViewModel.filterCourse("")
            else if (spinner == spinnerStatus)
                lessonViewModel.filterStatus("")
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.lesson_menu_context, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_add -> showAddLessonDialog()
        }
        return false
    }

    override fun onItemClickListener(lesson: Lesson) {
        showUpdateLessonDialog(lesson)
    }

    override fun onItemLongClickListener(lesson: Lesson): Boolean {
        return false
    }

    private fun showAddLessonDialog() {
        val dialog = AddLessonFragment()
        dialog.listener = object : AddLessonFragment.AddLessonListener {
            override fun onSuccess(lesson: Lesson) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_add)}\n${lesson.lessonName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                lessonViewModel.getData(lessonViewModel.getCurrentSearchBean())
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
        dialog.show(childFragmentManager, AddLessonFragment.TAG)
    }

    private fun showDeleteLessonDialog(lesson: Lesson) {
        val dialog = DeleteLessonFragment.newInstance(lesson)
        dialog.listener = object : DeleteLessonFragment.DelLessonListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_delete)}\n${lesson.lessonName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                lessonAdapter.delLesson(lesson)
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_error)
                val     message =
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
        dialog.show(childFragmentManager, DeleteLessonFragment.TAG)
        lessonAdapter.notifyItemChanged(lesson)
    }

    private fun showUpdateLessonDialog(oldLesson: Lesson) {
        val dialog = UpdateLessonFragment.newInstance(oldLesson)
        dialog.listener = object : UpdateLessonFragment.UpdateLessonListener {
            override fun onSuccess(lesson: Lesson) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_update)}\n${lesson.lessonName}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                fillOutCourseName(lesson)
                lessonAdapter.updateLesson(oldLesson, lesson)
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
        dialog.show(childFragmentManager, UpdateLessonFragment.TAG)
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