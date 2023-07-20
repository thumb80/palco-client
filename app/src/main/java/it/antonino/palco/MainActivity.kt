package it.antonino.palco

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import it.antonino.palco.ext.populateMonths
import it.antonino.palco.ui.advise.AdviseFragment
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModel()
    private var sharedPreferences: SharedPreferences? = null
    private var timeStamp: Long = 0
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        progressBar = findViewById(R.id.progress_bar) as ProgressBar
        progressBar?.visibility = View.VISIBLE
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
}