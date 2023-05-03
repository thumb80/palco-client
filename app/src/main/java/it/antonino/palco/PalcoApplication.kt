package it.antonino.palco

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import it.antonino.palco.di.appModule
import it.antonino.palco.model.Concerto
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PalcoApplication: Application() {

    val executorService: ExecutorService = Executors.newFixedThreadPool(4)

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

        startKoin {
            androidContext(this@PalcoApplication)
            modules(appModule)
        }
    }

}