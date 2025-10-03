package it.antonino.palco

import android.app.Application
import android.content.SharedPreferences
import com.jakewharton.threetenabp.AndroidThreeTen
import it.antonino.palco.di.appModule
import it.antonino.palco.ext.getShared
import it.antonino.palco.util.Constant.FILE_NAME_1
import it.antonino.palco.util.Constant.FILE_NAME_2
import it.antonino.palco.util.Constant.FILE_NAME_3
import it.antonino.palco.util.Constant.FILE_NAME_4
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import java.io.File

class PalcoApplication: Application() {

    companion object {
        var sharedPreferences: SharedPreferences? = null
        var file_1 = File("concerts_1.json")
        var file_2 = File("concerts_2.json")
        var file_3 = File("concerts_3.json")
        var file_4 = File("concerts_4.json")
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PalcoApplication)
            workManagerFactory()
            modules(appModule)
        }

        AndroidThreeTen.init(this);

        sharedPreferences = getShared(this)

        file_1 = File(this.filesDir, FILE_NAME_1)
        file_2 = File(this.filesDir, FILE_NAME_2)
        file_3 = File(this.filesDir, FILE_NAME_3)
        file_4 = File(this.filesDir, FILE_NAME_4)

    }

}