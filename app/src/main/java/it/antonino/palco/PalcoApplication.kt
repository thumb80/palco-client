package it.antonino.palco

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import it.antonino.palco.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PalcoApplication: Application() {

    var months: ArrayList<String> = arrayListOf()

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