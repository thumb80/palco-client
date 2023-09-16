package it.antonino.palco.di

import it.antonino.palco.BuildConfig
import it.antonino.palco.common.CustomTrust
import it.antonino.palco.network.*
import it.antonino.palco.util.Constant.cacheSize
import it.antonino.palco.util.Constant.gson
import it.antonino.palco.viewmodel.SharedViewModel
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val appModule = module {

    // Network Dependencies
    single {
        NetworkRepository.getInstance(get(),get(),get())
    }
    single {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                OkHttpClient()
                    .newBuilder()
                    .callTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(
                        Interceptor { chain ->
                            val response = chain.proceed(chain.request())
                            val maxAge = 60 * 60 * 4
                            response.newBuilder()
                                .header("Cache-Control", "public, max-age=$maxAge")
                                .removeHeader("Pragma")
                                .build()
                        }
                    ).cache(Cache(androidContext().cacheDir, cacheSize))
                    .build()
                /*CustomTrust(androidContext())
                    .client
                    .newBuilder()
                    .callTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(
                        Interceptor { chain ->
                            val response = chain.proceed(chain.request())
                            val maxAge = 60 * 60 * 4
                            response.newBuilder()
                                .header("Cache-Control", "public, max-age=$maxAge")
                                .removeHeader("Pragma")
                                .build()
                        }
                    ).cache(Cache(androidContext().cacheDir, cacheSize))
                    .retryOnConnectionFailure(true)
                    .build()*/)
            .baseUrl(BuildConfig.baseUrl)
            .build()
    }
    // Interfaces
    single {
        get<Retrofit>()
            .create(NetworkAPI::class.java)
    }
    single {
        get<Retrofit>()
            .create(DiscogsAPI::class.java)
    }
    single {
        get<Retrofit>()
            .create(WikiPediaAPI::class.java)
    }

    // ViewModel Dependency
   viewModel { SharedViewModel(get()) }

}