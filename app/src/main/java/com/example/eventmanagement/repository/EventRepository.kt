package com.example.eventmanagement.repository

import com.example.eventmanagement.model.Event
import com.example.eventmanagement.model.Statistics
import com.example.eventmanagement.utils.ApiClient
import com.example.eventmanagement.utils.Constants
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.*

class EventRepository {

    private val TAG = "EventRepository"

    // ============================================================
    // GET ALL EVENTS (DENGAN FILTER PARAMETER)
    // ============================================================
    suspend fun getAllEvents(filter: String? = null): Result<List<Event>> {
        return try {
            // Bangun URL dengan filter jika ada
            val url = buildUrlWithFilter(filter)

            println("[$TAG] Fetching events from: $url")

            val response = ApiClient.get(url)
            val json = JSONObject(response)

            val status = json.optInt("status", -1)
            if (status != 200) {
                val message = json.optString("message", "Unknown error")
                println("[$TAG] API Error: Status $status - $message")
                return Result.failure(Exception("API Error: $message"))
            }

            val dataArray = json.optJSONArray("data") ?: JSONArray()
            val list = parseEventsFromJson(dataArray)

            println("[$TAG] Successfully parsed ${list.size} events")

            Result.success(list)

        } catch (e: Exception) {
            println("[$TAG] Exception in getAllEvents: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Gagal memuat event: ${e.message}"))
        }
    }

    private fun buildUrlWithFilter(filter: String?): String {
        return if (!filter.isNullOrEmpty() && filter != Constants.FILTER_ALL) {
            "${Constants.API_ENDPOINT}?status=$filter"
        } else {
            Constants.API_ENDPOINT
        }
    }

    // ============================================================
    // GET EVENT BY ID
    // ============================================================
    suspend fun getEventById(id: String): Result<Event> {
        return try {
            val url = "${Constants.API_ENDPOINT}?id=$id"
            println("[$TAG] Fetching event by ID from: $url")

            val response = ApiClient.get(url)
            val json = JSONObject(response)

            if (json.getInt("status") != 200) {
                val message = json.optString("message", "Unknown error")
                return Result.failure(Exception(message))
            }

            val dataObj = json.optJSONObject("data")
            if (dataObj == null) {
                return Result.failure(Exception("Data event tidak ditemukan"))
            }

            val event = parseEventFromJson(dataObj)
            Result.success(event)

        } catch (e: Exception) {
            println("[$TAG] Exception in getEventById: ${e.message}")
            Result.failure(Exception("Gagal memuat detail event: ${e.message}"))
        }
    }

    // ============================================================
    // CREATE EVENT — SESUAI DOKUMENTASI DOSEN
    // ============================================================
    suspend fun createEvent(
        title: String,
        date: String,
        time: String,
        location: String,
        description: String,
        capacity: String,
        status: String
    ): Result<Event> {
        return try {
            // Validasi input
            val validation = Event.validate(title, date, time, location)
            if (validation != null) return Result.failure(Exception(validation))

            // Format JSON sesuai dokumentasi API dosen
            val jsonBody = JSONObject().apply {
                put("title", title)
                put("date", date)
                put("time", time)
                put("location", location)
                put("description", description)
                put("capacity", capacity.toIntOrNull() ?: 0)
                put("status", status)
            }

            println("[$TAG] Creating event with data: ${jsonBody.toString()}")

            // Gunakan POST JSON sesuai dokumentasi
            val response = ApiClient.postJson(Constants.API_ENDPOINT, jsonBody.toString())
            val json = JSONObject(response)

            println("[$TAG] Create response: ${json.toString()}")

            val responseStatus = json.optInt("status", -1)
            if (responseStatus != 201 && responseStatus != 200) {
                val message = json.optString("message", "Gagal membuat event")
                return Result.failure(Exception(message))
            }

            val dataObj = json.optJSONObject("data")
            if (dataObj == null) {
                return Result.failure(Exception("Data event tidak diterima dari server"))
            }

            val newEvent = parseEventFromJson(dataObj)
            Result.success(newEvent)

        } catch (e: Exception) {
            println("[$TAG] Exception in createEvent: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Gagal membuat event: ${e.message}"))
        }
    }

    // ============================================================
    // UPDATE EVENT — PUT JSON
    // ============================================================
    suspend fun updateEvent(
        id: String,
        title: String,
        date: String,
        time: String,
        location: String,
        description: String,
        capacity: String,
        status: String
    ): Result<Event> {
        return try {
            val validation = Event.validate(title, date, time, location)
            if (validation != null) return Result.failure(Exception(validation))

            val jsonBody = JSONObject().apply {
                put("title", title)
                put("date", date)
                put("time", time)
                put("location", location)
                put("description", description)
                put("capacity", capacity.toIntOrNull() ?: 0)
                put("status", status)
            }

            val url = "${Constants.API_ENDPOINT}?id=$id"
            println("[$TAG] Updating event at: $url")
            println("[$TAG] Update data: ${jsonBody.toString()}")

            val response = ApiClient.putJson(url, jsonBody.toString())
            val json = JSONObject(response)

            println("[$TAG] Update response: ${json.toString()}")

            if (json.getInt("status") != 200)
                return Result.failure(Exception(json.optString("message")))

            val updated = parseEventFromJson(json.getJSONObject("data"))
            Result.success(updated)

        } catch (e: Exception) {
            println("[$TAG] Exception in updateEvent: ${e.message}")
            Result.failure(Exception("Gagal update event: ${e.message}"))
        }
    }

    // ============================================================
    // DELETE EVENT
    // ============================================================
    suspend fun deleteEvent(id: String): Result<Boolean> {
        return try {
            val url = "${Constants.API_ENDPOINT}?id=$id"
            println("[$TAG] Deleting event at: $url")

            val response = ApiClient.delete(url)
            val json = JSONObject(response)

            println("[$TAG] Delete response: ${json.toString()}")

            if (json.getInt("status") == 200)
                Result.success(true)
            else
                Result.failure(Exception(json.optString("message")))

        } catch (e: Exception) {
            println("[$TAG] Exception in deleteEvent: ${e.message}")
            Result.failure(Exception("Gagal menghapus event: ${e.message}"))
        }
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    suspend fun getStatistics(): Result<Statistics> {
        return try {
            val url = "${Constants.API_ENDPOINT}?stats=1"
            println("[$TAG] Fetching statistics from: $url")

            val response = ApiClient.get(url)
            val json = JSONObject(response)

            println("[$TAG] Statistics response: ${json.toString()}")

            if (json.getInt("status") != 200)
                return Result.failure(Exception("Gagal memuat statistik"))

            val data = json.getJSONObject("data")
            Result.success(Statistics.fromJson(data))

        } catch (e: Exception) {
            println("[$TAG] Exception in getStatistics: ${e.message}")
            Result.failure(Exception("Gagal memuat statistik: ${e.message}"))
        }
    }

    // ============================================================
    // JSON PARSER
    // ============================================================
    private fun parseEventsFromJson(jsonArray: JSONArray): List<Event> {
        val list = mutableListOf<Event>()
        for (i in 0 until jsonArray.length()) {
            try {
                val eventJson = jsonArray.getJSONObject(i)
                list.add(parseEventFromJson(eventJson))
            } catch (e: Exception) {
                println("[$TAG] Error parsing event at index $i: ${e.message}")
            }
        }
        println("[$TAG] Parsed ${list.size} events from JSON")
        return list
    }

    private fun parseEventFromJson(json: JSONObject): Event {
        return Event(
            id = json.optString("id", "0"),
            title = json.optString("title", ""),
            date = json.optString("date", ""),
            time = json.optString("time", ""),
            location = json.optString("location", ""),
            description = json.optString("description", ""),
            capacity = json.optString("capacity", "0"),
            status = json.optString("status", ""),
            createdAt = json.optString("created_at", ""),
            updatedAt = json.optString("updated_at", "")
        )
    }
}