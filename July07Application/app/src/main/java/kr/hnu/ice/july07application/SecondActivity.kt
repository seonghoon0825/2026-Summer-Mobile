package kr.hnu.ice.july07application

import android.content.Intent
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

        val where = intent.getStringExtra("where")
        val value = intent.getIntExtra("value", 0)
        Toast.makeText(this, "MainActivity에서 전달받은 값: where=$where, value=$value", Toast.LENGTH_LONG).show()

        binding.goMain2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.goThird2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        binding.goFourth2.setOnClickListener {
            val intent = Intent(this, FourthActivity::class.java)
            startActivity(intent)
        }
        binding.finish2nd.setOnClickListener {
            Toast.makeText(this, "SecondActivity 종료", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}