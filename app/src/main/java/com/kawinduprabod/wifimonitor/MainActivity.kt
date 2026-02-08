package com.kawinduprabod.wifimonitor

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var wifiManager: WifiManager
    
    private lateinit var permissionStatusValue: TextView
    private lateinit var appStatusValue: TextView
    private lateinit var currentWifiSsid: TextView
    private lateinit var availableNetworksSection: View
    private lateinit var availableNetworksList: LinearLayout

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        updatePermissionStatus(granted)
        updateWifiInfo()
    }

    private val wifiStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                updateWifiInfo()
            }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread { updateWifiInfo() }
        }

        override fun onLost(network: Network) {
            runOnUiThread { updateWifiInfo() }
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            runOnUiThread { updateWifiInfo() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.status_screen)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        permissionStatusValue = findViewById(R.id.permission_status_value)
        appStatusValue = findViewById(R.id.app_status_value)
        currentWifiSsid = findViewById(R.id.current_wifi_ssid)
        availableNetworksSection = findViewById(R.id.available_networks_section)
        availableNetworksList = findViewById(R.id.available_networks_list)

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            updatePermissionStatus(true)
            updateWifiInfo()
        } else {
            updatePermissionStatus(false)
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun updatePermissionStatus(isGranted: Boolean) {
        permissionStatusValue.text = if (isGranted) getString(R.string.status_granted) else getString(R.string.permission_denied)
        permissionStatusValue.setTextColor(
            if (isGranted) ContextCompat.getColor(this, android.R.color.holo_green_dark)
            else ContextCompat.getColor(this, android.R.color.holo_red_dark)
        )
    }

    private fun updateWifiInfo() {
        if (!wifiManager.isWifiEnabled) {
            appStatusValue.text = getString(R.string.status_inactive)
            appStatusValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            currentWifiSsid.text = getString(R.string.wifi_not_connected)
            availableNetworksSection.visibility = View.GONE
            return
        }

        appStatusValue.text = getString(R.string.status_active)
        appStatusValue.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))

        val wifiInfo: WifiInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities?.transportInfo as? WifiInfo
        } else {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo
        }

        val ssid = wifiInfo?.ssid?.replace("\"", "") ?: ""
        
        if (ssid.isNotEmpty() && ssid != "<unknown ssid>" && ssid != "0x") {
            currentWifiSsid.text = ssid
            availableNetworksSection.visibility = View.GONE
        } else {
            currentWifiSsid.text = getString(R.string.wifi_not_connected)
            showAvailableNetworks()
        }
    }

    private fun showAvailableNetworks() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        
        availableNetworksSection.visibility = View.VISIBLE
        availableNetworksList.removeAllViews()

        val scanResults = wifiManager.scanResults
        if (scanResults.isNullOrEmpty()) {
            val tv = TextView(this).apply {
                text = getString(R.string.no_networks_found)
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setPadding(0, 8, 0, 0)
            }
            availableNetworksList.addView(tv)
        } else {
            scanResults.sortedByDescending { it.level }.take(5).forEach { result ->
                val tv = TextView(this).apply {
                    @Suppress("DEPRECATION")
                    text = result.SSID
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    setPadding(0, 12, 0, 12)
                }
                availableNetworksList.addView(tv)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        registerReceiver(wifiStateReceiver, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
        updateWifiInfo()
    }

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkCallback)
        unregisterReceiver(wifiStateReceiver)
    }
}
