package com.yangmaolie.hunter.domain.model

data class Deal(
    val dealId: String,
    val title: String,
    val description: String,
    val category: String,
    val subCategory: String,
    val dealType: String,
    val originalPrice: Double,
    val dealPrice: Double,
    val discount: Int,
    val publishTime: Long,
    val startTime: Long,
    val endTime: Long,
    val isUpcoming: Boolean,
    val remindBefore: Int,
    val isOnline: Boolean,
    val locationLat: Double?,
    val locationLng: Double?,
    val address: String,
    val district: String,
    val brandName: String,
    val usageRules: List<String>,
    val actionUrl: String,
    val qrCodeUrl: String,
    val images: List<String>,
    val coverImage: String,
    val viewCount: Int,
    val clickCount: Int,
    val collectCount: Int,
    val popularity: Double,
    val source: String,
    val status: String
) {

    fun isExpired(): Boolean {
        return System.currentTimeMillis() > endTime
    }

    fun isStartingSoon(): Boolean {
        val now = System.currentTimeMillis()
        return startTime > now && startTime - now <= remindBefore * 60 * 1000
    }

    fun getCategoryDisplayName(): String {
        return when(category) {
            "milk_tea" -> "奶茶"
            "restaurant" -> "餐饮"
            "bank" -> "银行"
            "online_shop" -> "网购"
            "food_drink" -> "美食"
            "other" -> "其他"
            else -> category
        }
    }

    companion object {
        const val CATEGORY_MILK_TEA = "milk_tea"
        const val CATEGORY_RESTAURANT = "restaurant"
        const val CATEGORY_BANK = "bank"
        const val CATEGORY_ONLINE_SHOP = "online_shop"
        const val CATEGORY_FOOD_DRINK = "food_drink"
        const val CATEGORY_OTHER = "other"

        val ALL_CATEGORIES = listOf(
            CATEGORY_MILK_TEA,
            CATEGORY_RESTAURANT,
            CATEGORY_BANK,
            CATEGORY_ONLINE_SHOP,
            CATEGORY_FOOD_DRINK,
            CATEGORY_OTHER
        )
    }
}
