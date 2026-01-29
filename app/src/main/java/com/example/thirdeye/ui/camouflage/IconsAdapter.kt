package com.example.thirdeye.ui.camouflage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.AppIcons
import com.example.thirdeye.databinding.IconItemBinding

class IconsAdapter : RecyclerView.Adapter<IconsAdapter.ViewHolder>() {

    var selectedPosition = -1

    var onFreeIconClick: ((icon: AppIcons, previousPosition: Int) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<AppIcons>() {
        override fun areItemsTheSame(oldItem: AppIcons, newItem: AppIcons): Boolean {
            return oldItem.icon == newItem.icon
        }

        override fun areContentsTheSame(oldItem: AppIcons, newItem: AppIcons): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = IconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val icon = differ.currentList[position]

        holder.binding.icon.setImageResource(icon.icon)
        holder.binding.iconName.text = icon.name

        if (position == selectedPosition) {
            holder.binding.imageLayout.setStrokeColor(
                ContextCompat.getColor(holder.itemView.context, R.color.dividerColor)
            )
        } else {
            holder.binding.imageLayout.setStrokeColor(
                ContextCompat.getColor(holder.itemView.context, R.color.transparent)
            )
        }

        holder.binding.root.setOnClickListener {
            if (position == selectedPosition) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = position

            if (previousPosition != -1) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(position)

            onFreeIconClick?.invoke(icon, previousPosition)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun clearSelection() {
        val previous = selectedPosition
        selectedPosition = -1
        if (previous != -1) {
            notifyItemChanged(previous)
        }
    }

    inner class ViewHolder(val binding: IconItemBinding) : RecyclerView.ViewHolder(binding.root)
}