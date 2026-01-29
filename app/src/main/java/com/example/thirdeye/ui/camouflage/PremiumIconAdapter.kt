package com.example.thirdeye.ui.camouflage

import android.view.LayoutInflater
import android.view.LayoutInflater.*
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.AppIcons
import com.example.thirdeye.databinding.IconItemBinding
import com.example.thirdeye.databinding.PremiumIconItemBinding
import com.example.thirdeye.databinding.PremiumIconItemBinding.*

class PremiumIconAdapter :
    RecyclerView.Adapter<PremiumIconAdapter.ViewHolder>() {



    var selectedPosition = -1

    var onPremiumIconClick: ((AppIcons) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<AppIcons>() {
        override fun areItemsTheSame(oldItem: AppIcons, newItem: AppIcons): Boolean {
            return oldItem.icon == newItem.icon
        }

        override fun areContentsTheSame(oldItem: AppIcons, newItem: AppIcons): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    private var hidePremium=false

    fun hidePremiumLogos() {
        hidePremium = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PremiumIconItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val icons = differ.currentList[position]

        holder.binding.icon.setImageResource(icons.icon)
        holder.binding.iconName.text = icons.name


        holder.binding.premiumIcon.visibility = if (hidePremium) View.GONE else View.VISIBLE


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

    override fun getItemCount(): Int = differ.currentList.size

    fun clearSelection() {
        val previousPosition = selectedPosition
        selectedPosition = -1
        if (previousPosition != -1) notifyItemChanged(previousPosition)
    }

    inner class ViewHolder(
        val binding: PremiumIconItemBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
