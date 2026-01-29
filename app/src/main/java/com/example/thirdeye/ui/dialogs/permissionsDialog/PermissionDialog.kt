package com.example.thirdeye.ui.dialogs.permissionsDialog

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.thirdeye.databinding.PermissionsViewpagerBinding
import com.example.thirdeye.permissions.DeviceAdminManager
import com.example.thirdeye.permissions.PermissionPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class PermissionDialog(
    pendingPermissions: List<String>,
    private val dm: DeviceAdminManager,
    private val onRequest: (String) -> Unit
) : DialogFragment() {

    private lateinit var binding: PermissionsViewpagerBinding
    private val mutablePermissions = pendingPermissions.toMutableList()
    private lateinit var adapter: PermissionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.example.thirdeye.R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PermissionsViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPager()
        setupButton()
    }

    private fun setupPager() {
        adapter = PermissionPagerAdapter(mutablePermissions)
        binding.permissionPager.adapter = adapter
        if (!dm.isDeviceAdminActive()) {
            adapter.addAdminPage(binding.permissionPager)
        }
       binding. tabDots.attachTo(binding.permissionPager)
        binding.permissionPager.isUserInputEnabled=false
        binding.permissionPager.offscreenPageLimit = mutablePermissions.size




    }

    private fun setupButton() {
        binding.allowPermissionbotnowBtn.setOnClickListener {
            val currentItem = binding.permissionPager.currentItem
            val currentPermission = mutablePermissions[currentItem]

            if (currentPermission == "DEVICE_ADMIN") {
                dm.requestDeviceAdmin()
                return@setOnClickListener
            }

            onRequest(currentPermission)


        }
    }

    override fun onResume() {
        super.onResume()
        checkAndDismissIfAllGranted()
    }

    fun checkAndDismissIfAllGranted() {
        val allGranted = mutablePermissions.all { perm ->
            when (perm) {
                "DEVICE_ADMIN" -> dm.isDeviceAdminActive()
                else -> requireContext().checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED
            }
        }
        if (allGranted && isAdded && dialog?.isShowing == true) {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    fun moveToNext() {
        val currentItem = binding.permissionPager.currentItem
        val lastItem = (binding.permissionPager.adapter?.itemCount ?: 0) - 1

        if (currentItem < lastItem) {
            binding.permissionPager.setCurrentItem(currentItem + 1, true)
        } else {
            dismiss()
        }
    }



}
