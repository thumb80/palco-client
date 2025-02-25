package it.antonino.palco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.antonino.palco.databinding.ActivityMainBinding
import it.antonino.palco.ui.AdviceFragment

class PalcoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdviceFragment())
            .commit()

    }
}