package com.example.vocabmanager.mapper

import com.example.vocabmanager.R
import com.example.vocabmanager.entities.Response

class ResponseMapping {
    companion object {
        fun toStringResID(code: Int?): Int = when(code) {
            Response.SUCCESS -> R.string.resp_success
            Response.DEL_COURSE_ERROR_EXISTING_LESSON_IN_COURSE -> R.string.resp_course_has_lessons
            Response.DEL_LESSON_ERROR_EXISTING_VOCAB_IN_LESSON -> R.string.resp_lesson_has_vocabs
            Response.DEL_STATUS_ERROR_EXISTING_COURSE_USES_STATUS -> R.string.resp_some_courses_use_this_status
            Response.DEL_STATUS_ERROR_EXISTING_LESSON_USES_STATUS -> R.string.resp_some_lessons_use_this_status
            Response.DEL_VOCAB_TYPE_ERROR_EXISTING_VOCAB_USES_TYPE -> R.string.resp_some_vocabs_use_this_type
            Response.ERROR_STATUS_DOES_NOT_EXIST -> R.string.resp_status_not_exist
            Response.ERROR_COURSE_DOES_NOT_EXIST -> R.string.resp_course_not_exist
            Response.ERROR_LESSON_DOES_NOT_EXIST -> R.string.resp_lesson_not_exist
            Response.ERROR_TYPE_DOES_NOT_EXIST -> R.string.resp_type_not_exist
            Response.ERROR_DB_ERROR -> R.string.resp_host_db_error
            else -> R.string.resp_unknown_error
        }
    }
}