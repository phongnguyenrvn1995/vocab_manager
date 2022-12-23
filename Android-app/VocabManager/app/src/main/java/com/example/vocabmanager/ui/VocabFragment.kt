package com.example.vocabmanager.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.BuildConfig
import com.example.vocabmanager.R
import com.example.vocabmanager.adapter.*
import com.example.vocabmanager.api.consts.APIConsts
import com.example.vocabmanager.databinding.FragmentVocabBinding
import com.example.vocabmanager.entities.*
import com.example.vocabmanager.mapper.ResponseMapping
import com.example.vocabmanager.mapper.UnCaughtExceptionMapping
import com.example.vocabmanager.ui.dialog.AddVocabFragment
import com.example.vocabmanager.ui.dialog.DeleteVocabFragment
import com.example.vocabmanager.ui.dialog.NotifyFragment
import com.example.vocabmanager.ui.dialog.UpdateVocabFragment
import com.example.vocabmanager.viewmodel.CourseViewModel
import com.example.vocabmanager.viewmodel.LessonViewModel
import com.example.vocabmanager.viewmodel.PoSViewModel
import com.example.vocabmanager.viewmodel.VocabViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player


class VocabFragment : Fragment(), MenuProvider, VocabAdapter.ItemEvent {
    companion object {
        const val TAG = "VocabFragment"
    }

