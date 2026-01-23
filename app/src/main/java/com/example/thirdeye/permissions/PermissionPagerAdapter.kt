package com.example.thirdeye.permissions

import android.Manifest
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.thirdeye.R
import com.example.thirdeye.databinding.PermissionsViewpagerItemBinding

class PermissionPagerAdapter(
    private val permissions: MutableList<String>
) : RecyclerView.Adapter<PermissionPagerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PermissionsViewpagerItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PermissionsViewpagerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val permission = permissions[position]
        val ctx=holder.itemView.context
        holder.binding.permissionTitle.text = readableName(permission,ctx)
        holder.binding.permissionDescription.text = readableDescription(permission,ctx)
        holder.binding.permissionIcon.setImageResource(iconFor(permission))
    }

    override fun getItemCount(): Int = permissions.size

    private fun readableName(permission: String,context: Context) = when (permission) {
        Manifest.permission.POST_NOTIFICATIONS -> context.getString(R.string.notificPerm)
        Manifest.permission.CAMERA -> context.getString(R.string.cameraPerm)
        "DEVICE_ADMIN" -> context.getString(R.string.adminPerm)
        else -> "Permission Required"
    }

    private fun readableDescription(permission: String,context: Context) = when (permission) {
        Manifest.permission.POST_NOTIFICATIONS -> context.getString(R.string.notifDesc)
        Manifest.permission.CAMERA -> context.getString(R.string.camreaDes)
        "DEVICE_ADMIN" -> context.getString(R.string.adminPermDesc)
        else -> "Permission is required to continue."
    }

    private fun iconFor(permission: String) = when (permission) {
        Manifest.permission.POST_NOTIFICATIONS -> R.drawable.notificationicon
        Manifest.permission.CAMERA -> R.drawable.solar_camera_bold
        "DEVICE_ADMIN" -> R.drawable.administratoricon
        else -> R.drawable.ic_launcher_background
    }


    fun addAdminPage(pager: ViewPager2) {
        if (!permissions.contains("DEVICE_ADMIN")) {
            permissions.add("DEVICE_ADMIN")
            notifyItemInserted(permissions.lastIndex)
        }
    }

}