package com.example.eventmanagement

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.adapter.EventAdapter
import com.example.eventmanagement.model.Event
import com.example.eventmanagement.model.Statistics
import com.example.eventmanagement.repository.EventRepository
import com.example.eventmanagement.utils.Constants
import com.example.eventmanagement.utils.DialogHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*

/**
 * MainActivity - Activity utama aplikasi Event Management
 */
class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private val repository = EventRepository()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAdd: FloatingActionButton

    // Statistics UI Components (sesuai dengan layout yang ada)
    private lateinit var tvTotal: TextView
    private lateinit var tvUpcoming: TextView
    private lateinit var tvOngoing: TextView
    private lateinit var tvCompleted: TextView
    

    // Data
    private var eventsList = mutableListOf<Event>()
    private var currentFilter = Constants.FILTER_ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupTabLayout()
        setupFab()

        // Load initial data
        loadEvents()
        loadStatistics()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
        tabLayout = findViewById(R.id.tabLayout)
        fabAdd = findViewById(R.id.fabAdd)

        // Statistics views - hanya ambil yang ada di layout
        tvTotal = findViewById(R.id.tvTotal)
        tvUpcoming = findViewById(R.id.tvUpcoming)
        tvOngoing = findViewById(R.id.tvOngoing)
        tvCompleted = findViewById(R.id.tvCompleted)

        // tvCancelled mungkin tidak ada di layout, jadi kita handle dengan try-catch
        // statsProgressBar mungkin juga tidak ada
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(eventsList) { event, action ->
            when (action) {
                EventAdapter.ACTION_VIEW -> {
                    Log.d(tag, "View event: ${event.id} - ${event.title}")
                    DialogHelper.showEventDetail(this, event)
                }
                EventAdapter.ACTION_EDIT -> {
                    Log.d(tag, "Edit event: ${event.id} - ${event.title}")
                    DialogHelper.showEditDialog(this, event) { formData ->
                        updateEvent(event.id, formData)
                    }
                }
                EventAdapter.ACTION_DELETE -> {
                    Log.d(tag, "Delete event: ${event.id} - ${event.title}")
                    DialogHelper.showDeleteConfirmation(this, event.title) {
                        deleteEvent(event.id)
                    }
                }
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupTabLayout() {
        // Setup tabs (sesuai dengan yang ada di layout Anda)
        tabLayout.addTab(tabLayout.newTab().setText("Semua"))
        tabLayout.addTab(tabLayout.newTab().setText("Akan Datang"))
        tabLayout.addTab(tabLayout.newTab().setText("Berlangsung"))
        tabLayout.addTab(tabLayout.newTab().setText("Selesai"))
        // Tab "Dibatalkan" mungkin tidak ada, jadi kita skip

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentFilter = when (tab?.position) {
                    0 -> Constants.FILTER_ALL
                    1 -> Constants.STATUS_UPCOMING
                    2 -> Constants.STATUS_ONGOING
                    3 -> Constants.STATUS_COMPLETED
                    // Tab "Dibatalkan" mungkin tidak ada
                    else -> Constants.FILTER_ALL
                }
                Log.d(tag, "Tab selected: ${tab?.text} -> filter: $currentFilter")
                loadEvents()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFab() {
        fabAdd.setOnClickListener {
            DialogHelper.showCreateDialog(this) { formData ->
                createEvent(formData)
            }
        }
    }

    // ============================================================
    // LOAD DATA METHODS
    // ============================================================

    private fun loadEvents() {
        showLoading(true)

        coroutineScope.launch {
            try {
                // Use filter if not "all"
                val filter = if (currentFilter == Constants.FILTER_ALL) null else currentFilter
                Log.d(tag, "Loading events with filter: ${filter ?: "all"}")

                val result = withContext(Dispatchers.IO) {
                    repository.getAllEvents(filter)
                }

                result.onSuccess { events ->
                    Log.d(tag, "Successfully loaded ${events.size} events")
                    eventsList.clear()
                    eventsList.addAll(events)

                    // Gunakan notifyDataSetChanged untuk sekarang
                    adapter.notifyDataSetChanged()

                    updateEmptyView()
                    showLoading(false)
                }

                result.onFailure { error ->
                    Log.e(tag, "Failed to load events: ${error.message}")
                    showError("Gagal memuat event: ${error.message}")
                    showLoading(false)
                }

            } catch (e: Exception) {
                Log.e(tag, "Exception in loadEvents: ${e.message}", e)
                showError("Terjadi kesalahan: ${e.message}")
                showLoading(false)
            }
        }
    }

    private fun loadStatistics() {
        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getStatistics()
                }

                result.onSuccess { stats ->
                    Log.d(tag, "Statistics loaded: $stats")
                    updateStatistics(stats)
                }

                result.onFailure { error ->
                    Log.e(tag, "Failed to load statistics: ${error.message}")
                    // Tidak perlu show error untuk statistik
                }

            } catch (e: Exception) {
                Log.e(tag, "Exception in loadStatistics: ${e.message}", e)
            }
        }
    }

    // ============================================================
    // CRUD OPERATIONS
    // ============================================================

    private fun createEvent(formData: Map<String, String>) {
        showLoading(true)

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.createEvent(
                        title = formData["title"] ?: "",
                        date = formData["date"] ?: "",
                        time = formData["time"] ?: "",
                        location = formData["location"] ?: "",
                        description = formData["description"] ?: "",
                        capacity = formData["capacity"] ?: "0",
                        status = formData["status"] ?: Constants.STATUS_UPCOMING
                    )
                }

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    result.onSuccess { event ->
                        Log.d(tag, "Event created successfully: ${event.id}")
                        showSuccess("Event berhasil dibuat!")
                        loadEvents()
                        loadStatistics()
                    }
                    result.onFailure { error ->
                        Log.e(tag, "Failed to create event: ${error.message}")
                        showError("Gagal membuat event: ${error.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Exception in createEvent: ${e.message}", e)
                showLoading(false)
                showError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun updateEvent(id: String, formData: Map<String, String>) {
        if (id.isEmpty()) {
            showError("ID event tidak valid")
            return
        }

        Log.d(tag, "Updating event with ID: '$id'")
        showLoading(true)

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.updateEvent(
                        id = id.trim(),
                        title = formData["title"] ?: "",
                        date = formData["date"] ?: "",
                        time = formData["time"] ?: "",
                        location = formData["location"] ?: "",
                        description = formData["description"] ?: "",
                        capacity = formData["capacity"] ?: "0",
                        status = formData["status"] ?: Constants.STATUS_UPCOMING
                    )
                }

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    result.onSuccess { event ->
                        Log.d(tag, "Event updated successfully: ${event.id}")
                        showSuccess("Event berhasil diupdate!")
                        loadEvents()
                        loadStatistics()
                    }
                    result.onFailure { error ->
                        Log.e(tag, "Failed to update event: ${error.message}")
                        showError("Gagal update event: ${error.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Exception in updateEvent: ${e.message}", e)
                showLoading(false)
                showError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun deleteEvent(id: String) {
        if (id.isEmpty()) {
            showError("ID event tidak valid")
            return
        }

        Log.d(tag, "Deleting event with ID: '$id'")
        showLoading(true)

        coroutineScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.deleteEvent(id.trim())
                }

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    result.onSuccess {
                        Log.d(tag, "Event deleted successfully")
                        showSuccess("Event berhasil dihapus!")
                        loadEvents()
                        loadStatistics()
                    }
                    result.onFailure { error ->
                        Log.e(tag, "Failed to delete event: ${error.message}")
                        showError("Gagal hapus event: ${error.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Exception in deleteEvent: ${e.message}", e)
                showLoading(false)
                showError("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    // ============================================================
    // UI UPDATE METHODS
    // ============================================================

    private fun updateStatistics(stats: Statistics) {
        tvTotal.text = stats.total.toString()
        tvUpcoming.text = stats.upcoming.toString()
        tvOngoing.text = stats.ongoing.toString()
        tvCompleted.text = stats.completed.toString()

        // tvCancelled mungkin tidak ada, jadi kita skip
        // Jika mau, bisa tambahkan dengan try-catch:
        // try { tvCancelled.text = stats.cancelled.toString() } catch (e: Exception) {}
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun updateEmptyView() {
        if (eventsList.isEmpty()) {
            emptyView.text = when (currentFilter) {
                Constants.FILTER_ALL -> "Tidak ada event"
                Constants.STATUS_UPCOMING -> "Tidak ada event yang akan datang"
                Constants.STATUS_ONGOING -> "Tidak ada event yang berlangsung"
                Constants.STATUS_COMPLETED -> "Tidak ada event yang selesai"
                else -> "Tidak ada event"
            }
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, "✓ $message", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, "✗ $message", Toast.LENGTH_LONG).show()
    }
}