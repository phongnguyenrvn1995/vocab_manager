package com.example.vocabmanager.api.consts

data class CommonException(val msg: String) : Exception(msg) {
    companion object {
        const val HOST_RESP_BODY_NULL = "HOST_RESP_BODY_NULL"
    }
}