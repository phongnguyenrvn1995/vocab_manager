package com.example.vocabmanager.entities.validate

data class CourseValidate(
    var isNamePassed: Boolean = true,
    var isDescPassed: Boolean = true,
    var isDateCreatePassed: Boolean = true,
    var isStatusPassed: Boolean = true
) {
    fun isPassed(): Boolean {
        return isNamePassed &&
                isDescPassed &&
                isDateCreatePassed &&
                isStatusPassed
    }
}