package com.example.vocabmanager.api

import android.widget.EditText
import com.example.vocabmanager.api.consts.APIConsts
import com.example.vocabmanager.entities.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class GlobalUtils {
    companion object {
        val RETROFIT: Retrofit = Retrofit.Builder()
            .baseUrl(APIConsts.BASE_REST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun EditText.setTextEx(text: String?) {
            setText("")
            append(text)
        }
    }

    interface CourseAPI {
        @GET("VocabRestApi/vocab_api/course/gets")
        fun listCourse(@Query("searchStr") searchStr: String): Call<List<Course>>

        @FormUrlEncoded
        @PUT("/VocabRestApi/vocab_api/course/save")
        fun saveCourse(
            @Field("course_name") courseName: String?,
            @Field("course_description") courseDesc: String?,
            @Field("course_date_creat") courseDate: String?,
            @Field("course_status") courseStatus: Int?
        ): Call<Response>

        @DELETE("/VocabRestApi/vocab_api/course/delete/{id}")
        fun deleteCourse(@Path("id") id: Int): Call<Response>

        @FormUrlEncoded
        @POST("/VocabRestApi/vocab_api/course/update")
        fun updateCourse(
            @Field("course_id") course_id: String?,
            @Field("course_name") course_name: String?,
            @Field("course_description") course_description: String?,
            @Field("course_date_creat") course_date_creat: String?,
            @Field("course_status") course_status: String?
        ): Call<Response>
    }

    interface LessonAPI {
        @GET("/VocabRestApi/vocab_api/lesson/get_filter")
        fun listLesson(
            @Query("searchStr") searchStr: String?,
            @Query("courseID") courseID: String?,
            @Query("statusID") statusID: String?
        ): Call<List<Lesson>>

        @FormUrlEncoded
        @PUT("/VocabRestApi/vocab_api/lesson/save")
        fun saveLesson(
            @Field("lesson_name") lesson_name: String?,
            @Field("lesson_course") lesson_course: String?,
            @Field("lesson_status") lesson_status: Int?
        ): Call<Response>

        @DELETE("/VocabRestApi/vocab_api/lesson/delete/{id}")
        fun deleteLesson(@Path("id") id: Int): Call<Response>

        @FormUrlEncoded
        @POST("/VocabRestApi/vocab_api/lesson/update")
        fun updateLesson(
            @Field("lesson_id") lesson_id: String?,
            @Field("lesson_name") lesson_name: String?,
            @Field("lesson_course") lesson_course: String?,
            @Field("lesson_status") lesson_status: Int?
        ): Call<Response>
    }

    interface PoSAPI {
        @GET("/VocabRestApi/vocab_api/vocab_type/gets")
        fun listPoS(@Query("searchStr") searchStr: String?): Call<List<PoS>>

        @FormUrlEncoded
        @PUT("/VocabRestApi/vocab_api/vocab_type/save")
        fun savePoS(@Field("vocab_type_name") vocab_type_name: String?): Call<Response>

        @FormUrlEncoded
        @POST("/VocabRestApi/vocab_api/vocab_type/update")
        fun updatePoS(
            @Field("vocab_type_id") vocab_type_id: String?,
            @Field("vocab_type_name") vocab_type_name: String?
        ): Call<Response>

        @DELETE("/VocabRestApi/vocab_api/vocab_type/delete/{id}")
        fun deletePoS(@Path("id") id: Int): Call<Response>
    }

    interface VocabAPI {
        @GET("/VocabRestApi/vocab_api/vocab/get_filter/{limit}/{offset}")
        fun listVocab(
            @Path("limit") limit: Int,
            @Path("offset") offset: Int,
            @Query("searchStr") searchStr: String?,
            @Query("typeID") typeID: String?,
            @Query("lessonID") lessonID: String?,
        ): Call<List<Vocab>>

        @FormUrlEncoded
        @PUT("/VocabRestApi/vocab_api/vocab/save")
        fun saveVocab(@Field("vocab_type") vocab_type: Int?,
                      @Field("vocab_lesson") vocab_lesson: Int?,
                      @Field("vocab_en") vocab_en: String?,
                      @Field("vocab_ipa") vocab_ipa: String?,
                      @Field("vocab_vi") vocab_vi: String?,
                      @Field("vocab_description") vocab_description: String?,
                      @Field("vocab_sound_url") vocab_sound_url: String?,
        ): Call<Response>

        @FormUrlEncoded
        @POST("/VocabRestApi/vocab_api/vocab/update")
        fun updateVocab(@Field("vocab_id") vocab_id: Int?,
                        @Field("vocab_type") vocab_type: Int?,
                        @Field("vocab_lesson") vocab_lesson: Int?,
                        @Field("vocab_en") vocab_en: String?,
                        @Field("vocab_ipa") vocab_ipa: String?,
                        @Field("vocab_vi") vocab_vi: String?,
                        @Field("vocab_description") vocab_description: String?,
                        @Field("vocab_sound_url") vocab_sound_url: String?,
        ): Call<Response>

        @DELETE("/VocabRestApi/vocab_api/vocab/delete/{id}")
        fun deleteVocab(@Path("id") id: Int): Call<Response>
    }
}