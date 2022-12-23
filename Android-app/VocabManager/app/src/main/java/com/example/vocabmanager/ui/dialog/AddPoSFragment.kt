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
import com.example.vocabmanager.databinding.FragmentAddPosBinding
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.viewmodel.AddPoSViewModel

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddPoSFragment : DialogFragment() {
    private lateinit var binding: FragmentAddPosBinding
    private val addPoSViewModel: AddPoSViewModel by viewModels()
    var listener: AddPoSListener? = null

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
        binding = FragmentAddPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initObserver() {
        with(addPoSViewModel) {
            poSValidateData.observe(requireActivity()) {
                binding.apply {
                    if (it.isNamePassed)
                        txtPosNameStatus.visibility = View.GONE
                    else
                        txtPosNameStatus.visibility = View.VISIBLE

                    if (!it.isPassed())
                        processingCircle.visibility = View.GONE
                }
            }

            respData.observe(requireActivity()) {
                val messageID =
                    com.example.vocabmanager.mapper.ResponseMapping.toStringResID(it.responseId)
                if (it.responseId == Response.SUCCESS) {
                    showSuccessMessage(messageID)
                } else {
                    listener ?: showFailedMessage(messageID)
                    listener?.onFailed(it)
                }
                binding.processingCircle.visibility = View.GONE
            }

            poSData.observe(requireActivity()) {
                listener?.onSuccess(it)
            }

            errorData.observe(requireActivity()) {
                listener ?: showFailedMessage(
                    com.example.vocabmanager.mapper.UnCaughtExceptionMapping.toStringResID(
                        it
                    )
                )
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
                Log.d(AddCourseFragment.TAG, "initView: ")
                hideSoftKeyboard()
                with(binding) {
                    addPoSViewModel.savePoS(
                        txtPosName.text.toString()
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

    override fun onResume() {
        super.onResume()
        with(binding) {
            txtPosNameStatus.visibility = View.GONE
            hideGlobalMessage()
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
        const val TAG = "AddPoSFragment"
    }

    interface AddPoSListener {
        fun onSuccess(poS: PoS)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}