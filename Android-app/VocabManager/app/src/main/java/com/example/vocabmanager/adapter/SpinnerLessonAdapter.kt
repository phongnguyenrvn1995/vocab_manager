package com.example.vocabmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.vocabmanager.databinding.ItemLessonBinding
import com.example.vocabmanager.databinding.ItemSpinnerLessonViewBinding
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Status

class SpinnerLessonAdapter(val context: Context, private var lessons: List<Lesson?>) :
    BaseAdapter() {
    fun updateLessons(lessons: List<Lesson?>) {
        this.lessons = lessons
        notifyDataSetChanged()
    }

    fun indexOf(lesson: Lesson?) = this.lessons.indexOf(lesson)

    fun indexOf(id: Int?): Int {
        var lesson: Lesson? = null
        this.lessons.filter {
            it?.lessonId == id
        }.forEach {
            lesson = it
            return@forEach
        }
        return indexOf(lesson)
    }

    override fun getCount(): Int {
        return lessons.size
    }

    override fun getItem(position: Int): Lesson? {
        return try {
            lessons[position]
        } catch (ex: Exception) {
            null
        }
    }

    override fun getItemId(position: Int) = lessons[position]?.lessonId?.toLong() ?: -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            viewHolder = ViewHolderItem()
            viewHolder.binding =
                ItemSpinnerLessonViewBinding.inflate(inflater, parent, false)
            view = viewHolder.binding.root
            view.tag = viewHolder
//            Log.d(TAG, "set: convertView NULL $viewHolder")
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolderItem
//            Log.d(TAG, "get: convertView NOT NULL $convertView")
        }

        viewHolder.binding.txtLessonName.text = lessons[position]?.lessonName

        return view
    }


    private lateinit var dropDownViewBinding: ItemLessonBinding
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        dropDownViewBinding = ItemLessonBinding.inflate(inflater, parent, false)
        lessons[position]?.let { lesson ->
            dropDownViewBinding.txtLessonName.text = lesson.lessonName
            dropDownViewBinding.txtCourse.text = lesson.courseName
            dropDownViewBinding.activeDot.setImageResource(
                if (lesson.lessonStatus == Status.ACTIVE)
                    android.R.color.holo_green_dark
                else
                    android.R.color.holo_red_dark
            )
        }
        return dropDownViewBinding.root
    }

    class ViewHolderItem {
        lateinit var binding: ItemSpinnerLessonViewBinding
    }

    companion object {
        const val TAG = "SpinnerLessonAdapter"
    }
}