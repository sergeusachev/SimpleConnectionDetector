package com.example.simpleconnectiondetector

import android.content.Context
import android.net.*
import android.telephony.TelephonyManager
import android.util.Log
import io.reactivex.Observable


private val DEBUG_TAG = RxNetworkChecker::class.java.simpleName

class RxNetworkChecker(private val context: Context) {

    interface NetworkCapability

    inner class NetworkData(
            val transportName: String,
            val upstreamKbps: Int,
            val downstreamKbs: Int
    ) : NetworkCapability

    inner class NoNetworkCapabilities : NetworkCapability

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun createNetworkChangeObservable(): Observable<NetworkCapability> {
        val request = NetworkRequest.Builder()
        request.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        return Observable.create<NetworkCapability> {
            connectivityManager.registerNetworkCallback(request.build(), object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    Log.d(DEBUG_TAG, "onAvailable()")
                    super.onAvailable(network)
                    //it.onNext(Unit)
                }

                override fun onCapabilitiesChanged(network: Network?, networkCapabilities: NetworkCapabilities?) {
                    Log.d(DEBUG_TAG, "onCapabilitiesChanged()")
                    super.onCapabilitiesChanged(network, networkCapabilities)

                    if (networkCapabilities != null) {
                        val hasWiFiTransport = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        val hasCellularTransport = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)


                        val transportName = when {
                            hasWiFiTransport -> "WIFI"
                            hasCellularTransport -> "CELL"
                            else -> "NONE"
                        }

                        val networkData = NetworkData(
                                transportName = transportName,
                                upstreamKbps = networkCapabilities.linkUpstreamBandwidthKbps,
                                downstreamKbs = networkCapabilities.linkDownstreamBandwidthKbps
                        )

                        it.onNext(networkData)
                    } else {
                        it.onNext(NoNetworkCapabilities())
                    }
                }

                override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
                    Log.d(DEBUG_TAG, "onLinkPropertiesChanged()")
                    super.onLinkPropertiesChanged(network, linkProperties)
                    //it.onNext(Unit)
                }

                override fun onLosing(network: Network?, maxMsToLive: Int) {
                    Log.d(DEBUG_TAG, "onLosing()")
                    super.onLosing(network, maxMsToLive)
                    //it.onNext(Unit)
                }

                override fun onLost(network: Network) {
                    Log.d(DEBUG_TAG, "onLost()")
                    super.onLost(network)
                    //it.onNext(Unit)
                }

                override fun onUnavailable() {
                    Log.d(DEBUG_TAG, "onUnavailable()")
                    super.onUnavailable()
                    //it.onNext(Unit)
                }

            })
            it.setCancellable {  }
        }
    }

    /*private fun createConnectSocketSingle(): Single<Boolean> {
        return Single.fromCallable<Boolean> {
            try {
                val timeoutMillis = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)
                socket.connect(socketAddress, timeoutMillis)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }*/
}