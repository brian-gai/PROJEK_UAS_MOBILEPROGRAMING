package com.example.eventmanagement.model

/**
 * Data class untuk Event
 * Merepresentasikan satu event dalam sistem
 */
data class Event(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val capacity: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    /**
     * Kompanion object untuk helper functions
     */
    companion object {
        /**
         * Validasi input event sebelum dikirim ke API
         */
        fun validate(
            title: String,
            date: String,
            time: String,
            location: String
        ): String? {
            return when {
                title.isEmpty() -> "Nama event harus diisi"
                date.isEmpty() -> "Tanggal harus diisi"
                time.isEmpty() -> "Waktu harus diisi"
                location.isEmpty() -> "Lokasi harus diisi"
                !isValidDateFormat(date) -> "Format tanggal salah (gunakan YYYY-MM-DD)"
                !isValidTimeFormat(time) -> "Format waktu salah (gunakan HH:MM:SS)"
                else -> null // Valid
            }
        }

        /**
         * Cek format tanggal YYYY-MM-DD
         */
        private fun isValidDateFormat(date: String): Boolean {
            return date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))
        }

        /**
         * Cek format waktu HH:MM:SS
         */
        private fun isValidTimeFormat(time: String): Boolean {
            return time.matches(Regex("^\\d{2}:\\d{2}:\\d{2}$"))
        }
    }

    /**
     * Format tampilan tanggal dan waktu
     */
    fun getFormattedDateTime(): String {
        return "$date | $time"
    }
}