package it.antonino.palco

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import it.antonino.palco.common.CheckBoxHolder
import it.antonino.palco.common.ProgressBarHolder
import it.antonino.palco.model.User
import it.antonino.palco.ui.ConcertiFragment
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity: AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var progressBarHolder: ProgressBarHolder? = null
    private var user: User? = null
    private var sharedPreferences: SharedPreferences? = null
    private var timeStamp: Long = 0

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions( this, arrayOf(ACCESS_COARSE_LOCATION), 1000)
            }
            else
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                val geocoder = Geocoder(this, Locale.getDefault())
                                val centerAdresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
                                val upperAddresses = geocoder.getFromLocation(location.latitude + 0.05, location.longitude + 0.05, 10)
                                val lowerAddresses = geocoder.getFromLocation(location.latitude - 0.05, location.longitude - 0.05, 10)
                                val addresses = mutableListOf<Address>()
                                centerAdresses?.forEach {
                                    addresses.add(it)
                                }
                                upperAddresses?.forEach {
                                    addresses.add(it)
                                }
                                lowerAddresses?.forEach {
                                    addresses.add(it)
                                }
                                val localities = collectLocations(addresses)
                                sharedPreferences?.edit()?.putStringSet("cities", localities.toMutableSet())?.apply()
                            }
                            else
                                Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_LONG).show()
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.container, ConcertiFragment.newInstance())
                                .commitNow()
                        }
                } catch (e: SecurityException) {
                    Log.w(TAG, "Cannot get last location")
                    Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_LONG).show()
                }
        else {
            Toast.makeText(this, getString(R.string.gps_disabled), Toast.LENGTH_LONG).show()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ConcertiFragment.newInstance())
                .commitNow()
        }


        if (sharedPreferences?.getBoolean("firebaseTokenUploaded", false) == false) {

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d(TAG, token ?: "")
                user = User(
                    username = sharedPreferences?.getString("username", "") ?: "",
                    password = sharedPreferences?.getString("password","") ?: "",
                    firebase_token = token ?: "",
                    huawei_token = null
                )
                viewModel.uploadFirebaseToken(user!!).observe(this, uploadObserver)
            })
        }

        val backColor = ContextCompat.getColor(applicationContext, R.color.white_alpha)
        val layoutColor = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        progressBarHolder = ProgressBarHolder.Builder()
            .setIndeterminateColor(layoutColor)
            .setLayoutBackColor(backColor)
            .build(this)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    try {
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location : Location? ->
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    val geocoder = Geocoder(this, Locale.getDefault())
                                    val centerAdresses = geocoder.getFromLocation(location.latitude, location.longitude, 10)
                                    val upperAddresses = geocoder.getFromLocation(location.latitude + 0.05, location.longitude + 0.05, 10)
                                    val lowerAddresses = geocoder.getFromLocation(location.latitude - 0.05, location.longitude - 0.05, 10)
                                    val addresses = mutableListOf<Address>()
                                    centerAdresses?.forEach {
                                        addresses.add(it)
                                    }
                                    upperAddresses?.forEach {
                                        addresses.add(it)
                                    }
                                    lowerAddresses?.forEach {
                                        addresses.add(it)
                                    }
                                    val localities = collectLocations(addresses)
                                    sharedPreferences?.edit()?.putStringSet("cities", localities.toMutableSet())?.apply()
                                }
                                else
                                    Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_LONG).show()
                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.container, ConcertiFragment.newInstance())
                                    .commitNow()
                            }
                    } catch (e: SecurityException) {
                        Log.w(TAG, "Cannot get last location")
                        Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_LONG).show()
                    }
                else
                    Toast.makeText(this, getString(R.string.no_location), Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 0) {
            if (System.currentTimeMillis() - timeStamp < 500)
                this.finishAffinity()
            else {
                timeStamp = System.currentTimeMillis()
                Toast.makeText(this, "Premere due volte per uscire dall'app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showProgress() {
        PalcoApplication.instance.executorService.execute {
            progressBarHolder?.show(this)
        }
    }

    fun hideProgress() {
        PalcoApplication.instance.executorService.execute {
            progressBarHolder?.hide(this)
        }
    }

    private val uploadObserver = Observer<String?> {
        when (it?.contains("Token uploaded successfully !!")) {
            true -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", true)?.apply()
            }
            else -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", false)?.apply()
                Toast.makeText(this, "Ops.. c'è stato un problema con il token", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun collectLocations(addresses: MutableList<Address>?): ArrayList<String> {
        val ret = arrayListOf<String>()
        addresses?.forEach {
            if (it.locality != null && !ret.contains(it.locality))
                ret.add(it.locality)
        }
        return ret
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.concerti_locali -> {
                    if (checked) {
                        EventBus.getDefault().post(CheckBoxHolder(true))
                    }
                }
                R.id.concerti_nazionali -> {
                    if (checked) {
                        EventBus.getDefault().post(CheckBoxHolder(false))
                    }
                }
            }
        }
    }

}