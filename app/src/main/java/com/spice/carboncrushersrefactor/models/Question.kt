package com.spice.carboncrushersrefactor.models

data class Question(
        val type: Int,
        val id: QuestionID,
        val primaryText: String? = null,
        val secondaryText: String? = null,
        val altText: String? = null,
        val inputUnit: String? = null,
        val inputIcon: Int? = null,
        val imageResource: Int? = null
) {
    companion object {
        const val TYPE_SINGLE_INPUT = 0
        const val TYPE_MULTIPLE_INPUT = 1
        const val TYPE_OPTIONAL_INPUT = 2

        enum class QuestionID {
            Q_MEAT,
            Q_PACKAGED_FOOD,
            Q_NON_LOCAL_PRODUCE,
            Q_TRAVEL,
            Q_ELECTRIC,
            Q_GAS
        }
    }
}