    private var _binding: FragmentVocabBinding? = null
    private val binding get() = _binding!!
    private val vocabViewModel: VocabViewModel by activityViewModels()
    private val lessonViewModel: LessonViewModel by viewModels()
    private val courseViewModel: CourseViewModel by viewModels()
    private val posViewModel: PoSViewModel by viewModels()
    private lateinit var spinnerCourseAdapter: SpinnerCourseAdapter
    private lateinit var spinnerLessonAdapter: SpinnerLessonAdapter
    private lateinit var spinnerPosAdapter: SpinnerPoSAdapter
    private lateinit var vocabAdapter: VocabAdapter
    private var exoPlayer: ExoPlayer? = null
    private var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var courseSave: Course? = null
    private var courseSelecting: Course? = null
    private var lessonSave: Lesson? = null
    private var lessonSelecting: Lesson? = null
    private var posSave: PoS? = null
    private var posSelecting: PoS? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVocabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewStateRestored(savedInstanceState)
        initView()
        initSwipeEvent()
        initObserver()
    }

    private fun initObserver() {
        lessonViewModel.apply {
            searchData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: $it")
                lessonViewModel.getData(it)
            }
            lessonData?.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: adapter")
                val list = it.toMutableList()
                fillOutCourseName(*list.toTypedArray())
                val allLesson = Lesson(
                    lessonName = context?.getString(R.string.tv_hint_all_lessons),
                )
                var tmp: String? = ""/*
                    courseViewModel.getLocalCourseById(
                        getCurrentSearchBean().course.toIntOrNull() ?: -1
                    )?.courseName*/
                if (tmp == null || tmp.isBlank())
                    tmp = context?.getString(R.string.tv_hint_all_courses)
                allLesson.courseName = tmp
                list.add(0, allLesson)
                Log.d(
                    TAG,
                    "initObserver: lessonViewModel courseSelecting?.courseName = ${courseSelecting?.courseName}, allLesson.courseName = ${allLesson.courseName}"
                )
                if (courseSelecting?.courseName != allLesson.courseName)
                    return@observe


                spinnerLessonAdapter.updateLessons(list.toMutableList())
                binding.spinnerLesson.setSelection(0)

                let {
                    if (lessonSave == null)
                        return@let
                    binding.spinnerLesson.setSelection(spinnerLessonAdapter.indexOf(lessonSave!!))
//                    lessonSave = null
                    Log.d(TAG, "initObserver: lessonViewModel done")
                }
            }
        }

        courseViewModel.apply {
            courseData?.observe(viewLifecycleOwner) {
                val list = it.toMutableList()
                val allCourse = Course(
                    courseName = context?.getString(R.string.tv_hint_all_courses),
                    courseDateCreat = context?.getString(R.string.tv_hint_all_dates)
                )
                list.add(0, allCourse)
                spinnerCourseAdapter.updateCourses(list)
                if (courseSave != null) {
                    binding.spinnerCourse.setSelection(spinnerCourseAdapter.indexOf(courseSave!!))
//                    courseSave = null
                }
            }
        }

        with(posViewModel) {
            searchData?.observe(viewLifecycleOwner) {
                getData(it)
            }

            poSData?.observe(viewLifecycleOwner) {
                val list = it.toMutableList()
                val allPoS = PoS(
                    vocabTypeName = context?.getString(R.string.tv_hint_all_pos),
                )
                list.add(0, allPoS)
                spinnerPosAdapter.updatePoSs(list)
                if(posSave != null) {
                    binding.spinnerPos.setSelection(spinnerPosAdapter.indexOf(posSave!!))
                }
            }
        }

        vocabViewModel.apply {
            searchData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: $it")
                vocabViewModel.getData(it)
            }

            vocabData.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver vocabData: $it")
                if (it == null)
                    return@observe
                val list = it.toMutableList()
                fillOutLessonName(*list.toTypedArray())
                vocabAdapter.updateVocabs(list)
                endlessRecyclerViewScrollListener?.loading = false
            }

            progressData?.observe(viewLifecycleOwner) {
                binding.layoutProgress.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    private fun initSwipeEvent() {
        val swipeToDeleteCallback = object: SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(viewHolder is VocabAdapter.ViewHolder) {
                    viewHolder.vocab?.let {
                        showDeleteVocabDialog(it)
                    }
                }
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun initView() {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        spinnerCourseAdapter =
            SpinnerCourseAdapter(requireActivity(), listOf<Course>())
        spinnerLessonAdapter =
            SpinnerLessonAdapter(requireActivity(), listOf<Lesson>())

        spinnerPosAdapter = SpinnerPoSAdapter(requireActivity(), listOf<PoS>())

        vocabAdapter = VocabAdapter(null)
        vocabAdapter.event = this
        with(binding) {
            val layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.layoutManager = layoutManager
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )

            endlessRecyclerViewScrollListener =
                object : EndlessRecyclerViewScrollListener(layoutManager) {
                    override fun loadMore() {
                        Log.d(TAG, "loadMore: ")
                        vocabViewModel.getData(vocabViewModel.getCurrentSearchBean(), true)
                    }
                }
            recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener!!)

            recyclerView.adapter = vocabAdapter

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                vocabViewModel.refresh()
            }

            spinnerCourse.adapter = spinnerCourseAdapter
            spinnerCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    courseSelecting =
                        spinnerCourseAdapter.getItem(binding.spinnerCourse.selectedItemPosition)
                    Log.d(
                        TAG,
                        "onItemSelected: spinnerCourse.onItemSelectedListener = $courseSelecting"
                    )
                    lessonViewModel.filterCourse(p3.toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

            spinnerLesson.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    this@VocabFragment.lessonSelecting =
                        spinnerLessonAdapter.getItem(binding.spinnerLesson.selectedItemPosition)
                    vocabViewModel.filterLesson(id.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            spinnerPos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    this@VocabFragment.posSelecting =
                        spinnerPosAdapter.getItem(binding.spinnerPos.selectedItemPosition)
                    vocabViewModel.filterPoS(id.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d(TAG, "onQueryTextSubmit: $query")
                    vocabViewModel.searchQuery(query ?: "")
                    searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "onQueryTextChange: $newText")
                    if (newText?.isEmpty() == true)
                        vocabViewModel.searchQuery(newText)
                    return true
                }
            })

            spinnerLesson.adapter = spinnerLessonAdapter
            spinnerPos.adapter = spinnerPosAdapter
        }
        courseViewModel.setQuery("")
    }

    private fun fillOutCourseName(vararg listLesson: Lesson) {
        listLesson.forEach {
            val courseId = it.lessonCourse ?: return
            val course = courseViewModel.getLocalCourseById(courseId)
            it.courseName = course?.courseName
        }
    }

    private fun fillOutLessonName(vararg listVocabs: Vocab) {
        listVocabs.forEach {
            val lessonId = it.vocabLesson ?: return
            val lesson = lessonViewModel.getLocalLessonById(lessonId)
            it.lessonName = lesson?.lessonName
        }
    }

    override fun onItemClickListener(vocab: Vocab) {
        showVocabInfoDialog(vocab)
    }

    override fun onAudioClickListener(vocab: Vocab) {
        Log.d(TAG, "onAudioClickListener: " + APIConsts.BASE_SOUND_URL + "/" + vocab.vocabSoundUrl)
        val mediaItem = when(vocab.vocabSoundUrl) {
            "", null -> MediaItem.fromUri("android.resource://${BuildConfig.APPLICATION_ID}/" + R.raw.no_sound)
            else -> MediaItem.fromUri(APIConsts.BASE_SOUND_URL + vocab.vocabSoundUrl)
        }
        exoPlayer?.addMediaItem(mediaItem)
        exoPlayer?.seekTo(0)
        exoPlayer?.prepare()
    }

    override fun onItemLongClickListener(vocab: Vocab): Boolean {
        showUpdateVocabDialog(vocab)
        return false
    }

    private fun showVocabInfoDialog(vocab: Vocab) {
        val dialog = UpdateVocabFragment.newInstance(vocab)
        dialog.show(childFragmentManager, "${UpdateVocabFragment.TAG} ${UpdateVocabFragment.MODE_VIEW}")
    }

    private fun showUpdateVocabDialog(oldVocab: Vocab) {
        val dialog = UpdateVocabFragment.newInstance(oldVocab, UpdateVocabFragment.MODE_UPDATE)
        dialog.listener = object : UpdateVocabFragment.UpdateVocabListener {
            override fun onSuccess(vocab: Vocab) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_update)}\n${vocab.vocabEn}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                fillOutLessonName(vocab)
                vocabAdapter.updateVocabs(oldVocab, vocab)
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
            }

            override fun onOK() {
                Log.d(TAG, "onOK: ")
            }
        }
        dialog.show(childFragmentManager, "${UpdateVocabFragment.TAG} ${UpdateVocabFragment.MODE_UPDATE}")
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.vocab_menu_context, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_add) {
            showAddVocabDialog()
        }
        return false
    }

    private fun showAddVocabDialog() {
        Log.d(TAG, "showAddVocabDialog: ")
        val dialog = AddVocabFragment.newInstance()
        dialog.listener = object: AddVocabFragment.AddVocabListener {
            override fun onSuccess(vocab: Vocab) {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_add)}\n${vocab.vocabEn}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                vocabViewModel.refresh()
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
            }
        }
        dialog.show(childFragmentManager, AddVocabFragment.TAG)
    }

    private fun showDeleteVocabDialog(vocab: Vocab) {
        val dialog = DeleteVocabFragment.newInstance(vocab)
        dialog.listener = object : DeleteVocabFragment.DelVocabListener {
            override fun onSuccess() {
                Log.d(TAG, "onSuccess: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_success)
                val message =
                    "${context?.getString(R.string.tv_hint_delete)}\n${vocab.vocabEn}\n" +
                            context?.getString(R.string.tv_hint_successfully)
                showSuccessDialog(title, message)
                vocabAdapter.delVocab(vocab)
            }

            override fun onFailed(response: Response) {
                Log.d(TAG, "onFailed: ")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(ResponseMapping.toStringResID(response.responseId))
                showErrorDialog(title, message)
            }

            override fun onError(throwable: Throwable) {
                Log.d(TAG, "onError: $throwable")
                dialog.dismiss()
                val title = context?.getString(R.string.tv_hint_error)
                val message =
                    context?.getString(UnCaughtExceptionMapping.toStringResID(throwable))
                showErrorDialog(title, message)
            }

            override fun onCancelled() {
                Log.d(TAG, "onCancelled: ")
                dialog.dismiss()
            }
        }
        dialog.show(childFragmentManager, DeleteVocabFragment.TAG)
        vocabAdapter.notifyItemChanged(vocab)
    }

    private fun showSuccessDialog(title: String?, message: String?) {
        val notifyFragment = NotifyFragment.newInstance(title ?: "", message ?: "")
        notifyFragment.show(childFragmentManager, NotifyFragment.TAG)
    }

    private fun showErrorDialog(title: String?, message: String?) {
        val notifyFragment =
            NotifyFragment.newInstance(title ?: "", message ?: "", NotifyFragment.DialogType.FAILED)
        notifyFragment.show(childFragmentManager, NotifyFragment.TAG)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val search = _binding?.searchView?.query?.toString()
        val course = courseSelecting
        val lesson = lessonSelecting
        val poS = posSelecting
        Log.d(TAG, "onSaveInstanceState: $search")
        Log.d(TAG, "onSaveInstanceState: $course")
        Log.d(TAG, "onSaveInstanceState: $lesson")
        Log.d(TAG, "poS: $poS")
        outState.putString("search", search ?: "")
        outState.putSerializable("course", course)
        outState.putSerializable("lesson", lesson)
        outState.putSerializable("poS", poS)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val search = savedInstanceState?.getString("search")
        val course = savedInstanceState?.getSerializable("course")
        val lesson = savedInstanceState?.getSerializable("lesson")
        val poS = savedInstanceState?.getSerializable("poS")
        Log.d(TAG, "onViewStateRestored: $search")
        Log.d(TAG, "onViewStateRestored: $course")
        Log.d(TAG, "onViewStateRestored: $lesson")
        handler.postDelayed({
            _binding?.searchView?.setQuery(search, true)
            if (course is Course)
                this.courseSave = course
            if (lesson is Lesson)
                this.lessonSave = lesson
            if (poS is PoS)
                this.posSave = poS
        }, 50)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    private val handler = Handler(Looper.myLooper()!!)
}