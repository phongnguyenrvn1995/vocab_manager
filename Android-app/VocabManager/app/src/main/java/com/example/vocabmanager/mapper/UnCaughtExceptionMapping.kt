package com.example.vocabmanager.mapper

import com.example.vocabmanager.R
import java.net.ConnectException
import java.net.SocketTimeoutException

class UnCaughtExceptionMapping {
    companion object {
        fun toStringResID(throwable: Throwable): Int = when(throwable) {
            is ConnectException -> R.string.tv_hint_connection_failed
            is SocketTimeoutException -> R.string.tv_hint_connection_timeout
            else -> R.string.resp_unknown_error
        }
    }
}