package com.samsung.android.scan3d.serv

import android.content.Context
import android.net.wifi.WifiManager

class WifiLockManager(context: Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var wifiLock: WifiManager.WifiLock? = null

    fun acquireWifiLock() {
        if (wifiLock == null) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "MyAppWifiLock")
            wifiLock?.setReferenceCounted(false)
            wifiLock?.acquire()
            android.util.Log.i("WifiLockManager", "Wi-Fi lock acquired")
        }
    }

    fun releaseWifiLock() {
        if (wifiLock?.isHeld == true) {
            wifiLock?.release()
            android.util.Log.i("WifiLockManager", "Wi-Fi lock released")
        }
    }
}