package it.antonino.palco

import android.app.Application
import android.content.SharedPreferences
import it.antonino.palco.di.appModule
import it.antonino.palco.ext.getShared
import it.antonino.palco.model.Concerto
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import java.io.File


class PalcoApplication: Application() {

    companion object {
        lateinit var concerti: ArrayList<Concerto>
        var sharedPreferences: SharedPreferences? = null
        var file: File? = null
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PalcoApplication)
            workManagerFactory()
            modules(appModule)
        }

        sharedPreferences = sharedPreferences.getShared(this)
        file = File(cacheDir, "concerti.json")
    }

}