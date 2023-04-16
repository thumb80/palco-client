package it.antonino.palco.di

import it.antonino.palco.BuildConfig
import it.antonino.palco.common.CustomTrust
import it.antonino.palco.network.DiscogsAPI
import it.antonino.palco.network.NetworkAPI
import it.antonino.palco.network.NetworkRepository
import it.antonino.palco.network.UnsplashAPI
import it.antonino.palco.viewmodel.SharedViewModel
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                CustomTrust(androidContext())
                    .client
                    .newBuilder()
                    .callTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build())
            .baseUrl(BuildConfig.BASE_URL)
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
            .create(UnsplashAPI::class.java)
    }

    // ViewModel Dependencies
   viewModel { SharedViewModel(get()) }

}