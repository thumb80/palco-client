package it.antonino.palco

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import it.antonino.palco.PalcoApplication.Companion.networkMonitor
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.network.monitor.NetworkMonitor
import it.antonino.palco.ui.AdviceFragment
import it.antonino.palco.ui.NoConnectionFragment

class PalcoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkMonitor = NetworkMonitor(this)
        networkMonitor?.registerNetworkCallback(networkCallback)

        if (networkMonitor?.isNetworkAvailable() == false)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, NoConnectionFragment())
                .commitAllowingStateLoss()

    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor?.unregisterNetworkCallback(networkCallback)
    }

    val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            if (!Python.isStarted())
                Python.start(AndroidPlatform(this@PalcoActivity))

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AdviceFragment())
                .commit()
        }

        override fun onLost(network: Network) {
            Toast.makeText(this@PalcoActivity, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
        }
    }



}