package com.example.eventmanagement.model

import org.json.JSONObject

/**
 * Data class untuk Statistics
 * Berisi ringkasan jumlah event berdasarkan status
 */
data class Statistics(
    val total: Int,
    val upcoming: Int,
    val ongoing: Int,
    val completed: Int,
    val cancelled: Int
) {
    /**
     * Cek apakah ada event
     */
    fun hasEvents(): Boolean {
        return total > 0
    }

    /**
     * Hitung persentase event per status
     */
    fun getPercentage(count: Int): Float {
        return if (total > 0) (count.toFloat() / total.toFloat()) * 100 else 0f
    }

    /**
     * Dapatkan persentase upcoming events
     */
    fun getUpcomingPercentage(): Float {
        return getPercentage(upcoming)
    }

    /**
     * Dapatkan persentase ongoing events
     */
    fun getOngoingPercentage(): Float {
        return getPercentage(ongoing)
    }

    /**
     * Dapatkan persentase completed events
     */
    fun getCompletedPercentage(): Float {
        return getPercentage(completed)
    }

    /**
     * Dapatkan persentase cancelled events
     */
    fun getCancelledPercentage(): Float {
        return getPercentage(cancelled)
    }

    companion object {
        /**
         * Parse Statistics dari JSON
         */
        fun fromJson(json: JSONObject): Statistics {
            return Statistics(
                total = json.optInt("total", 0),
                upcoming = json.optInt("upcoming", 0),
                ongoing = json.optInt("ongoing", 0),
                completed = json.optInt("completed", 0),
                cancelled = json.optInt("cancelled", 0)
            )
        }

        /**
         * Create empty statistics
         */
        fun empty(): Statistics {
            return Statistics(0, 0, 0, 0, 0)
        }
    }

    override fun toString(): String {
        return "Statistics(total=$total, upcoming=$upcoming, ongoing=$ongoing, completed=$completed, cancelled=$cancelled)"
    }
}