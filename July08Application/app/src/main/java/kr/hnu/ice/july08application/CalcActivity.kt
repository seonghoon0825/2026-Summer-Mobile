package kr.hnu.ice.july08application

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.july08application.databinding.ActivityCalcBinding

class CalcActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCalcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val num1 = intent.getDoubleExtra("num1", 0.0)
        val num2 = intent.getDoubleExtra("num2", 0.0)
        val op = intent.getStringExtra("op")

        val inputText = "전달받은 데이터는 $num1 $op $num2 입니다."
        binding.textInput.text = inputText // setText(inputText) 대신 text 속성을 사용하여 텍스트를 설정합니다.

        val result = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 != 0.0) num1 / num2 else null
            else -> null
        }

        binding.calcBtn.setOnClickListener {
            if (result != null) {
                val intent = intent
                intent.putExtra("result", result)
                setResult(RESULT_OK, intent)
            } else {
                val intent = intent
                intent.putExtra("result", "0으로 나눌 수 없습니다.")
                setResult(RESULT_CANCELED, intent)
            }
            finish()
        }

        binding.errBtn.setOnClickListener {
            val intent = intent
            intent.putExtra("result", "전달받은 데이터가 올바르지 않습니다.")
            setResult(RESULT_CANCELED, intent)
            finish()
        }

    }
}