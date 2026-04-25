package com.yangmaolie.hunter.presentation.ui.onboard

import com.yangmaolie.hunter.domain.model.Deal

data class OnboardQuestion(
    val question: String,
    val hint: String,
    val options: List<Option>,
    val type: QuestionType // multi-choice or single-choice
) {
    data class Option(
        val label: String,
        val value: String
    )

    enum class QuestionType {
        MULTI_CHOICE,
        SINGLE_CHOICE,
        DISTRICT_SELECT
    }

    companion object {
        fun getQuestions(): List<OnboardQuestion> {
            return listOf(
                OnboardQuestion(
                    question = "你喜欢哪些类型的优惠？",
                    hint = "可多选，我们会根据你的选择推荐合适的内容",
                    options = listOf(
                        Option("奶茶", Deal.CATEGORY_MILK_TEA),
                        Option("餐饮", Deal.CATEGORY_RESTAURANT),
                        Option("银行活动", Deal.CATEGORY_BANK),
                        Option("线上网购", Deal.CATEGORY_ONLINE_SHOP),
                        Option("美食零食", Deal.CATEGORY_FOOD_DRINK),
                        Option("其他优惠", Deal.CATEGORY_OTHER)
                    ),
                    type = QuestionType.MULTI_CHOICE
                ),
                OnboardQuestion(
                    question = "你更关注什么价位的优惠？",
                    hint = "可多选",
                    options = listOf(
                        Option("10元以下", "low"),
                        Option("10-30元", "medium"),
                        Option("30-100元", "high"),
                        Option("100元以上", "very_high")
                    ),
                    type = QuestionType.MULTI_CHOICE
                ),
                OnboardQuestion(
                    question = "你主要在深圳哪个区域活动？",
                    hint = "我们会优先推荐你附近的线下优惠",
                    options = listOf(
                        Option("南山区", "南山区"),
                        Option("福田区", "福田区"),
                        Option("罗湖区", "罗湖区"),
                        Option("宝安区", "宝安区"),
                        Option("龙华区", "龙华区"),
                        Option("龙岗区", "龙岗区"),
                        Option("盐田区", "盐田区"),
                        Option("坪山区", "坪山区"),
                        Option("光明区", "光明区"),
                        Option("大鹏新区", "大鹏新区")
                    ),
                    type = QuestionType.SINGLE_CHOICE
                )
            )
        }
    }
}
