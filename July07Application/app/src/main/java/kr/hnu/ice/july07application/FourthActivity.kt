package kr.hnu.ice.july07application

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july07application.databinding.ActivityFourthBinding

class FourthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFourthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}