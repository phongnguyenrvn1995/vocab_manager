package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Vocab (
    @SerializedName("vocab_id"          ) var vocabId          : Int?    = null,
    @SerializedName("vocab_type"        ) var vocabType        : Int?    = null,// 5
    @SerializedName("vocab_lesson"      ) var vocabLesson      : Int?    = null,// 7
    @SerializedName("vocab_en"          ) var vocabEn          : String? = null,// 1
    @SerializedName("vocab_ipa"         ) var vocabIpa         : String? = null,// 2
    @SerializedName("vocab_vi"          ) var vocabVi          : String? = null,// 4
    @SerializedName("vocab_description" ) var vocabDescription : String? = null,// 3
    @SerializedName("vocab_sound_url"   ) var vocabSoundUrl    : String? = null//  6
): Serializable {
    var lessonName: String? = null
}