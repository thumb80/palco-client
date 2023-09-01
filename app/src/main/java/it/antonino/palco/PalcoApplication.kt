package it.antonino.palco

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import com.jakewharton.threetenabp.AndroidThreeTen
import it.antonino.palco.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PalcoApplication: Application() {

    var sharedPreferences: SharedPreferences? = null

    companion object {
        lateinit var instance: PalcoApplication
    }

    init {
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        AndroidThreeTen.init(this)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PalcoApplication)
            modules(appModule)
        }

        sharedPreferences = instance.getSharedPreferences("palco_prefs", Context.MODE_PRIVATE)

    }
}