package com.example.vocabmanager.entities.validate

data class VocabValidate(
    var isEnPassed: Boolean = true,
    var isIpaPassed: Boolean = true,
    var isDescPassed: Boolean = true,
    var isViPassed: Boolean = true,
    var isSoundPassed: Boolean = true,
    var isPOSPassed: Boolean = true,
    var isLessonPassed: Boolean = true
) {
    fun isPassed(): Boolean {
        return isEnPassed &&
                isIpaPassed &&
                isDescPassed &&
                isViPassed &&
                isSoundPassed &&
                isPOSPassed &&
                isLessonPassed
    }
}