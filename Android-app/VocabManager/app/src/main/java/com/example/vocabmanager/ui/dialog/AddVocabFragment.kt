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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.vocabmanager.adapter.SpinnerLessonAdapter
import com.example.vocabmanager.adapter.SpinnerPoSAdapter
import com.example.vocabmanager.databinding.FragmentAddVocabBinding
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Vocab
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.viewmodel.AddVocabViewModel
import com.example.vocabmanager.viewmodel.LessonViewModel
import com.example.vocabmanager.viewmodel.PoSViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

/**
 * A simple [Fragment] subclass.
 * Use the [AddVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVocabFragment : DialogFragment() {
    private lateinit var binding: FragmentAddVocabBinding
    private val poSViewModel: PoSViewModel by activityViewModels()
    private lateinit var spinnerPosAdapter: SpinnerPoSAdapter
    private val lessonViewModel: LessonViewModel by activityViewModels()
    private lateinit var spinnerLessonAdapter: SpinnerLessonAdapter

    private val addVocabViewModel: AddVocabViewModel by viewModels()

    var listener: AddVocabListener? = null
    private var exoPlayer: ExoPlayer? = null

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
        binding = FragmentAddVocabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initObserver() {
        with(poSViewModel) {
            searchData?.observe(viewLifecycleOwner) {
                getData(it)
            }

            poSData?.observe(viewLifecycleOwner) {
                spinnerPosAdapter.updatePoSs(it.toMutableList())
            }
        }

        with(lessonViewModel) {
            searchData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: $it")
                lessonViewModel.getData(it)
            }

            lessonData?.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: adapter")
                spinnerLessonAdapter.updateLessons(it.toMutableList())
            }
        }

        with(addVocabViewModel) {
            vocabValidateData.observe(requireActivity()) {
                binding.apply {
                    if (it.isEnPassed)
                        txtVocabEnStatus.visibility = View.GONE
                    else
                        txtVocabEnStatus.visibility = View.VISIBLE

                    if (it.isIpaPassed)
                        txtVocabIpaStatus.visibility = View.GONE
                    else
                        txtVocabIpaStatus.visibility = View.VISIBLE

                    if (it.isDescPassed)
                        txtVocabDescStatus.visibility = View.GONE
                    else
                        txtVocabDescStatus.visibility = View.VISIBLE

                    if (it.isViPassed)
                        txtVocabViStatus.visibility = View.GONE
                    else
                        txtVocabViStatus.visibility = View.VISIBLE

                    if (it.isSoundPassed)
                        txtVocabSoundStatus.visibility = View.GONE
                    else
                        txtVocabSoundStatus.visibility = View.VISIBLE

                    if (it.isPOSPassed)
                        txtVocabPosStatus.visibility = View.GONE
                    else
                        txtVocabPosStatus.visibility = View.VISIBLE

                    if (it.isLessonPassed)
                        txtVocabLessonStatus.visibility = View.GONE
                    else
                        txtVocabLessonStatus.visibility = View.VISIBLE

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

            vocabData.observe(requireActivity()) {
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
        spinnerPosAdapter = SpinnerPoSAdapter(requireActivity(), listOf<PoS>())
        spinnerLessonAdapter = SpinnerLessonAdapter(requireActivity(), listOf())

        with(binding) {
            root.setOnApplyWindowInsetsListener { _, windowInsets ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                    binding.root.setPadding(0, 0, 0, imeHeight)
                }
                windowInsets
            }
            isCancelable = false
            txtVocabSound.keyListener = null

            btnOk.setOnClickListener {
                hideSoftKeyboard()
                addVocabViewModel.saveVocab(
                    txtVocabEn.text.toString(),
                    txtVocabIpa.text.toString(),
                    txtVocabDesc.text.toString(),
                    txtVocabVi.text.toString(),
                    txtVocabSound.text.toString(),
                    spinnerPos.selectedItemId.toInt(),
                    spinnerLesson.selectedItemId.toInt()
                )
                processingCircle.visibility = View.VISIBLE
                hideGlobalMessage()
            }

            btnCancel.setOnClickListener {
                listener?.onCancelled()
                dismiss()
            }

            btnSound.setOnClickListener {
            }

            exoPlayer = ExoPlayer.Builder(requireActivity()).build().also {
                it.playWhenReady = true
                it.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        Log.d(TAG, "onPlaybackStateChanged: playbackState $playbackState")
                        if (Player.STATE_ENDED == playbackState) {
                            with(it) {
                                it.removeMediaItems(0, mediaItemCount)
                            }
                        }
                    }
                })
            }

            spinnerPos.adapter = spinnerPosAdapter
            spinnerLesson.adapter = spinnerLessonAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
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
        const val TAG = "AddVocabFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AddVocabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AddVocabFragment()
    }

    interface AddVocabListener {
        fun onSuccess(vocab: Vocab)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
    }
}