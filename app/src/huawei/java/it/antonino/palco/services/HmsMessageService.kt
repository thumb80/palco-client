package it.antonino.palco.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import it.antonino.palco.MainViewModel
import it.antonino.palco.R
import it.antonino.palco.model.User
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class HmsMessageService : HmsMessageService(), KoinComponent, LifecycleOwner {

    private val viewModel: MainViewModel by inject()
    private var sharedPreferences: SharedPreferences? = null
    private val mDispatcher = ServiceLifecycleDispatcher(this)

    val TAG = it.antonino.palco.services.HmsMessageService::class.java.name

    override fun onMessageReceived(p0: RemoteMessage) {
        createNotificationChannel()

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(Intent.ACTION_VIEW)
        resultIntent.data = Uri.parse(p0.data)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        var builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_palco_icon)
            .setContentTitle(p0.notification?.title)
            .setContentText(p0.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
        with(NotificationManagerCompat.from(this)) {
            notify(R.string.notification_id, builder.build())
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val user = User(
            username = sharedPreferences?.getString("username", "") ?: "",
            password = null,
            firebase_token = null,
            huawei_token = p0
        )
        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                viewModel.uploadHuaweiToken(user).observe(this@HmsMessageService, uploadObserver)
            },
            1000
        )
        Log.d(TAG, p0)
    }

    override fun getLifecycle() = mDispatcher.lifecycle

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val uploadObserver = Observer<String?> {
        when (it?.contains("Token uploaded successfully !!")) {
            true -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", true)?.apply()
            }
            false -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", false)?.apply()
                Toast.makeText(this, "Ops.. c'è stato un problema con il token", Toast.LENGTH_LONG).show()
            }
        }
    }

}