package it.antonino.palco

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.ui.advice.AdviceFragment
import it.antonino.palco.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity: AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModel()
    private var timeStamp: Long = 0
    private lateinit var binding: ActivityMainBinding
    var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        viewModel.getConcertiNazionali().observe(this) {
            if (it == null)
                Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
            else {
                viewModel.setConcerti(it)
            }

            binding.progressBar.visibility = View.INVISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AdviceFragment())
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
                Toast.makeText(this, PalcoApplication.instance.getString(R.string.back_exit), Toast.LENGTH_SHORT).show()
            }
        }
    }
}