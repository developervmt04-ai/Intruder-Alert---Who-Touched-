package com.example.thirdeye.permissions

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.thirdeye.ui.dialogs.permissionsDialog.PermissionDialog

class Permissions(private val activity: AppCompatActivity, private val deviceAdminManager: DeviceAdminManager) {

    private val requiredPermissions = buildList {
        add(android.Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)

        }
    }


    private val singlePermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

        }

    fun checkAndRequest() {
        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toMutableList()

        if (!deviceAdminManager.isDeviceAdminActive()) {
            notGranted.add("DEVICE_ADMIN")
        }

        if (notGranted.isEmpty()) return


        val permissionDialog = PermissionDialog(
            pendingPermissions = notGranted,
            dm = deviceAdminManager,
            onRequest = { perm -> requestSinglePermission(perm) }
        )

        permissionDialog.show(activity.supportFragmentManager, "perm_dialog")
    }




    fun requestSinglePermission(permission: String) {
        singlePermissionLauncher.launch(permission)
    }
    fun allGranted(): Boolean {
        val missingPermission = requiredPermissions.any {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }
        val deviceAdminMissing = !deviceAdminManager.isDeviceAdminActive()
        return !missingPermission && !deviceAdminMissing
    }
}
