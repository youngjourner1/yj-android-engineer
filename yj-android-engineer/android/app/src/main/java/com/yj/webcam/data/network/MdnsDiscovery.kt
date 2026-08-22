package com.yj.webcam.data.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MdnsDiscovery @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null

    fun registerService(deviceName: String, port: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "YJWebcam-$deviceName"
            serviceType = "_yjwebcam._tcp"
            setPort(port)
            setAttribute("ver", "1.0")
            setAttribute("fps_max", "60")
            setAttribute("paired", "true")
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                Timber.i("mDNS service registered successfully: ${NsdServiceInfo.serviceName}")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Timber.e("mDNS service registration failed with code: $errorCode")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                Timber.i("mDNS service unregistered")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Timber.e("mDNS service unregistration failed with code: $errorCode")
            }
        }

        try {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        } catch (e: Exception) {
            Timber.e(e, "Error registering mDNS service")
        }
    }

    fun unregisterService() {
        registrationListener?.let {
            try {
                nsdManager.unregisterService(it)
            } catch (e: Exception) {
                Timber.e(e, "Error unregistering mDNS service")
            }
        }
        registrationListener = null
    }
}
