package com.example.simpleconnectiondetector

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkChecker = RxNetworkChecker(this)

        networkChecker.createNetworkChangeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it) {
                        is RxNetworkChecker.NoNetworkCapabilities -> {
                            tv_transport.text = "Transport: NO CAPABILITIES"
                            tv_upstream.text = "NONE"
                            tv_downstream.text = "NONE"
                        }
                        is RxNetworkChecker.NetworkData -> {
                            tv_transport.text = "Transport: ${it.transportName}"
                            tv_upstream.text = "Upstream: ${it.upstreamKbps} Kbps"
                            tv_downstream.text = "Downstream: ${it.downstreamKbs} Kbps"
                        }
                    }
                }, {
                    tv_transport.text = "Transport: ERROR"
                    tv_upstream.text = "Upstream: NONE"
                    tv_downstream.text = "Downstream: NONE"
                }).addTo(compositeDisposable)
    }
}
