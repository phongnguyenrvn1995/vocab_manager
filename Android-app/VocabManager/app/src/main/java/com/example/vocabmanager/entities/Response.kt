package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("response_id"          ) var responseId          : Int?    = null,
    @SerializedName("response_description" ) var responseDescription : String? = null
) {
    companion object {
        const val SUCCESS = 0
        const val DEL_COURSE_ERROR_EXISTING_LESSON_IN_COURSE = 1
        const val DEL_LESSON_ERROR_EXISTING_VOCAB_IN_LESSON = 2
        const val DEL_STATUS_ERROR_EXISTING_COURSE_USES_STATUS = 3
        const val DEL_STATUS_ERROR_EXISTING_LESSON_USES_STATUS = 4
        const val DEL_VOCAB_TYPE_ERROR_EXISTING_VOCAB_USES_TYPE = 5
        const val ERROR_STATUS_DOES_NOT_EXIST = 6
        const val ERROR_COURSE_DOES_NOT_EXIST = 7
        const val ERROR_LESSON_DOES_NOT_EXIST = 8
        const val ERROR_TYPE_DOES_NOT_EXIST = 9
        const val ERROR_DB_ERROR = 99
    }
}