package com.kawinduprabod.wifimonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.widget.Toast

class WifiStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                @Suppress("DEPRECATION")
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo = wifiManager.connectionInfo
                    val ssid = wifiInfo.ssid.replace("\"", "")
                    if (ssid != "<unknown ssid>" && ssid.isNotEmpty()) {
                        Toast.makeText(context, "Connected to WiFi: $ssid", Toast.LENGTH_LONG).show()
                    }
                } else if (networkInfo?.detailedState == NetworkInfo.DetailedState.DISCONNECTED) {
                    Toast.makeText(context, "WiFi Disconnected", Toast.LENGTH_LONG).show()
                }
            }
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                if (state == WifiManager.WIFI_STATE_DISABLED) {
                    Toast.makeText(context, "WiFi is Off", Toast.LENGTH_SHORT).show()
                } else if (state == WifiManager.WIFI_STATE_ENABLED) {
                    Toast.makeText(context, "WiFi is On", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
