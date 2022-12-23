package com.example.vocabmanager.ui.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.R
import com.example.vocabmanager.databinding.FragmentDeleteLessonBinding
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.viewmodel.DeleteLessonViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteLessonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteLessonFragment : DialogFragment() {
    private var lesson: Lesson? = null
    private lateinit var binding: FragmentDeleteLessonBinding
    private val deleteLessonViewModel: DeleteLessonViewModel by viewModels()
    var listener: DelLessonListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param = it.getSerializable(ARG_LESSON)
            if (param is Lesson) {
                this.lesson = param
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
        binding = FragmentDeleteLessonBinding.inflate(layoutInflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initObserver() {
        with(deleteLessonViewModel) {
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
                context?.getString(R.string.dialog_do_you_want_to_delete) + " " + lesson?.lessonName + "?"
            txtContent.text = title

            btnOk.setOnClickListener {
                lesson?.lessonId?.let {
                    processingCircle.visibility = View.VISIBLE
                    deleteLessonViewModel.deleteLesson(it)
                }
            }

            btnCancel.setOnClickListener {
                listener?.onCancelled() ?: run {
                    dismiss()
                }
            }
        }
    }

    companion object {
        const val TAG = "DeleteLessonFragment"
        const val ARG_LESSON = "ARG_LESSON"

        @JvmStatic
        fun newInstance(lesson: Lesson) =
            DeleteLessonFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LESSON, lesson)
                }
            }
    }

    interface DelLessonListener {
        fun onSuccess()
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}