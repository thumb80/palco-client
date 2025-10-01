package it.antonino.palco.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import it.antonino.palco.BuildConfig
import it.antonino.palco.R
import androidx.core.net.toUri

object VersionCheck {

    fun checkForUpdate(context: Context) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (optional)
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // fetch every hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Fetch remote config
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val latestVersionCode = remoteConfig.getString("latest_version_code")
                    val latestVersion = remoteConfig.getString("latest_version")
                    val forceUpdate = remoteConfig.getBoolean("force_update")
                    val updateUrl = remoteConfig.getString("update_url")

                    val currentVersion = BuildConfig.VERSION_NAME
                    val currentVersionCode = BuildConfig.VERSION_CODE

                    if (isUpdateAvailable(currentVersion,
                            currentVersionCode.toString(),
                            latestVersion,
                            latestVersionCode)) {
                        if (forceUpdate) {
                            showForceUpdateDialog(context, updateUrl)
                        } else {
                            showOptionalUpdateDialog(context, updateUrl)
                        }
                    }
                }
            }
    }

    private fun isUpdateAvailable(
        current: String,
        currentVersionCode: String,
        latest: String,
        latestVersionCode: String): Boolean {

        if (currentVersionCode != latestVersionCode)
            return true
        return current != latest
    }


    private fun showForceUpdateDialog(context: Context, updateUrl: String) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.force_update_dialog_title))
            .setMessage(context.getString(R.string.force_update_dialog_content))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.force_update)) { _, _ ->
                openPlayStore(context, updateUrl)
            }
            .show()
    }

    private fun showOptionalUpdateDialog(context: Context, updateUrl: String) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.update_dialog_title))
            .setMessage(context.getString(R.string.update_dialog_content))
            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                openPlayStore(context, updateUrl)
            }
            .setNegativeButton(context.getString(R.string.ko), null)
            .show()
    }

    private fun openPlayStore(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
