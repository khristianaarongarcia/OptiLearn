package com.lokixcz.optilearn.model

data class Badge(
    val badgeId: Int,
    val name: String,
    val icon: String,
    val description: String,
    val levelRequired: Int,
    val isEarned: Boolean = false,
    val earnedDate: Long = 0L
) {
    fun getIconEmoji(): String {
        return icon
    }
    
    fun getFormattedDate(): String {
        if (earnedDate == 0L) return "Not earned yet"
        return java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            .format(java.util.Date(earnedDate))
    }
}
