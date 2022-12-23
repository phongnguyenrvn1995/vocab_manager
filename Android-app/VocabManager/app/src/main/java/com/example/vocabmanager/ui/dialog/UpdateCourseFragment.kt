package com.example.vocabmanager.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.api.GlobalUtils.Companion.setTextEx
import com.example.vocabmanager.databinding.FragmentUpdateCourseBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Status
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.viewmodel.UpdateCourseViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateCourseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateCourseFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var course: Course? = null
    private lateinit var binding: FragmentUpdateCourseBinding
    var listener: UpdateCourseListener? = null
    private val updateCourseViewModel: UpdateCourseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val obj = it.getSerializable(ARG_COURSE)
            if (obj is Course)
                course = obj
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initObserver()
    }

    private fun initData() {
        binding.apply {
            course?.let {
                txtCourseName.hint = it.courseName
                txtCourseName.setTextEx(it.courseName)
                txtCourseDesc.hint = it.courseDescription
                txtCourseDesc.setTextEx(it.courseDescription)
                if(it.courseStatus == Status.ACTIVE) {
                    radioActive.isChecked = true
                } else {
                    radioDeActive.isChecked = true
                }
            }
        }
    }

    private fun initObserver() {
        updateCourseViewModel.apply {
            courseValidateData.observe(requireActivity()) {
                binding.apply {
                    if(it.isNamePassed)
                        txtCourseNameStatus.visibility = View.GONE
                    else
                        txtCourseNameStatus.visibility = View.VISIBLE

                    if(it.isDescPassed)
                        txtCourseDescStatus.visibility = View.GONE
                    else
                        txtCourseDescStatus.visibility = View.VISIBLE

                    if(!it.isPassed())
                        processingCircle.visibility = View.GONE
                }
            }

            respData.observe(requireActivity()) {
                val messageID = ResponseMapping.toStringResID(it.responseId)
                if(it.responseId == Response.SUCCESS) {
                    showSuccessMessage(messageID)
                } else {
                    showFailedMessage(messageID)
                    listener?.onFailed(it)
                }
                binding.processingCircle.visibility = View.GONE
            }

            courseData.observe(requireActivity()) {
                listener?.onSuccess(it)
            }

            errorData.observe(requireActivity()) {
                listener?.onError(it)
                binding.processingCircle.visibility = View.GONE
            }
        }
    }

    @Suppress("DEPRECATION", "DEPRECATION")
    private fun initView() {
        with(binding) {
            isCancelable = false

            btnCancel.setOnClickListener {
                listener?.onCancelled()
                dismiss()
            }

            btnUpdate.setOnClickListener {
                Log.d(AddCourseFragment.TAG, "initView: ")
                hideSoftKeyboard()
                course?.let {
                        updateCourseViewModel.updateCourse(
                            it.courseId,
                            txtCourseName.text.toString(),
                            txtCourseDesc.text.toString(),
                            it.courseDateCreat,
                            radioActive.isChecked
                        )
                }
                processingCircle.visibility = View.VISIBLE
                hideGlobalMessage()
            }


            root.setOnApplyWindowInsetsListener { _, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
                windowInsets
            }
        }
    }

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
        binding = FragmentUpdateCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            processingCircle.visibility = View.GONE
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
        private const val ARG_COURSE = "ARG_COURSE"
        const val TAG = "UpdateCourseFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param course Parameter 1.
         * @return A new instance of fragment UpdateCourseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(course: Course) =
            UpdateCourseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_COURSE, course)
                }
            }
    }

    interface UpdateCourseListener {
        fun onSuccess(course: Course)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}