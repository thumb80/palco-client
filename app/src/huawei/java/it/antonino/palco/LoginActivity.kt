package it.antonino.palco

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import it.antonino.palco.ui.ConcertiFragment
import it.antonino.palco.ui.login.LoginFragment


class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_login)

        val accessToken = AccessToken.getCurrentAccessToken()

        if (accessToken == null || accessToken.isExpired) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
        else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ConcertiFragment.newInstance())
                .commitNow()
        }
    }

}