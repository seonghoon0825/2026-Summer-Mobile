package kr.hnu.ice.july08application

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july08application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = "계산된 결과는" + result.data?.getDoubleExtra("result", 0.0) + "입니다."
                binding.txtResult.text = data
                binding.txtResult.setTextColor(Color.BLUE)
            }
            else {
                val data = result.data?.getStringExtra("result") + "원인으로 계산이 취소되었습니다."
                binding.txtResult.text = data
                binding.txtResult.setTextColor(Color.RED)
            }
        }

        binding.sendBtn.setOnClickListener {
            val intent = Intent(this, CalcActivity::class.java)
            intent.putExtra("num1", binding.num1.text.toString().toDouble())
            intent.putExtra("num2", binding.num2.text.toString().toDouble())
            val op = when (binding.opRadio.checkedRadioButtonId) {
                R.id.addRdo -> "+"
                R.id.subRdo -> "-"
                R.id.mulRdo -> "*"
                R.id.divRdo -> "/"
                else -> ""
            }
            intent.putExtra("op", op)
            requestLauncher.launch(intent)
        }
    }
}