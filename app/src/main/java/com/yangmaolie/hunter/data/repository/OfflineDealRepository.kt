package com.yangmaolie.hunter.data.repository

import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.repository.DealRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineDealRepository @Inject constructor() : DealRepository {

    private val sampleDeals = listOf(
        Deal(
            dealId = "sample1",
            title = "喜茶国庆限定奶茶买一送一",
            description = "国庆期间喜茶所有限定款奶茶买一送一，带上朋友一起来喝吧！活动仅限线下门店参与。",
            category = "milk_tea",
            subCategory = "限定优惠",
            dealType = "coupon",
            originalPrice = 28.0,
            dealPrice = 14.0,
            discount = 50,
            publishTime = System.currentTimeMillis() - 86400000,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 2592000000,
            isUpcoming = false,
            remindBefore = 15,
            isOnline = false,
            locationLat = 22.5431,
            locationLng = 114.0579,
            address = "深圳市南山区科技园",
            district = "南山区",
            brandName = "喜茶",
            usageRules = listOf(
                "每人限参与一次",
                "不与其他优惠叠加",
                "仅限堂食"
            ),
            actionUrl = "",
            qrCodeUrl = "",
            images = emptyList(),
            coverImage = "",
            viewCount = 128,
            clickCount = 95,
            collectCount = 42,
            popularity = 85.0,
            source = "用户投稿",
            status = "approved"
        ),
        Deal(
            dealId = "sample2",
            title = "招商银行新户开户送100元红包",
            description = "招商银行最新活动，新用户开户并完成一笔交易即可领取100元现金红包，秒到账。",
            category = "bank",
            subCategory = "新户礼",
            dealType = "cashback",
            originalPrice = 0.0,
            dealPrice = 0.0,
            discount = 100,
            publishTime = System.currentTimeMillis() - 172800000,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 7776000000,
            isUpcoming = false,
            remindBefore = 15,
            isOnline = true,
            locationLat = null,
            locationLng = null,
            address = "",
            district = "",
            brandName = "招商银行",
            usageRules = listOf(
                "新用户才可参与",
                "需要完成风险评估",
                "红包7个工作日内到账"
            ),
            actionUrl = "",
            qrCodeUrl = "",
            images = emptyList(),
            coverImage = "",
            viewCount = 256,
            clickCount = 188,
            collectCount = 76,
            popularity = 92.0,
            source = "官方活动",
            status = "approved"
        ),
        Deal(
            dealId = "sample3",
            title = "海底捞大学生9折优惠",
            description = "持本人学生证即可享受海底捞整单9折优惠，节假日通用。记得提前在支付宝认证学生身份。",
            category = "restaurant",
            subCategory = "会员优惠",
            dealType = "discount",
            originalPrice = 200.0,
            dealPrice = 180.0,
            discount = 10,
            publishTime = System.currentTimeMillis() - 259200000,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 31536000000,
            isUpcoming = false,
            remindBefore = 15,
            isOnline = false,
            locationLat = 22.5350,
            locationLng = 114.0450,
            address = "海底捞全国门店",
            district = "",
            brandName = "海底捞",
            usageRules = listOf(
                "需要出示学生证",
                "支付宝学生认证即可",
                "锅底酒水不参与"
            ),
            actionUrl = "",
            qrCodeUrl = "",
            images = emptyList(),
            coverImage = "",
            viewCount = 312,
            clickCount = 245,
            collectCount = 108,
            popularity = 95.0,
            source = "长期活动",
            status = "approved"
        ),
        Deal(
            dealId = "sample4",
            title = "淘宝双11预售定金立减",
            description = "双11预售活动开启，付定金立减20%-50%，提前抢购心仪商品。",
            category = "online_shop",
            subCategory = "大促活动",
            dealType = "pre-sale",
            originalPrice = 599.0,
            dealPrice = 399.0,
            discount = 33,
            publishTime = System.currentTimeMillis() - 432000000,
            startTime = System.currentTimeMillis() + 1296000000,
            endTime = System.currentTimeMillis() + 2592000000,
            isUpcoming = true,
            remindBefore = 60,
            isOnline = true,
            locationLat = null,
            locationLng = null,
            address = "",
            district = "",
            brandName = "淘宝",
            usageRules = listOf(
                "定金不可退",
                "11月1日付尾款",
                "支持红包叠加"
            ),
            actionUrl = "",
            qrCodeUrl = "",
            images = emptyList(),
            coverImage = "",
            viewCount = 423,
            clickCount = 312,
            collectCount = 156,
            popularity = 88.0,
            source = "官方活动",
            status = "approved"
        ),
        Deal(
            dealId = "sample5",
            title = "肯德基早餐套餐9.9元",
            description = "肯德基超值早餐套餐，豆浆+油条只要9.9元，每天早上10点前供应。",
            category = "food_drink",
            subCategory = "早餐优惠",
            dealType = "set-meal",
            originalPrice = 16.0,
            dealPrice = 9.9,
            discount = 38,
            publishTime = System.currentTimeMillis() - 518400000,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 15552000000,
            isUpcoming = false,
            remindBefore = 15,
            isOnline = false,
            locationLat = 22.5500,
            locationLng = 114.0600,
            address = "肯德基全国门店",
            district = "",
            brandName = "肯德基",
            usageRules = listOf(
                "仅限早餐时段（6:00-10:00）",
                "部分门店不参与",
                "以门店实际供应为准"
            ),
            actionUrl = "",
            qrCodeUrl = "",
            images = emptyList(),
            coverImage = "",
            viewCount = 189,
            clickCount = 134,
            collectCount = 58,
            popularity = 78.0,
            source = "长期优惠",
            status = "approved"
        )
    )

    override suspend fun getAllApprovedDeals(): List<Deal> {
        return sampleDeals
    }

    override suspend fun getDealsByCategory(category: String): List<Deal> {
        return sampleDeals.filter { it.category == category }
    }

    override suspend fun getUpcomingDeals(): List<Deal> {
        val now = System.currentTimeMillis()
        return sampleDeals.filter { it.isUpcoming && it.startTime > now }
    }

    override suspend fun getDealById(dealId: String): Deal? {
        return sampleDeals.find { it.dealId == dealId }
    }

    override suspend fun getDealsByDateRange(start: Long, end: Long): List<Deal> {
        return sampleDeals.filter { it.startTime in start..end }
    }
}
