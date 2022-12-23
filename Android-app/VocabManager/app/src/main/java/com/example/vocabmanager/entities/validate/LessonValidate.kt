package com.example.vocabmanager.entities.validate

data class LessonValidate(
    var isNamePassed: Boolean = true,
    var isCourseIdPassed: Boolean = true,
    var isStatusPassed: Boolean = true
) {
    fun isPassed(): Boolean {
        return isNamePassed &&
                isCourseIdPassed &&
                isStatusPassed
    }
}