package kr.hnu.ice.visibleclick

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val visibleBtn: Button = findViewById(R.id.visibleBtn)
        val invisibleBtn: Button = findViewById(R.id.invisibleBtn)
        val goneBtn: Button = findViewById(R.id.goneBtn)
        val targetBtn: Button = findViewById(R.id.targetBtn)

        visibleBtn.setOnClickListener {
            targetBtn.visibility = Button.VISIBLE
        }
        invisibleBtn.setOnClickListener {
            targetBtn.visibility = Button.INVISIBLE
        }
        goneBtn.setOnClickListener {
            targetBtn.visibility = Button.GONE
        }
    }
}