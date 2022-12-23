package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PoS(
    @SerializedName("vocab_type_id") var vocabTypeId: Int? = null,
    @SerializedName("vocab_type_name") var vocabTypeName: String? = null
) : Serializable