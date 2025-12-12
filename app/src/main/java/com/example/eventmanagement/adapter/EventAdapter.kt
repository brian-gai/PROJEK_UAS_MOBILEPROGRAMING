package com.example.eventmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.R
import com.example.eventmanagement.model.Event
import com.example.eventmanagement.utils.Constants

/**
 * EventAdapter - Adapter untuk RecyclerView
 * Menampilkan list event dalam card view
 */
class EventAdapter(
    private val events: List<Event>,
    private val onItemAction: (Event, String) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    /**
     * ViewHolder untuk item event
     */
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvEventTitle)
        val tvEventId: TextView = view.findViewById(R.id.tvEventId)
        val tvDate: TextView = view.findViewById(R.id.tvEventDate)
        val tvLocation: TextView = view.findViewById(R.id.tvEventLocation)
        val tvStatus: TextView = view.findViewById(R.id.tvEventStatus)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        // Set ID event ke TextView (untuk debugging)
        holder.tvEventId.text = "ID: ${event.id}"

        // Set data ke view
        holder.tvTitle.text = event.title
        holder.tvDate.text = event.getFormattedDateTime()
        holder.tvLocation.text = event.location
        holder.tvStatus.text = Constants.getStatusLabel(event.status)

        // Set background color berdasarkan status
        holder.tvStatus.setBackgroundResource(Constants.getStatusColorRes(event.status))

        // Click listener untuk view detail
        holder.itemView.setOnClickListener {
            onItemAction(event, ACTION_VIEW)
        }

        // Click listener untuk edit
        holder.btnEdit.setOnClickListener {
            onItemAction(event, ACTION_EDIT)
        }

        // Click listener untuk delete
        holder.btnDelete.setOnClickListener {
            onItemAction(event, ACTION_DELETE)
        }
    }

    override fun getItemCount() = events.size

    companion object {
        // Action constants
        const val ACTION_VIEW = "view"
        const val ACTION_EDIT = "edit"
        const val ACTION_DELETE = "delete"
    }
}