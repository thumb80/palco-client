package it.antonino.palco

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import it.antonino.palco.common.ProgressBarHolder
import it.antonino.palco.model.User
import it.antonino.palco.ui.ConcertiFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private var progressBarHolder: ProgressBarHolder? = null
    private var user: User? = null
    private var sharedPreferences: SharedPreferences? = null
    private var timeStamp: Long = 0

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ConcertiFragment.newInstance())
            .commitNow()

        val backColor = ContextCompat.getColor(applicationContext, R.color.white_alpha)
        val layoutColor = ContextCompat.getColor(applicationContext, R.color.colorAccent)
        progressBarHolder = ProgressBarHolder.Builder()
            .setIndeterminateColor(layoutColor)
            .setLayoutBackColor(backColor)
            .build(this)


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
        progressBarHolder!!.show()

    }

    fun hideProgress() {
        progressBarHolder!!.hide()
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

}