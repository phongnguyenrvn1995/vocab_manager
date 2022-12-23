package com.example.vocabmanager.ui.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.R
import com.example.vocabmanager.databinding.FragmentDeleteCourseBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.viewmodel.DeleteCourseViewModel


@Suppress("DEPRECATION")
class DeleteCourseFragment : DialogFragment() {
    private var course: Course? = null
    private val deleteCourseViewModel: DeleteCourseViewModel by viewModels()
    private lateinit var binding: FragmentDeleteCourseBinding
    var listener: DelCourseListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param = it.getSerializable(ARG_COURSE)
            if (param is Course) {
                this.course = param
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
        } else {
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
        binding = FragmentDeleteCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            processingCircle.visibility = View.GONE
        }
    }

    private fun initView() {
        with(binding) {
            root.setOnApplyWindowInsetsListener { _, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
                windowInsets
            }
            val title =
                context?.getString(R.string.dialog_do_you_want_to_delete) + " " + course?.courseName + "?"
            txtContent.text = title

            btnOk.setOnClickListener {
                course?.courseId?.let {
                    processingCircle.visibility = View.VISIBLE
                    deleteCourseViewModel.deleteCourse(it)
                }
            }

            btnCancel.setOnClickListener {
                listener?.onCancelled() ?: run {
                    dismiss()
                }
            }
        }
    }

    private fun initObserver() {
        with(deleteCourseViewModel) {
            with(binding) {
                respData.observe(requireActivity()) {
                    Log.d(TAG, "initObserver: $it")
                    processingCircle.visibility = View.GONE
                    if (it.responseId != Response.SUCCESS)
                        listener?.onFailed(it)
                    else
                        listener?.onSuccess()
                    listener ?: run {
                        dismiss()
                    }
                }

                errorData.observe(requireActivity()) {
                    Log.d(TAG, "initObserver: $it")
                    listener?.onError(it)
                    processingCircle.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        const val ARG_COURSE = "ARG_COURSE"
        const val TAG = "DeleteCourseFragment"

        @JvmStatic
        fun newInstance(course: Course? = null) =
            DeleteCourseFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_COURSE, course)
                }
            }
    }

    interface DelCourseListener {
        fun onSuccess()
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}