package it.antonino.palco

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.richpath.RichPath
import com.richpathanimator.RichPathAnimator
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity: AppCompatActivity() {

    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        version_text.text = sharedPreferences?.getString("VERSION", "") ?: packageManager.getPackageInfo(this.packageName, 0).versionName

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        if (isUpdated(this))  {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
            mGoogleSignInClient.signOut()
            sharedPreferences?.edit()?.clear()?.apply()
        }

        Handler().postDelayed(
            {
                when (sharedPreferences?.getBoolean("logged", false)) {
                    true -> {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    false -> {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }, 5000
        )


    }

    private fun isUpdated(context: Context): Boolean {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val oldVerName = sharedPreferences?.getString("VERSION", "")
        return oldVerName != null && oldVerName != "" && oldVerName != versionName
    }

}