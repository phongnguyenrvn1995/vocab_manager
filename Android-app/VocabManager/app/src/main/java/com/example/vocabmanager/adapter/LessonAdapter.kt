package com.example.vocabmanager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.databinding.ItemLessonBinding
import com.example.vocabmanager.entities.Lesson
import com.example.vocabmanager.entities.Status

class LessonAdapter(var lessons: MutableList<Lesson>?) :
    RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    interface ItemEvent {
        fun onItemClickListener(lesson: Lesson)
        fun onItemLongClickListener(lesson: Lesson): Boolean
    }

    var event: ItemEvent? = null

    class ViewHolder(val binding: ItemLessonBinding, var lesson: Lesson? = null) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lesson = lessons?.get(position)
        holder.lesson = lesson
        holder.binding.apply {
            txtLessonName.text = lesson?.lessonName
            txtCourse.text = lesson?.courseName
            activeDot.setImageResource(
                if (lesson?.lessonStatus == Status.ACTIVE)
                    android.R.color.holo_green_dark
                else
                    android.R.color.holo_red_dark
            )

            lesson?.let {
                root.setOnClickListener {
                    event?.onItemClickListener(lesson)
                }

                root.setOnLongClickListener {
                    event?.onItemLongClickListener(lesson) ?: false
                }
            }
        }
    }

    override fun getItemCount(): Int = lessons?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateLessons(list: MutableList<Lesson>) {
        this.lessons = list
        notifyDataSetChanged()
    }

    fun updateLesson(oldLesson: Lesson, newLesson: Lesson) {
        val index = lessons?.indexOf(oldLesson)
        if (index == null || index == -1)
            return

        this.lessons?.set(index, newLesson)
        this.lessons?.indexOf(newLesson)?.let {
            notifyItemChanged(it)
        }
    }

    fun delLesson(lesson: Lesson) {
        val position = this.lessons?.indexOf(lesson)
        this.lessons?.remove(lesson)
        position?.let {
            notifyItemRemoved(position)
        }
    }
}

fun LessonAdapter.notifyItemChanged(lesson: Lesson?) {
    lessons?.indexOf(lesson)?.let {
        notifyItemChanged(it)
    }
}