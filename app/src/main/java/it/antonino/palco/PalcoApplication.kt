package it.antonino.palco

import android.app.Application
import android.content.SharedPreferences
import it.antonino.palco.di.appModule
import it.antonino.palco.ext.getShared
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class PalcoApplication: Application() {

    companion object {
        lateinit var instance: PalcoApplication
        var sharedPreferences: SharedPreferences? = null
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PalcoApplication)
            modules(appModule)
        }

        sharedPreferences = sharedPreferences.getShared()
    }

}