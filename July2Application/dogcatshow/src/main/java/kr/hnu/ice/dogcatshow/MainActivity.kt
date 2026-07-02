package kr.hnu.ice.dogcatshow

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.dogcatshow.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dogButton.setOnClickListener {
            binding.dogView.visibility = View.VISIBLE
            binding.catView.visibility = View.GONE
        }
        binding.catButton.setOnClickListener {
            binding.dogView.visibility = View.GONE
            binding.catView.visibility = View.VISIBLE
        }
    }
}