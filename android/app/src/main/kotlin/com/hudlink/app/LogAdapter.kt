package com.hudlink.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hudlink.app.databinding.ItemLogBinding

class LogAdapter : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(LogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LogViewHolder(
        private val binding: ItemLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: LogEntry) {
            binding.textTimestamp.text = entry.timestamp
            binding.textType.text = entry.type.name.replace("_", " ")
            binding.textMessage.text = entry.message

            // Color-code by type
            val typeColor = when (entry.type) {
                LogEntry.LogType.HEART_RATE -> 0xFFE91E63.toInt() // Pink
                LogEntry.LogType.GPS -> 0xFF4CAF50.toInt()        // Green
                LogEntry.LogType.HEADING -> 0xFF2196F3.toInt()    // Blue
            }
            binding.textType.setTextColor(typeColor)
        }
    }

    private class LogDiffCallback : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem == newItem
    }
}
