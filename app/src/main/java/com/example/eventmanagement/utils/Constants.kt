package com.example.eventmanagement.utils

import com.example.eventmanagement.R

object Constants {

    // ==============================
    // API CONFIG
    // ==============================
    const val BASE_URL = "http://104.248.153.158/event-api/"
    const val API_ENDPOINT = "${BASE_URL}api.php"

    // Timeout (30 detik)
    const val CONNECT_TIMEOUT = 30000
    const val READ_TIMEOUT = 30000

    // ==============================
    // STATUS EVENT
    // ==============================
    const val STATUS_UPCOMING = "upcoming"
    const val STATUS_ONGOING = "ongoing"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_CANCELLED = "cancelled"

    // ==============================
    // FILTER
    // ==============================
    const val FILTER_ALL = "all"   // ⭐ INI YANG DICARI MainActivity

    fun getStatusLabel(status: String): String {
        return when (status) {
            STATUS_UPCOMING -> "Akan Datang"
            STATUS_ONGOING -> "Berlangsung"
            STATUS_COMPLETED -> "Selesai"
            STATUS_CANCELLED -> "Dibatalkan"
            else -> status
        }
    }

    fun getStatusColorRes(status: String): Int {
        return when (status) {
            STATUS_UPCOMING -> R.drawable.bg_status_upcoming
            STATUS_ONGOING -> R.drawable.bg_status_ongoing
            STATUS_COMPLETED -> R.drawable.bg_status_completed
            STATUS_CANCELLED -> R.drawable.bg_status_cancelled
            else -> R.drawable.bg_status_upcoming
        }
    }

    // Untuk network_security_config
    fun getBaseDomain(): String {
        return "104.248.153.158"
    }
}
