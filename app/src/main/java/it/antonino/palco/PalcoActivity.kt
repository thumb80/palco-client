package it.antonino.palco

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.ui.AdviceFragment
import it.antonino.palco.util.NetworkCheck.isNetworkAvailable
import it.antonino.palco.util.VersionCheck.checkForUpdate
import kotlin.system.exitProcess

class PalcoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.enableEdgeToEdge(window)

        if (!isNetworkAvailable(this)) {
            showNoInternetDialogAndExit()
        } else {
            checkForUpdate(this)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AdviceFragment())
                .commit()
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
}