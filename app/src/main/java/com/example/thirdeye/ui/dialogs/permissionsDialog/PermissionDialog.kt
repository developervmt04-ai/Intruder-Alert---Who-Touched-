package com.example.thirdeye.ui.dialogs.permissionsDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.thirdeye.databinding.PermissionsViewpagerBinding
import com.example.thirdeye.permissions.DeviceAdminManager

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


        adapter = PermissionPagerAdapter(mutablePermissions)
        binding.permissionPager.adapter = adapter

        if (!dm.isDeviceAdminActive()) {
            adapter.addAdminPage(binding.permissionPager)
        }

        binding.allowPermissionbotnowBtn.setOnClickListener {
            val currentItem = binding.permissionPager.currentItem
            val currentPermission = mutablePermissions[currentItem]

            if (currentPermission == "DEVICE_ADMIN") {
                dm.requestDeviceAdmin()
                dismiss()
                return@setOnClickListener
            }

            onRequest(currentPermission)

            if (currentItem < mutablePermissions.size - 1) {
                binding.permissionPager.setCurrentItem(currentItem + 1, true)
            } else {
                dismiss()
            }
        }
    }






    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}
