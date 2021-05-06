package it.antonino.palco.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import it.antonino.palco.MainActivity
import it.antonino.palco.R
import it.antonino.palco.model.User
import kotlinx.android.synthetic.google.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var username = ""
    private var password = ""

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

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

        google_sign_in_button.setSize(SignInButton.SIZE_STANDARD)

        google_sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, 1)
        }

        info_text.text = resources.getString(R.string.info_text)

        customizeGoogleSignInButton()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1)   {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                username = task.result?.email!!
                password = it.antonino.palco.common.Hasher().hash()
                viewModel.registration(User(
                    username = task.result?.email!!,
                    password = password,
                    firebase_token = null,
                    huawei_token = null
                )).observe(viewLifecycleOwner, registrationObserver)
            }
            else
                Toast.makeText(context,"Ops... c'è stato un problema",Toast.LENGTH_LONG).show()
        }
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

    private fun customizeGoogleSignInButton() {

        google_sign_in_button.children.forEach {
            if (it is TextView) {
                it.text = resources.getString(R.string.google_signin)
            }
        }

    }

}