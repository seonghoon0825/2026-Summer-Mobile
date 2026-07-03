package kr.hnu.ice.viewbindingexam

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.viewbindingexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetBtn.setOnClickListener {
            binding.titleText.text = "ViewBinding Exam"
        }

        binding.changeBtn.setOnClickListener {
            binding.titleText.text = "텍스트가 변경되었음"
        }
    }
}