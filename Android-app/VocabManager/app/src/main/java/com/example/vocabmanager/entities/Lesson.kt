package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Lesson(
    @SerializedName("lesson_id"     ) var lessonId     : Int?    = null,
    @SerializedName("lesson_course" ) var lessonCourse : Int?    = null,
    @SerializedName("lesson_name"   ) var lessonName   : String? = null,
    @SerializedName("lesson_status" ) var lessonStatus : Int?    = null
) : Serializable {
    var courseName: String? = null
}