package com.example.vocabmanager.entities

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("status_id") var statusId: Int? = null,
    @SerializedName("status_description") var statusDescription: String? = null
) {
    companion object {
        const val ACTIVE = 0
        const val DE_ACTIVE = 1
    }
}