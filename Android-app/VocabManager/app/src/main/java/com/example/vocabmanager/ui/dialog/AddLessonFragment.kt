package com.example.vocabmanager.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.vocabmanager.adapter.SpinnerCourseAdapter
import com.example.vocabmanager.databinding.FragmentAddLessonBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.viewmodel.AddLessonViewModel
import com.example.vocabmanager.viewmodel.CourseViewModel

class AddLessonFragment : DialogFragment() {
    private var _binding: FragmentAddLessonBinding? = null
    private val binding: FragmentAddLessonBinding get() = _binding!!
    private val addLessonViewModel: AddLessonViewModel by viewModels()
    private val courseViewModel: CourseViewModel by activityViewModels()
    private lateinit var spinnerCourseAdapter: SpinnerCourseAdapter
    var listener: AddLessonListener? = null

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
        } else {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        _binding = FragmentAddLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            txtLessonNameStatus.visibility = View.GONE
            txtCourseDescStatus.visibility = View.GONE
            hideGlobalMessage()
            processingCircle.visibility = View.GONE
        }
    }

    private fun initObserver() {
        courseViewModel.apply {
            courseData?.observe(requireActivity()) {
                spinnerCourseAdapter.updateCourses(it.toMutableList())
            }
        }

        with(addLessonViewModel) {
            lessonValidateData.observe(requireActivity()) {
                binding.apply {
                    if (it.isNamePassed)
                        txtLessonNameStatus.visibility = View.GONE
                    else
                        txtLessonNameStatus.visibility = View.VISIBLE

                    if (it.isCourseIdPassed)
                        txtCourseDescStatus.visibility = View.GONE
                    else
                        txtCourseDescStatus.visibility = View.VISIBLE

                    if (!it.isPassed())
                        processingCircle.visibility = View.GONE
                }
            }

            respData.observe(requireActivity()) {
                val messageID = ResponseMapping.toStringResID(it.responseId)
                if (it.responseId == Response.SUCCESS) {
                    showSuccessMessage(messageID)
                } else {
                    listener ?: showFailedMessage(messageID)
                    listener?.onFailed(it)
                }
                binding.processingCircle.visibility = View.GONE
            }

            lessonData.observe(requireActivity()) {
                listener?.onSuccess(it)
            }

            errorData.observe(requireActivity()) {
                listener ?: showFailedMessage(UnCaughtExceptionMapping.toStringResID(it))
                listener?.onError(it)
                binding.processingCircle.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        isCancelable = false
        spinnerCourseAdapter =
            SpinnerCourseAdapter(requireActivity(), listOf<Course>())

        with(binding) {
            root.setOnApplyWindowInsetsListener { _, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
                windowInsets
            }

            btnCancel.setOnClickListener {
                listener?.onCancelled()
                dismiss()
            }
            btnOk.setOnClickListener {
                Log.d(AddCourseFragment.TAG, "initView: ")
                hideSoftKeyboard()
                with(binding) {
                    addLessonViewModel.saveLesson(
                        txtLessonName.text.toString(),
                        spinnerCourse.selectedItemId.toInt(),
                        radioActive.isChecked
                    )
                    processingCircle.visibility = View.VISIBLE
                    hideGlobalMessage()
                }
            }

            spinnerCourse.adapter = spinnerCourseAdapter
//            spinnerCourse.onItemSelectedListener = this@AddLessonFragment
        }

    }

    private fun showSuccessMessage(resID: Int) {
        with(binding.txtGlobalStatus) {
            setTextColor(Color.GREEN)
            setText(resID)
            visibility = View.VISIBLE
        }
    }

    private fun showFailedMessage(resID: Int) {
        with(binding.txtGlobalStatus) {
            setTextColor(Color.RED)
            setText(resID)
            visibility = View.VISIBLE
        }
    }

    private fun hideGlobalMessage() {
        binding.txtGlobalStatus.visibility = View.GONE
    }

    private fun hideSoftKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {
        const val TAG = "AddLessonFragment"
    }

    interface AddLessonListener {
        fun onSuccess(lesson: Lesson)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}