package com.example.thirdeye.ui.intruders.IntruderDetail.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.thirdeye.R
import com.example.thirdeye.data.models.IntrudersImages
import com.example.thirdeye.databinding.HistoryRvItemBinding
import com.example.thirdeye.ui.intruders.IntruderImageAdapter

class HistoryAdapter(): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding= HistoryRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = differ.currentList[position]




        if (items.isLocked) {
            holder.binding.intruderImage.setImageResource(R.drawable.locked)
            holder.binding.root.setOnClickListener {
                onLockedClick?.invoke(items)
            }
        } else {
            holder.binding.intruderImage.setImageBitmap(items.bitmap)
            holder.binding.root.setOnClickListener {
                onClick?.invoke(items)
            }
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
    val diffCallback= object : DiffUtil.ItemCallback<IntrudersImages>()
    {
        override fun areItemsTheSame(
            oldItem: IntrudersImages,
            newItem: IntrudersImages
        ): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(
            oldItem: IntrudersImages,
            newItem: IntrudersImages
        ): Boolean {
          return oldItem==newItem
        }


    }
    val differ= AsyncListDiffer(this,diffCallback)
    var onLockedClick: ((IntrudersImages) -> Unit)? = null
    var onClick:((IntrudersImages)-> Unit)?=null

    inner class ViewHolder(val binding: HistoryRvItemBinding): RecyclerView.ViewHolder(binding.root){





    }
}