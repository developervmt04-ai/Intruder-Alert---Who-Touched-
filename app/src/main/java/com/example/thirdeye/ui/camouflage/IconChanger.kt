package com.example.thirdeye.ui.camouflage

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import com.example.thirdeye.constants.Constants.CALCULATOR_ICON
import com.example.thirdeye.constants.Constants.DEFAULT_ICON
import com.example.thirdeye.constants.Constants.WEATHER_ICON

object IconChanger {


    private val alias=listOf(
        WEATHER_ICON,CALCULATOR_ICON
    )

    fun changeIcon(context: Context,aliasToEnable:String){

        val pm=context.packageManager

        try {

            pm.setComponentEnabledSetting(
                ComponentName(context,DEFAULT_ICON),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            alias.forEach {

                alias->
                pm.setComponentEnabledSetting(
                    ComponentName(context,alias),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )

            }
            pm.setComponentEnabledSetting(
                ComponentName(context,aliasToEnable),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            Toast.makeText(context,"Icon Changed", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(context, "Icon not found: $aliasToEnable", Toast.LENGTH_SHORT).show()


        }
    }


}