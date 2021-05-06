package it.antonino.palco

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.location.*
import it.antonino.palco.common.ProgressBarHolder
import it.antonino.palco.model.User
import it.antonino.palco.ui.ConcertiFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.name

    private var progressBarHolder: ProgressBarHolder? = null
    private var sharedPreferences: SharedPreferences? = null
    //private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ConcertiFragment.newInstance())
            .commitNow()

        val backColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        val layoutColor = ContextCompat.getColor(applicationContext, R.color.colorAccent)

        progressBarHolder = ProgressBarHolder.Builder()
            .setIndeterminateColor(backColor)
            .setLayoutBackColor(layoutColor)
            .build(this)
        if (sharedPreferences?.getBoolean("huaweiTokenUploaded", false) == false)
            getToken()
    }

    fun showProgress() {
        runOnUiThread { progressBarHolder!!.show() }

    }

    fun hideProgress() {
        runOnUiThread { progressBarHolder!!.hide() }
    }

    private fun getToken() {
        // Create a thread.
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    val appId = AGConnectServicesConfig.fromContext(this@MainActivity).getString("client/app_id")

                    // Enter the token ID HCM.
                    val tokenScope = "HCM"
                    val token = HmsInstanceId.getInstance(this@MainActivity).getToken(
                        appId,
                        tokenScope
                    )
                    Log.i(TAG, "get token:$token")

                    // Check whether the token is empty.
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token)
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "get token failed, $e")
                }
            }
        }.start()
    }

    private fun sendRegTokenToServer(token: String?) {
        Log.i(TAG, "sending token to server. token:$token")
        val user = User(
            username = sharedPreferences?.getString("username", "") ?: "",
            password = sharedPreferences?.getString("password","") ?: "",
            firebase_token = null,
            huawei_token = token
        )
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                viewModel.uploadHuaweiToken(user).observe(this@MainActivity, uploadObserver)
            },
            1000
        )
    }

    private val uploadObserver = Observer<String?> {
        when (it?.contains("Token uploaded successfully !!")) {
            true -> {
                sharedPreferences?.edit()?.putBoolean("huaweiTokenUploaded", true)?.apply()
            }
            false -> {
                sharedPreferences?.edit()?.putBoolean("huaweiTokenUploaded", false)?.apply()
                Toast.makeText(this, "Ops.. c'è stato un problema con il token", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

}