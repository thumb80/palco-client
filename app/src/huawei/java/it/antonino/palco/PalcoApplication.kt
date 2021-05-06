package it.antonino.palco

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import it.antonino.palco.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PalcoApplication: Application() {

    val executorService: ExecutorService = Executors.newFixedThreadPool(1)

    companion object {
        lateinit var instance: PalcoApplication
    }

    init {
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        startKoin {
            androidContext(this@PalcoApplication)
            modules(appModule)
        }
    }

}