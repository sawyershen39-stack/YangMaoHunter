package com.yangmaolie.hunter.core.extensions

import java.text.SimpleDateFormat
import java.util.*

private val defaultFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
private val dateFormat = SimpleDateFormat("MM-dd", Locale.CHINA)

fun Long.formatDefault(): String {
    return defaultFormat.format(Date(this))
}

fun Long.formatDate(): String {
    return dateFormat.format(Date(this))
}

fun Long.getTimeRemaining(): String {
    val now = System.currentTimeMillis()
    val diff = this - now
    if (diff <= 0) return "已开始"

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "${days}天 ${hours % 24}小时"
        hours > 0 -> "${hours}小时 ${minutes % 60}分钟"
        minutes > 0 -> "${minutes}分钟 ${seconds % 60}秒"
        else -> "${seconds}秒"
    }
}

fun isToday(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance()
    val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = timestamp
    return todayDay == calendar.get(Calendar.DAY_OF_YEAR)
}

fun isTomorrow(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = timestamp
    return tomorrowDay == calendar.get(Calendar.DAY_OF_YEAR)
}
