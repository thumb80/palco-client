package it.antonino.palco

import android.app.Application
import android.content.SharedPreferences
import it.antonino.palco.di.appModule
import it.antonino.palco.ext.getShared
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.monitor.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.io.File
import java.util.UUID


class PalcoApplication: Application() {

    companion object {
        lateinit var instance: PalcoApplication
        lateinit var concerti: ArrayList<Concerto>
        lateinit var checkWorkerRequestId: UUID
        var sharedPreferences: SharedPreferences? = null
        var networkMonitor: NetworkMonitor? = null
        var file: File? = null
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
        file = File(cacheDir, "concerti.json")
    }

}