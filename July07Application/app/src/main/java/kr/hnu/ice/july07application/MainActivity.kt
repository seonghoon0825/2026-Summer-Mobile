package kr.hnu.ice.july07application

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july07application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.secondBtn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        binding.thirdBtn.setOnClickListener {
            val intent = Intent(this, ThridActivity::class.java)
            startActivity(intent)
        }

        binding.fourthBtn.setOnClickListener {
            val intent = Intent(this, FourthActivity::class.java)
            startActivity(intent)
        }
    }
}