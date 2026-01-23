package com.example.thirdeye.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdeye.R
import com.example.thirdeye.data.models.IntrudersImages
import com.example.thirdeye.databinding.HomePagerLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class HomePagerAdapter : RecyclerView.Adapter<HomePagerAdapter.ViewHolder>() {

    val differ = AsyncListDiffer(this, diffCallback)

    var onClick: ((IntrudersImages) -> Unit)? = null
    var onLockedClick: ((IntrudersImages) -> Unit)? = null
    var onWatchAdClicked: ((IntrudersImages) -> Unit)? = null
    var onPremiumClicked: ((IntrudersImages) -> Unit)? = null
    var onDetailsClicked: ((IntrudersImages) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomePagerLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]


        if (item.isLocked) {
            holder.binding.lokcedCard.visibility = View.VISIBLE
            holder.binding.unlockedCard.visibility = View.INVISIBLE

            holder.binding.root.setOnClickListener {
                onLockedClick?.invoke(item)
            }
            holder.binding.watchAdBtn.setOnClickListener {
                onWatchAdClicked?.invoke(item)
            }
            holder.binding.premiumBtn.setOnClickListener {
                onPremiumClicked?.invoke(item)
            }
        } else {
            holder.binding.lokcedCard.visibility = View.INVISIBLE
            holder.binding.unlockedCard.visibility = View.VISIBLE

            val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val date = Date(item.timeStamp)
            holder.binding.date.text = dateFormatter.format(date)
            holder.binding.time.text = timeFormatter.format(date)
            holder.binding.homeImage.setImageBitmap(item.bitmap)

            holder.binding.root.setOnClickListener {
                onClick?.invoke(item)
            }
            holder.binding.unlockedCard.setOnClickListener {
                onDetailsClicked?.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<IntrudersImages>() {
            override fun areItemsTheSame(
                oldItem: IntrudersImages,
                newItem: IntrudersImages
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: IntrudersImages,
                newItem: IntrudersImages
            ): Boolean = oldItem == newItem
        }
    }

    inner class ViewHolder(val binding: HomePagerLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
