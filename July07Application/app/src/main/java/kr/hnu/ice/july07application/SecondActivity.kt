package kr.hnu.ice.july07application

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july07application.databinding.ActivityMainBinding
import kr.hnu.ice.july07application.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finish2nd.setOnClickListener {
            Toast.makeText(this, "SecondActivity 종료", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}