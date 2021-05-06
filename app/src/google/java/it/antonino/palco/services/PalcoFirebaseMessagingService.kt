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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.antonino.palco.LoginActivity
import it.antonino.palco.MainActivity
import it.antonino.palco.MainViewModel
import it.antonino.palco.R
import it.antonino.palco.model.User
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class PalcoFirebaseMessagingService: FirebaseMessagingService(), KoinComponent, LifecycleOwner {

    val TAG = PalcoFirebaseMessagingService::class.qualifiedName

    private val viewModel: MainViewModel by inject()
    private var sharedPreferences: SharedPreferences? = null
    private val mDispatcher = ServiceLifecycleDispatcher(this)

    override fun onMessageReceived(p0: RemoteMessage) {
        createNotificationChannel()

        // Create an Intent for the activity you want to start
        //val resultIntent = Intent(Intent.ACTION_VIEW)
        //resultIntent.data = Uri.parse(p0.data["bill"])
        // Create the TaskStackBuilder
        /*val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }*/
        /*val resultPendingIntent =
            PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        */

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        var builder = NotificationCompat.Builder(
            this,
            getString(R.string.default_notification_channel_id)
        )
            .setSmallIcon(R.drawable.palco_icon)
            .setContentTitle(p0.notification?.title)
            .setContentText(p0.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
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
            firebase_token = p0,
            huawei_token = null
        )

        Handler(Looper.getMainLooper()).postDelayed(
            Runnable {
                viewModel.uploadFirebaseToken(user).observe(
                    this@PalcoFirebaseMessagingService,
                    uploadObserver
                )
            },
            1000
        )

        Log.d(TAG, p0)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun getLifecycle() = mDispatcher.lifecycle

    private val uploadObserver = Observer<String?> {
        when (it?.contains("Token uploaded successfully !!")) {
            true -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", true)?.apply()
            }
            false -> {
                sharedPreferences?.edit()?.putBoolean("firebaseTokenUploaded", false)?.apply()
                Toast.makeText(this, "Ops.. c'è stato un problema con il token", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


}