package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Course(
    @SerializedName("course_id"          ) var courseId          : Int?    = null,
    @SerializedName("course_name"        ) var courseName        : String? = null,
    @SerializedName("course_description" ) var courseDescription : String? = null,
    @SerializedName("course_date_creat"  ) var courseDateCreat   : String? = null,
    @SerializedName("course_status"      ) var courseStatus      : Int?    = null
) : Serializable