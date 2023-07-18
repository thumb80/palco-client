package it.antonino.palco

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import it.antonino.palco.common.ProgressBarHolder
import it.antonino.palco.ext.populateMonths
import it.antonino.palco.ui.ConcertiFragment
import it.antonino.palco.ui.advise.AdviseFragment
import it.antonino.palco.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.fragment_advise.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModel()
    private var progressBarHolder: ProgressBarHolder? = null
    private var sharedPreferences: SharedPreferences? = null
    private var timeStamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        viewModel.getConcertiNazionali().observe(this) {
            if (it == null)
                Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
            else {
                viewModel.setConcerti(it)
                PalcoApplication.instance.months = it.populateMonths()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AdviseFragment())
                .commit()
        }

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
        PalcoApplication.instance.executorService.execute {
            progressBarHolder?.show(this)
        }
    }

    fun hideProgress() {
        PalcoApplication.instance.executorService.execute {
            progressBarHolder?.hide(this)
        }
    }
}