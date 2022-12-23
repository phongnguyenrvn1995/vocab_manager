package com.example.vocabmanager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vocabmanager.databinding.ItemCourseBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Status

class CourseAdapter(var courses: MutableList<Course>?) :
    RecyclerView.Adapter<CourseAdapter.ViewHolder>() {

    interface ItemEvent {
        fun onItemClickListener(course: Course)
        fun onItemLongClickListener(course: Course): Boolean
    }

    var event: ItemEvent? = null

    class ViewHolder(val binding: ItemCourseBinding, var course: Course? = null) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course = courses?.get(position)
        holder.course = course
        holder.binding.apply {
            txtCourseName.text = course?.courseName
            txtCourseDate.text = course?.courseDateCreat
            activeDot.setImageResource(
                if (course?.courseStatus == Status.ACTIVE)
                    android.R.color.holo_green_dark
                else
                    android.R.color.holo_red_dark
            )

            course?.let {
                root.setOnClickListener {
                    event?.onItemClickListener(course)
                }

                root.setOnLongClickListener {
                    event?.onItemLongClickListener(course) ?: false
                }
            }
        }
    }

    override fun getItemCount(): Int = courses?.size ?: 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateCourses(list: MutableList<Course>) {
        this.courses = list
        notifyDataSetChanged()
    }

    fun updateCourse(oldCourse: Course, newCourse: Course) {
        val index = courses?.indexOf(oldCourse)
        if(index == null || index == -1)
            return

        this.courses?.set(index, newCourse)
        this.courses?.indexOf(newCourse)?.let {
            notifyItemChanged(it)
        }
    }
/*    fun addCourse(course: Course) {
        this.courses?.add(course)
        this.courses?.indexOf(course)?.let {
            notifyItemInserted(it)
        }
    }*/

    fun delCourse(course: Course) {
        val position = this.courses?.indexOf(course)
        this.courses?.remove(course)
        position?.let {
            notifyItemRemoved(position)
        }
    }
}

fun CourseAdapter.notifyItemChanged(course: Course?) {
    courses?.indexOf(course)?.let {
        notifyItemChanged(it)
    }
}