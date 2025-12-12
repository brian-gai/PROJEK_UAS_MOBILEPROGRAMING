package com.example.eventmanagement.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * ApiClient - Android Version
 * HTTP Request Handler untuk aplikasi Android
 */
object ApiClient {

    private const val TAG = "ApiClient"

    // ============================================================
    // GET REQUEST
    // ============================================================
    suspend fun get(urlString: String): String {
        var connection: HttpURLConnection? = null
        return try {
            println("[$TAG] GET Request to: $urlString")
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "GET"
                connectTimeout = Constants.CONNECT_TIMEOUT
                readTimeout = Constants.READ_TIMEOUT
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "EventManagementAndroid/1.0")
            }

            val responseCode = connection.responseCode
            println("[$TAG] GET Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val result = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                println("[$TAG] GET Response Body: ${result.take(200)}...")
                result
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream)).use {
                        it.readText()
                    }
                } catch (e: Exception) {
                    "Unable to read error stream"
                }
                println("[$TAG] GET Error Response: $error")
                throw Exception("Server error: $responseCode - $error")
            }

        } catch (e: Exception) {
            println("[$TAG] GET Exception: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to fetch: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }

    // ============================================================
    // POST JSON (UNTUK CREATE EVENT - SESUAI DOKUMENTASI DOSEN)
    // ============================================================
    suspend fun postJson(urlString: String, jsonData: String): String {
        var connection: HttpURLConnection? = null
        return try {
            println("[$TAG] POST JSON to: $urlString")
            println("[$TAG] POST JSON Data: $jsonData")

            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                connectTimeout = Constants.CONNECT_TIMEOUT
                readTimeout = Constants.READ_TIMEOUT
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "EventManagementAndroid/1.0")
            }

            // Write JSON data
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use {
                it.write(jsonData)
                it.flush()
            }

            val responseCode = connection.responseCode
            println("[$TAG] POST JSON Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val result = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                println("[$TAG] POST JSON Response Body: $result")
                result
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream)).use {
                        it.readText()
                    }
                } catch (e: Exception) {
                    "Unable to read error stream"
                }
                println("[$TAG] POST JSON Error Response: $error")
                throw Exception("Server error: $responseCode - $error")
            }

        } catch (e: Exception) {
            println("[$TAG] POST JSON Exception: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to post JSON: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }

    // ============================================================
    // PUT JSON (UNTUK UPDATE EVENT - SESUAI DOKUMENTASI DOSEN)
    // ============================================================
    suspend fun putJson(urlString: String, jsonData: String): String {
        var connection: HttpURLConnection? = null
        return try {
            println("[$TAG] PUT JSON to: $urlString")
            println("[$TAG] PUT JSON Data: $jsonData")

            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "PUT"
                doOutput = true
                doInput = true
                connectTimeout = Constants.CONNECT_TIMEOUT
                readTimeout = Constants.READ_TIMEOUT
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "EventManagementAndroid/1.0")
            }

            // Write JSON data
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use {
                it.write(jsonData)
                it.flush()
            }

            val responseCode = connection.responseCode
            println("[$TAG] PUT JSON Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val result = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                println("[$TAG] PUT JSON Response Body: $result")
                result
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream)).use {
                        it.readText()
                    }
                } catch (e: Exception) {
                    "Unable to read error stream"
                }
                println("[$TAG] PUT JSON Error Response: $error")
                throw Exception("Server error: $responseCode - $error")
            }

        } catch (e: Exception) {
            println("[$TAG] PUT JSON Exception: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to put JSON: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }

    // ============================================================
    // DELETE REQUEST
    // ============================================================
    suspend fun delete(urlString: String): String {
        var connection: HttpURLConnection? = null
        return try {
            println("[$TAG] DELETE Request to: $urlString")

            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "DELETE"
                connectTimeout = Constants.CONNECT_TIMEOUT
                readTimeout = Constants.READ_TIMEOUT
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "EventManagementAndroid/1.0")
            }

            val responseCode = connection.responseCode
            println("[$TAG] DELETE Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val result = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                println("[$TAG] DELETE Response Body: $result")
                result
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream)).use {
                        it.readText()
                    }
                } catch (e: Exception) {
                    "Unable to read error stream"
                }
                println("[$TAG] DELETE Error Response: $error")
                throw Exception("Server error: $responseCode - $error")
            }

        } catch (e: Exception) {
            println("[$TAG] DELETE Exception: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to delete: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }

    // ============================================================
    // POST FORM-ENCODED (BACKUP JIKA JSON TIDAK BEKERJA)
    // ============================================================
    suspend fun postForm(urlString: String, formData: String): String {
        var connection: HttpURLConnection? = null
        return try {
            println("[$TAG] POST FORM to: $urlString")
            println("[$TAG] POST FORM DATA: $formData")

            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                connectTimeout = Constants.CONNECT_TIMEOUT
                readTimeout = Constants.READ_TIMEOUT
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "EventManagementAndroid/1.0")

                val bytes = formData.toByteArray(Charsets.UTF_8)
                setRequestProperty("Content-Length", bytes.size.toString())
            }

            // Write form data
            connection.outputStream.use { outputStream ->
                outputStream.write(formData.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }

            val responseCode = connection.responseCode
            println("[$TAG] POST FORM Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                val result = BufferedReader(InputStreamReader(connection.inputStream)).use {
                    it.readText()
                }
                println("[$TAG] POST FORM Response Body: $result")
                result
            } else {
                val error = try {
                    BufferedReader(InputStreamReader(connection.errorStream)).use {
                        it.readText()
                    }
                } catch (e: Exception) {
                    "Unable to read error stream"
                }
                println("[$TAG] POST FORM Error Response: $error")
                throw Exception("Server error: $responseCode - $error")
            }

        } catch (e: Exception) {
            println("[$TAG] POST FORM Exception: ${e.message}")
            e.printStackTrace()
            throw Exception("Failed to post form: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }
}