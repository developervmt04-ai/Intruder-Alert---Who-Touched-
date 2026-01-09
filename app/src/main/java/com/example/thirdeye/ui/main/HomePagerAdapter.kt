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
import com.example.thirdeye.databinding.IntruderImgItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePagerAdapter(): RecyclerView.Adapter<HomePagerAdapter.ViewHoler>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHoler {
       val binding= HomePagerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHoler(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHoler,
        position: Int
    ) {
        val items=differ.currentList[position]

        if (items.isLocked){
            holder.binding.unlockedCard.visibility=View.INVISIBLE
            holder.binding.lokcedCard.visibility= View.VISIBLE


            holder.binding.root.setOnClickListener {
                onLockedClick?.invoke(items)

            }
            holder.binding.watchAdBtn.setOnClickListener {
                onWatchAdClicked?.invoke(items)

            }
            holder.binding.premiumBtn.setOnClickListener {
                onPremiumClicked?.invoke(items)

            }



        }
        else {
            val dateFormater= SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)

            val timerFormatter= SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            val date = Date(items.timeStamp)
            holder.binding.date.text = dateFormater.format(date)
            holder.binding.time.text=timerFormatter.format(date)
            holder.binding.homeImage.setImageBitmap(items.bitmap)
            holder.binding.root.setOnClickListener {
                onClick?.invoke(items)

            }
            holder.binding.intruderDetails.setOnClickListener {

                onDetailsClicked?.invoke(items)
            }



        }

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
    val diffCallBack=object : DiffUtil.ItemCallback<IntrudersImages>(){
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
    val differ= AsyncListDiffer(this,diffCallBack)
    var onClick:((IntrudersImages)-> Unit)?=null
   var  onLockedClick:((IntrudersImages)->Unit)?=null

    var onWatchAdClicked:((IntrudersImages)-> Unit)?=null

    var onPremiumClicked:((IntrudersImages)-> Unit)?=null

    var onDetailsClicked:((IntrudersImages)-> Unit)?=null


    inner class ViewHoler(val binding: HomePagerLayoutBinding): RecyclerView.ViewHolder(binding.root){




    }
}