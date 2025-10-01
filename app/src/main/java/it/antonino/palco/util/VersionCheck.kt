package it.antonino.palco.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import it.antonino.palco.BuildConfig
import it.antonino.palco.R

object VersionCheck {

    var latestVersionCode: Int = -1

    fun checkForUpdate(context: Context, callback: () -> Unit, closeAppCallback: () -> Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3 // fetch every hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    latestVersionCode = remoteConfig.getLong("latest_version_code").toInt()
                    val latestVersion = remoteConfig.getString("latest_version")
                    val forceUpdate = remoteConfig.getBoolean("force_update")
                    val updateUrl = remoteConfig.getString("update_url")

                    val currentVersion = BuildConfig.VERSION_NAME
                    val currentVersionCode = BuildConfig.VERSION_CODE

                    println("currentVersion : $currentVersion")
                    println("latestVersionCode : $latestVersionCode")
                    println("currentVersionCode : $currentVersionCode")
                    println("latestVersionCode : $latestVersionCode")
                    println("latestVersion : $latestVersion")
                    println("updateUrl : $updateUrl")

                    if (isUpdateAvailable(
                            currentVersion,
                            currentVersionCode,
                            latestVersion,
                            latestVersionCode)) {
                        if (forceUpdate) {
                            showForceUpdateDialog(context, updateUrl, closeAppCallback)
                        } else {
                            showOptionalUpdateDialog(context, updateUrl, callback)
                        }
                    } else {
                        callback.invoke()
                    }
                }
            }
    }

    private fun isUpdateAvailable(
        current: String,
        currentVersionCode: Int,
        latest: String,
        latestVersionCode: Int): Boolean {

        if (currentVersionCode < latestVersionCode)
            return true
        return current != latest
    }


    private fun showForceUpdateDialog(context: Context, updateUrl: String, callback: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.force_update_dialog_title))
            .setMessage(context.getString(R.string.force_update_dialog_content))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.force_update)) { _, _ ->
                callback.invoke()
                openPlayStore(context, updateUrl)
            }
            .show()
    }

    private fun showOptionalUpdateDialog(context: Context, updateUrl: String, callback: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.update_dialog_title))
            .setMessage(context.getString(R.string.update_dialog_content))
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                openPlayStore(context, updateUrl)
            }
            .setNegativeButton(context.getString(R.string.ko), { _, _, -> callback.invoke() })
            .show()
    }

    private fun openPlayStore(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}
