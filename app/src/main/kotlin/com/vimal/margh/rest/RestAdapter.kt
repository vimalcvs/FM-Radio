@file:Suppress("DEPRECATION")

package com.vimal.margh.rest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.vimal.margh.util.Config
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestAdapter {
    private const val TIMEOUT = 10
    private var retrofit: Retrofit? = null

    fun createAPI(context: Context): ApiInterface {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .cache(Cache(context.cacheDir, (5 * 1024 * 1024).toLong()))
                .addInterceptor { chain: Interceptor.Chain ->
                    var request = chain.request()
                    request = if (isNetworkConnected(context)) request.newBuilder()
                        .header("Cache-Control", "public, max-age=" + 1).build()
                    else request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 30
                    ).build()
                    chain.proceed(request)
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Config.REST_API_URL + "/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit!!.create(ApiInterface::class.java)
    }

    private fun isNetworkConnected(context: Context): Boolean {
        var isConnected = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) isConnected = true
        return isConnected
    }


}
