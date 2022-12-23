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
import com.example.vocabmanager.BuildConfig
import com.example.vocabmanager.R
import com.example.vocabmanager.adapter.SpinnerLessonAdapter
import com.example.vocabmanager.adapter.SpinnerPoSAdapter
import com.example.vocabmanager.api.GlobalUtils.Companion.setTextEx
import com.example.vocabmanager.api.consts.APIConsts
import com.example.vocabmanager.databinding.FragmentUpdateVocabBinding
import com.example.vocabmanager.entities.PoS
import com.example.vocabmanager.entities.Response
import com.example.vocabmanager.entities.Vocab
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.viewmodel.LessonViewModel
import com.example.vocabmanager.viewmodel.PoSViewModel
import com.example.vocabmanager.viewmodel.UpdateVocabViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateVocabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateVocabFragment : DialogFragment() {
    private var vocab: Vocab? = null
    private var mode: Int = MODE_VIEW
    private lateinit var binding: FragmentUpdateVocabBinding
    private val poSViewModel: PoSViewModel by activityViewModels()
    private lateinit var spinnerPosAdapter: SpinnerPoSAdapter
    private val lessonViewModel: LessonViewModel by activityViewModels()
    private lateinit var spinnerLessonAdapter: SpinnerLessonAdapter
    private val updateVocabViewModel: UpdateVocabViewModel by viewModels()

    var listener: UpdateVocabListener? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val obj = it.getSerializable(ARG_VOCAB)
            if (obj is Vocab)
                this.vocab = obj
            this.mode = it.getInt(ARG_MODE, MODE_VIEW)
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
        binding = FragmentUpdateVocabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initObserver()
    }

    private fun initObserver() {
        binding.apply {
            with(poSViewModel) {
                searchData?.observe(viewLifecycleOwner) {
                    getData(it)
                }

                poSData?.observe(viewLifecycleOwner) {
                    spinnerPosAdapter.updatePoSs(it.toMutableList())
                    vocab?.let { itVocab ->
                        val index = spinnerPosAdapter.indexOf(itVocab.vocabType)
                        spinnerPos.setSelection(index)
                    }
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
                    vocab?.let { itVocab ->
                        val index = spinnerLessonAdapter.indexOf(itVocab.vocabLesson)
                        spinnerLesson.setSelection(index)
                    }
                }
            }

            with(updateVocabViewModel) {
                vocabValidateData.observe(requireActivity()) {
                    if(it.isEnPassed)
                        txtVocabEnStatus.visibility = View.GONE
                    else
                        txtVocabEnStatus.visibility = View.VISIBLE

                    if(it.isIpaPassed)
                        txtVocabIpaStatus.visibility = View.GONE
                    else
                        txtVocabIpaStatus.visibility = View.VISIBLE

                    if(it.isDescPassed)
                        txtVocabDescStatus.visibility = View.GONE
                    else
                        txtVocabDescStatus.visibility = View.VISIBLE

                    if(it.isViPassed)
                        txtVocabViStatus.visibility = View.GONE
                    else
                        txtVocabViStatus.visibility = View.VISIBLE

                    if(it.isSoundPassed)
                        txtVocabSoundStatus.visibility = View.GONE
                    else
                        txtVocabSoundStatus.visibility = View.VISIBLE

                    if(it.isPOSPassed)
                        txtVocabPosStatus.visibility = View.GONE
                    else
                        txtVocabPosStatus.visibility = View.VISIBLE

                    if(it.isLessonPassed)
                        txtVocabLessonStatus.visibility = View.GONE
                    else
                        txtVocabLessonStatus.visibility = View.VISIBLE
                }

                respData.observe(requireActivity()) {
                    val messageID =
                        ResponseMapping.toStringResID(it.responseId)
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
                    listener ?: showFailedMessage(
                        UnCaughtExceptionMapping.toStringResID(
                            it
                        )
                    )
                    listener?.onError(it)
                    binding.processingCircle.visibility = View.GONE
                }
            }
        }
    }

    private fun initData() {
        binding.apply {
            vocab?.let {
                txtVocabEn.setTextEx(it.vocabEn)
                txtVocabIpa.setTextEx(it.vocabIpa)
                txtVocabDesc.setTextEx(it.vocabDescription)
                txtVocabVi.setTextEx(it.vocabVi)
//                spinnerPos.setText(it.vocabVi)
                txtVocabSound.setTextEx(it.vocabSoundUrl)
//                spinnerLesson.setText(it.vocabSoundUrl)
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
            isCancelable = mode == MODE_VIEW
            when (mode) {
                MODE_VIEW -> {
                    btnCancel.visibility = View.GONE
                    btnUpdate.visibility = View.GONE
                    btnOk.visibility = View.VISIBLE
                    btnChoose.visibility = View.GONE

                    txtVocabEn.keyListener = null
                    txtVocabIpa.keyListener = null
                    txtVocabDesc.keyListener = null
                    txtVocabVi.keyListener = null
                    spinnerPos.isEnabled = false
                    spinnerLesson.isEnabled = false
                }
                MODE_UPDATE -> {
                    btnCancel.visibility = View.VISIBLE
                    btnUpdate.visibility = View.VISIBLE
                    btnOk.visibility = View.GONE
                }
            }
            txtVocabSound.keyListener = null

            btnOk.setOnClickListener {
                listener?.onOK()
                dismiss()
            }

            btnCancel.setOnClickListener {
                listener?.onCancelled()
                dismiss()
            }

            btnUpdate.setOnClickListener {
                Log.d(TAG, "initView: ")
                hideSoftKeyboard()
                with(binding) {
                    updateVocabViewModel.updateVocab(
                        vocabId = vocab?.vocabId,
                        vocabEn = txtVocabEn.text.toString(),
                        vocabIpa = txtVocabIpa.text.toString(),
                        vocabDescription = txtVocabDesc.text.toString(),
                        vocabVi = txtVocabVi.text.toString(),
                        vocabSoundUrl = txtVocabSound.text.toString(),
                        vocabType = spinnerPos.selectedItemId.toInt(),
                        vocabLesson = spinnerLesson.selectedItemId.toInt()
                    )

                    processingCircle.visibility = View.VISIBLE
                    hideGlobalMessage()
                }
            }

            btnSound.setOnClickListener {
                playVocabSoundViewMode()
                playVocabSoundUpdateMode()
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

    private fun playVocabSoundViewMode() {
        vocab?.let { itVocab ->
            if (mode == MODE_VIEW) {
                Log.d(
                    TAG,
                    "onAudioClickListener: " + APIConsts.BASE_SOUND_URL + "/" + itVocab.vocabSoundUrl
                )
                val mediaItem = when (itVocab.vocabSoundUrl) {
                    "", null -> MediaItem.fromUri("android.resource://${BuildConfig.APPLICATION_ID}/" + R.raw.no_sound)
                    else -> MediaItem.fromUri(APIConsts.BASE_SOUND_URL + itVocab.vocabSoundUrl)
                }
                exoPlayer?.addMediaItem(mediaItem)
                exoPlayer?.seekTo(0)
                exoPlayer?.prepare()
            }
        }
    }

    private fun playVocabSoundUpdateMode() {
        vocab?.let { itVocab ->
            if (mode == MODE_UPDATE) {
                Log.d(
                    TAG,
                    "onAudioClickListener: " + APIConsts.BASE_SOUND_URL + "/" + itVocab.vocabSoundUrl
                )
            }
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
        const val TAG = "UpdateVocabFragment"
        private const val ARG_VOCAB = "ARG_VOCAB"
        private const val ARG_MODE = "ARG_MODE"
        const val MODE_VIEW = 0
        const val MODE_UPDATE = 1

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param vocab Parameter 1.
         * @param mode
         * @return A new instance of fragment UpdateVocabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(vocab: Vocab, mode: Int = MODE_VIEW) =
            UpdateVocabFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_VOCAB, vocab)
                    putInt(ARG_MODE, mode)
                }
            }
    }

    interface UpdateVocabListener {
        fun onSuccess(vocab: Vocab)
        fun onFailed(response: Response)
        fun onError(throwable: Throwable)
        fun onCancelled()
        fun onOK()
    }
}