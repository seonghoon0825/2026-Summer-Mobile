package kr.hnu.ice.pickerdialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import java.util.Calendar
import android.text.format.DateFormat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.hnu.ice.pickerdialog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _: DatePicker, y: Int, m: Int, d: Int ->
                binding.dateText.text = "선택한 날짜는 ${y}년 ${m + 1}월 ${d}일 입니다."
            }, year, month, day).show()
        }

        binding.timeBtn.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val is24 = DateFormat.is24HourFormat(this)

            TimePickerDialog(this, { _, h, m ->
                binding.timeText.text = "선택한 시간은 ${h}시 ${m}분 입니다."
            }, hour, minute, is24).show()
        }

        binding.dlgBtn.setOnClickListener {
            val dlg = AlertDialog.Builder(this).run {
                setTitle("알림")
                setMessage("알림 대화상자를 띄웁니다.")
                setPositiveButton("확인") { dialog, which ->
                    dialog.dismiss()
                }
                setNegativeButton("취소") { dialog, which ->
                    dialog.dismiss()
                }
                create()
            }
            dlg.show()
        }

        binding.alertBtn.setOnClickListener {

            val dlg = AlertDialog.Builder(this).run {
                setTitle("확인")
                setMessage("확인 대화상자를 띄웁니다.")
                setPositiveButton("확인") { dialog, which ->
                    dialog.dismiss()
                }
                create()
            }
            dlg.show()
        }
    }
}