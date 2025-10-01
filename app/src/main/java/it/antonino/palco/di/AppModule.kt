package it.antonino.palco.di

import android.os.Build
import it.antonino.palco.BuildConfig
import it.antonino.palco.network.DiscogsAPI
import it.antonino.palco.network.NetworkRepository
import it.antonino.palco.util.Constant
import it.antonino.palco.util.Constant.DISCOGS_BASE_URL
import it.antonino.palco.viewmodel.SharedViewModel
import it.antonino.palco.workers.FirstBatchWorker
import it.antonino.palco.workers.SecondBatchWorker
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // Network
    single {
        NetworkRepository.getInstance(get())
    }
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Constant.gson))
            .client(
                OkHttpClient()
                    .newBuilder()
                    .callTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                            .header("User-Agent", getUserAgent())
                            .build()
                        chain.proceed(newRequest)
                    }
                    .addNetworkInterceptor(
                        Interceptor { chain ->
                            val response = chain.proceed(chain.request())
                            val maxAge = 60 * 60 * 4
                            response.newBuilder()
                                .header("Cache-Control", "public, max-age=$maxAge")
                                .removeHeader("Pragma")
                                .build()
                        }
                    )
                    .cache(Cache(androidContext().cacheDir, Constant.cacheSize))
                    .build())
            .baseUrl(DISCOGS_BASE_URL)
            .build()
    }
    // Interfaces
    single {
        get<Retrofit>()
            .create(DiscogsAPI::class.java)
    }
    // ViewModel
    viewModelOf(::SharedViewModel)
    // WorkManager
    worker {
        FirstBatchWorker(get(), get())
    }
    worker {
        SecondBatchWorker(get(), get())
    }
}

fun getUserAgent(): String {
    val appName = "Palco"
    val versionName = BuildConfig.VERSION_NAME
    val osVersion = "Android ${Build.VERSION.RELEASE}"
    val device = Build.MODEL ?: "Unknown"
    val buildId = Build.ID ?: "N/A"

    return "$appName/$versionName ($osVersion; $device; Build/$buildId)"
}