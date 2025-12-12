package com.example.eventmanagement.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.eventmanagement.R
import com.example.eventmanagement.model.Event

/**
 * DialogHelper - Helper class untuk handle semua dialog
 * Memisahkan logic dialog dari MainActivity
 */
object DialogHelper {

    /**
     * Show dialog untuk create event baru
     */
    fun showCreateDialog(
        context: Context,
        onSave: (Map<String, String>) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_event_form, null)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Buat Event Baru")
            .setView(dialogView)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setOnClickListener {
                val formData = extractFormData(dialogView)

                // Validasi
                val error = Event.validate(
                    formData["title"]!!,
                    formData["date"]!!,
                    formData["time"]!!,
                    formData["location"]!!
                )

                if (error != null) {
                    android.widget.Toast.makeText(context, "âŒ $error", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    onSave(formData)
                    dialog.dismiss()
                }
            }
        }

        setupStatusSpinner(context, dialogView)
        dialog.show()
    }

    /**
     * Show dialog untuk edit event
     */
    fun showEditDialog(
        context: Context,
        event: Event,
        onUpdate: (Map<String, String>) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_event_form, null)

        // Pre-fill form
        dialogView.findViewById<EditText>(R.id.etTitle).setText(event.title)
        dialogView.findViewById<EditText>(R.id.etDate).setText(event.date)
        dialogView.findViewById<EditText>(R.id.etTime).setText(event.time)
        dialogView.findViewById<EditText>(R.id.etLocation).setText(event.location)
        dialogView.findViewById<EditText>(R.id.etDescription).setText(event.description)
        dialogView.findViewById<EditText>(R.id.etCapacity).setText(event.capacity)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Event")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            val btnUpdate = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnUpdate.setOnClickListener {
                val formData = extractFormData(dialogView)

                // Validasi
                val error = Event.validate(
                    formData["title"]!!,
                    formData["date"]!!,
                    formData["time"]!!,
                    formData["location"]!!
                )

                if (error != null) {
                    android.widget.Toast.makeText(context, "âŒ $error", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    onUpdate(formData)
                    dialog.dismiss()
                }
            }
        }

        setupStatusSpinner(context, dialogView, event.status)
        dialog.show()
    }

    /**
     * Show detail event
     */
    fun showEventDetail(context: Context, event: Event) {
        val message = """
            Tanggal: ${event.date}
            Waktu: ${event.time}
            Lokasi: ${event.location}
            Deskripsi: ${event.description}
            Kapasitas: ${event.capacity} orang
            Status: ${Constants.getStatusLabel(event.status)}
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle(event.title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Show dialog konfirmasi hapus
     */
    fun showDeleteConfirmation(
        context: Context,
        eventTitle: String,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Event")
            .setMessage("Yakin ingin menghapus '$eventTitle'?")
            .setPositiveButton("Hapus") { _, _ -> onConfirm() }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ========== PRIVATE HELPERS ==========

    /**
     * Extract form data dari dialog
     */
    private fun extractFormData(dialogView: View): Map<String, String> {
        return mapOf(
            "title" to dialogView.findViewById<EditText>(R.id.etTitle).text.toString(),
            "date" to dialogView.findViewById<EditText>(R.id.etDate).text.toString(),
            "time" to dialogView.findViewById<EditText>(R.id.etTime).text.toString(),
            "location" to dialogView.findViewById<EditText>(R.id.etLocation).text.toString(),
            "description" to dialogView.findViewById<EditText>(R.id.etDescription).text.toString(),
            "capacity" to dialogView.findViewById<EditText>(R.id.etCapacity).text.toString(),
            "status" to dialogView.findViewById<Spinner>(R.id.spinnerStatus).selectedItem.toString()
        )
    }

    /**
     * Setup status spinner
     */
    private fun setupStatusSpinner(context: Context, dialogView: View, selectedStatus: String? = null) {
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val statuses = arrayOf(
            Constants.STATUS_UPCOMING,
            Constants.STATUS_ONGOING,
            Constants.STATUS_COMPLETED,
            Constants.STATUS_CANCELLED
        )
        spinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, statuses)

        selectedStatus?.let {
            spinner.setSelection(statuses.indexOf(it))
        }
    }
}