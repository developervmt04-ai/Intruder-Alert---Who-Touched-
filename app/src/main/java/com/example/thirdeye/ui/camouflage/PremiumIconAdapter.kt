package com.example.thirdeye.ui.camouflage

import android.view.LayoutInflater
import android.view.LayoutInflater.*
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.AppIcons
import com.example.thirdeye.databinding.IconItemBinding
import com.example.thirdeye.databinding.PremiumIconItemBinding
import com.example.thirdeye.databinding.PremiumIconItemBinding.*

class PremiumIconAdapter(
    val icons: List<AppIcons>
) : RecyclerView.Adapter<PremiumIconAdapter.ViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = inflate(from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icons = icons[position]

        holder.binding.icon.setImageResource(icons.icon)
        holder.binding.iconName.text = icons.name


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
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onPremiumIconClick?.invoke(icons)
        }
    }

    override fun getItemCount(): Int {
        return icons.size
    }


    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        if (previousPosition != -1) notifyItemChanged(previousPosition)
    }

    var onPremiumIconClick: ((AppIcons) -> Unit)? = null

    inner class ViewHolder(val binding: PremiumIconItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }
}