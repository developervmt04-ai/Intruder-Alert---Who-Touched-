package com.example.thirdeye.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtils {


    fun isInternetAvailable(context: Context): Boolean{
        val cm= context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            val network: Network=cm.activeNetwork?:return false
            val activeNetwork= cm.getNetworkCapabilities(network)?:return false
            return  when{

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                activeNetwork.hasCapability(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                else ->false
            }



        }
        else{
            val networkInfo=cm.activeNetworkInfo
            return networkInfo!=null && networkInfo.isConnected

        }






    }
}