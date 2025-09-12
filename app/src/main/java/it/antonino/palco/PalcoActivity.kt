package it.antonino.palco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.ui.AdviceFragment
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat

class PalcoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.enableEdgeToEdge(window)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdviceFragment())
            .commit()

    }
}