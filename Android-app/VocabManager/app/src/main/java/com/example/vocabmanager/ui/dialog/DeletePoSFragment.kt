package com.example.vocabmanager.ui.dialog

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.R
import com.example.vocabmanager.databinding.FragmentDeletePosBinding
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.viewmodel.DeletePoSViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [DeletePoSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeletePoSFragment : DialogFragment() {
    private var poS: PoS? = null
    private lateinit var binding: FragmentDeletePosBinding
    private val deletePoSViewModel: DeletePoSViewModel by viewModels()
    var listener: DelPoSListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val obj = it.getSerializable(TAG_POS)
            if (obj is PoS)
                this.poS = obj
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
        binding = FragmentDeletePosBinding.inflate(inflater, container, false)
        return binding.root
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
        with(deletePoSViewModel) {
            with(binding) {
                respData.observe(requireActivity()) {
                    android.util.Log.d(
                        DeleteLessonFragment.TAG,
                        "initObserver: $it"
                    )
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
                    android.util.Log.d(
                        DeleteLessonFragment.TAG,
                        "initObserver: $it"
                    )
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
                context?.getString(R.string.dialog_do_you_want_to_delete) + " " + poS?.vocabTypeName + "?"
            txtContent.text = title

            btnOk.setOnClickListener {
                poS?.vocabTypeId?.let {
                    processingCircle.visibility = View.VISIBLE
                    deletePoSViewModel.deletePoS(it)
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
        const val TAG = "DeletePoSFragment"
        const val TAG_POS = "TAG_POS"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param poS Parameter 1.
         * @return A new instance of fragment DeletePoSFragment.
         */
        @JvmStatic
        fun newInstance(poS: PoS) =
            DeletePoSFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TAG_POS, poS)
                }
            }
    }

    interface DelPoSListener {
        fun onSuccess()
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}