package kr.hnu.ice.july07application

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july07application.databinding.ActivitySecondBinding
import kr.hnu.ice.july07application.databinding.ActivityThridBinding

class ThridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityThridBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}