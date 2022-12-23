package com.example.vocabmanager.entities.validate

data class PoSValidate(
    var isNamePassed: Boolean = true
) {
    fun isPassed(): Boolean {
        return isNamePassed
    }
}