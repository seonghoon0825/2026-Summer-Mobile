package kr.hnu.ice.fragmentapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.fragmentapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.firstBtn.isEnabled = false

        binding.firstBtn.setOnClickListener {
            binding.firstBtn.isEnabled = false
            binding.secondBtn.isEnabled = true
            binding.thirdBtn.isEnabled = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FirstFragment())
                .commit()
        }

        binding.secondBtn.setOnClickListener {
            binding.firstBtn.isEnabled = true
            binding.secondBtn.isEnabled = false
            binding.thirdBtn.isEnabled = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SecondFragment())
                .commit()
        }

        binding.thirdBtn.setOnClickListener {
            binding.firstBtn.isEnabled = true
            binding.secondBtn.isEnabled = true
            binding.thirdBtn.isEnabled = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, ThirdFragment())
                .commit()
        }
    }
}