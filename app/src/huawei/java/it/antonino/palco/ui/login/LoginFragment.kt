package it.antonino.palco.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import it.antonino.palco.LoginActivity
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.model.User
import kotlinx.android.synthetic.huawei.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()
    private var callbackManager: CallbackManager? = null
    private var username = ""
    private var password = ""

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        facebook_sign_in_button.setReadPermissions("email")
        facebook_sign_in_button.setFragment(this)
        facebook_sign_in_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                username = result.accessToken.userId
                password = it.antonino.palco.common.Hasher().hash()
                viewModel.registration(
                    User(
                        username,
                        password,
                        null,
                        null
                    )
                ).observe(viewLifecycleOwner, registrationObserver)
            }

            override fun onCancel() {
                Toast.makeText(context,"Ops c'è stato un problema",Toast.LENGTH_LONG).show()
                startActivity(Intent(context, LoginActivity::class.java))
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(context,"Ops c'è stato un problema",Toast.LENGTH_LONG).show()
                startActivity(Intent(context, LoginActivity::class.java))
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

    }

    private val registrationObserver = Observer<Pair<Int?, String?>> {

        when (it.second.isNullOrEmpty()) {
            true -> {
                Toast.makeText(context, "Ops... c'è stato un problema", Toast.LENGTH_LONG).show()
            }
            false -> {
                when (it.first) {
                    200 -> {
                        val sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        sharedPreferences?.edit()
                            ?.putBoolean("logged", true)
                            ?.putString("username", username)
                            ?.putString("password", password)
                            ?.putString("VERSION", context?.packageManager?.getPackageInfo(context?.packageName!!,0)?.versionName)
                            ?.apply()
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }
        }

    }

}