package com.example.vocabmanager.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.databinding.FragmentAddCourseBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.viewmodel.AddCourseViewModel

class AddCourseFragment : DialogFragment() {

    companion object {
        const val TAG = "AddCourseFragment"
    }

    private lateinit var binding: FragmentAddCourseBinding
    private val addCourseViewModel: AddCourseViewModel by viewModels()
    var listener: AddCourseListener? = null

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
        binding = FragmentAddCourseBinding.inflate(inflater, container, false)
        return binding.root//inflater.inflate(R.layout.fragment_add_course, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            txtCourseNameStatus.visibility = View.GONE
            txtCourseDescStatus.visibility = View.GONE
            hideGlobalMessage()
            processingCircle.visibility = View.GONE
        }
    }

    private fun initObserver() {
        with(addCourseViewModel) {
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
                    listener ?: showFailedMessage(messageID)
                    listener?.onFailed(it)
                }
                binding.processingCircle.visibility = View.GONE
            }

            courseData.observe(requireActivity()) {
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
        with(binding) {
            btnCancel.setOnClickListener {
                listener?.onCancelled()
                dismiss()
            }
            btnOk.setOnClickListener {
                Log.d(TAG, "initView: ")
                hideSoftKeyboard()
                with(binding) {
                    addCourseViewModel.saveCourse(
                        txtCourseName.text.toString(),
                        txtCourseDesc.text.toString(),
                        radioActive.isChecked
                    )
                    processingCircle.visibility = View.VISIBLE
                    hideGlobalMessage()
                }
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

    interface AddCourseListener {
        fun onSuccess(course: Course)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}