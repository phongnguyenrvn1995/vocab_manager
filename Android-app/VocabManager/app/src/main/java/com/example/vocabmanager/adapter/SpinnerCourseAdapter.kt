package com.example.vocabmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.vocabmanager.databinding.ItemCourseBinding
import com.example.vocabmanager.databinding.ItemSpinnerCourseViewBinding
import com.example.vocabmanager.entities.Course
import com.example.vocabmanager.entities.Status

class SpinnerCourseAdapter(val context: Context, private var courses: List<Course?>) :
    BaseAdapter() {
    fun updateCourses(courses: List<Course?>) {
        this.courses = courses
        notifyDataSetChanged()
    }

    fun indexOf(course: Course) = this.courses.indexOf(course)

    override fun getCount(): Int {
        return courses.size
    }

    override fun getItem(position: Int): Course? {
        return try {
            courses[position]
        } catch (ex: Exception) {
            null
        }
    }

    override fun getItemId(position: Int) = courses[position]?.courseId?.toLong() ?: -1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view: View
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            viewHolder = ViewHolderItem()
            viewHolder.binding =
                ItemSpinnerCourseViewBinding.inflate(inflater, parent, false)
            view = viewHolder.binding.root
            view.tag = viewHolder
//            Log.d(TAG, "set: convertView NULL $viewHolder")
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolderItem
//            Log.d(TAG, "get: convertView NOT NULL $convertView")
        }

        viewHolder.binding.txtCourseName.text = courses[position]?.courseName

        return view
    }


    private lateinit var dropDownViewBinding: ItemCourseBinding
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        dropDownViewBinding = ItemCourseBinding.inflate(inflater, parent, false)
        courses[position]?.let { course ->
            dropDownViewBinding.txtCourseName.text = course.courseName
            dropDownViewBinding.txtCourseDate.text = course.courseDateCreat
            dropDownViewBinding.activeDot.setImageResource(
                if (course.courseStatus == Status.ACTIVE)
                    android.R.color.holo_green_dark
                else
                    android.R.color.holo_red_dark
            )
        }
        return dropDownViewBinding.root
    }

    class ViewHolderItem {
        lateinit var binding: ItemSpinnerCourseViewBinding
    }

    companion object {
        const val TAG = "SpinnerCourseAdapter"
    }
}