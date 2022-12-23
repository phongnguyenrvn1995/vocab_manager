package com.example.vocabmanager.ui.dialog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.vocabmanager.R
import com.example.vocabmanager.databinding.FragmentDeleteVocabBinding
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Vocab
import com.example.vocabmanager.viewmodel.DeleteVocabViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteVocabFragment : DialogFragment() {
    private var vocab: Vocab? = null
    private lateinit var binding: FragmentDeleteVocabBinding
    private val deleteVocabViewModel: DeleteVocabViewModel by viewModels()
    var listener: DelVocabListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val obj = it.getSerializable(ARG_VOCAB)
            if(obj is Vocab) {
                this.vocab = obj
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
        binding = FragmentDeleteVocabBinding.inflate(inflater, container, false)
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
        with(deleteVocabViewModel) {
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
                context?.getString(R.string.dialog_do_you_want_to_delete) + " " + vocab?.vocabEn + "?"
            txtContent.text = title

            btnOk.setOnClickListener {
                vocab?.vocabId?.let {
                    processingCircle.visibility = View.VISIBLE
                    deleteVocabViewModel.deleteVocab(it)
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
        const val TAG = "DeleteVocabFragment"
        const val ARG_VOCAB = "ARG_VOCAB"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param vocab Parameter 1.
         * @return A new instance of fragment DeleteVocabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(vocab: Vocab) =
            DeleteVocabFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_VOCAB, vocab)
                }
            }
    }

    interface DelVocabListener {
        fun onSuccess()
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}