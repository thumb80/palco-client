package it.antonino.palco.di

import it.antonino.palco.network.DiscogsAPI
import it.antonino.palco.network.NetworkRepository
import it.antonino.palco.network.WikiPediaAPI
import it.antonino.palco.util.Constant
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
        NetworkRepository.getInstance(get(),get())
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
            .baseUrl("https://it.wikipedia.org")
            .build()
    }
    // Interfaces
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