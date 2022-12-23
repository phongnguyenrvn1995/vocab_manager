package com.example.vocabmanager.ui.dialog

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.vocabmanager.R
import com.example.vocabmanager.databinding.FragmentNotifyBinding
import java.io.Serializable


class NotifyFragment : DialogFragment() {

    private var dialogType: DialogType? = null
    private var title: String? = null
    private var message: String? = null
    private lateinit var binding: FragmentNotifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            dialogType = it.getSerializable(ARG_TYPE) as DialogType
        }
    }

    @Suppress("DEPRECATION")
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
        binding = FragmentNotifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    private fun initView() {
        isCancelable = false
        with(binding) {
            root.setOnApplyWindowInsetsListener { _, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
                windowInsets
            }

            when (dialogType) {
                DialogType.SUCCESS -> {
                    icStatus.setImageResource(R.drawable.ic_success_outline)
                    bgStatus.setBackgroundColor(Color.parseColor("#ff669900"))
                }
                DialogType.FAILED -> {
                    icStatus.setImageResource(R.drawable.ic_error_outline)
                    bgStatus.setBackgroundColor(Color.parseColor("#ffff4444"))
                }
                DialogType.WARNING -> {
                    icStatus.setImageResource(R.drawable.ic_warning_outline)
                    bgStatus.setBackgroundColor(Color.parseColor("#ffff8800"))
                }
                else -> {}
            }

            txtTitle.text = title
            txtMessage.text = message

            btnOk.setOnClickListener { dismiss() }
        }
    }

    companion object {
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_TYPE = "ARG_TYPE"
        const val TAG = "SuccessFragment"

        @JvmStatic
        fun newInstance(title: String? = null, message: String? = null, dialogType: DialogType = DialogType.SUCCESS) =
            NotifyFragment().apply {
                arguments = Bundle().apply {
                    title?.let { putString(ARG_TITLE, title) }
                    message?.let { putString(ARG_MESSAGE, message) }
                    putSerializable(ARG_TYPE, dialogType)
                }
            }
    }

    enum class DialogType : Serializable{
        SUCCESS,
        FAILED,
        WARNING
    }
}