package it.antonino.palco

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import it.antonino.palco.PalcoApplication.Companion.sharedPreferences
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.ui.AdviceFragment
import it.antonino.palco.util.NetworkCheck.isNetworkAvailable
import it.antonino.palco.util.VersionCheck
import it.antonino.palco.util.VersionCheck.checkForUpdate
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class PalcoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SharedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.enableEdgeToEdge(window)

        if (!isNetworkAvailable(this)) {
            showNoInternetDialogAndExit()
        } else {
            checkForUpdate(this, {
                checkAndClearIfUpdated(context = this)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AdviceFragment())
                    .commitAllowingStateLoss()
            }, {
                finishAffinity()
                exitProcess(0)
            })
        }

    }

    private fun showNoInternetDialogAndExit() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.no_internet_title))
            .setMessage(R.string.no_internet_content)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.no_internet_action)) { _, _ ->
                finishAffinity()
                exitProcess(0)
            }
            .show()
    }

    private fun checkAndClearIfUpdated(context: Context) {

        val currentVersionCode = BuildConfig.VERSION_CODE

        println("CurrentVersionCode : $currentVersionCode")
        println("latestVersionCode : ${VersionCheck.latestVersionCode}")

        if (currentVersionCode < VersionCheck.latestVersionCode) {
            clearAppData(context)
            sharedPreferences?.edit { putInt("last_version_code", VersionCheck.latestVersionCode) }
        }
    }

    private fun clearAppData(context: Context) {
        sharedPreferences?.edit { clear() }
        viewModel.setIsAppUpdate(true)
        context.filesDir.deleteRecursively()
        context.cacheDir.deleteRecursively()
    }

}