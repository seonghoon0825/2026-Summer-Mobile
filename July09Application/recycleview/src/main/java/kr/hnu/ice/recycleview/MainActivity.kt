package kr.hnu.ice.recycleview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kr.hnu.ice.recycleview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        for (i in 1..50) {
            datas.add("Item $i")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = MyAdapter(datas)

        binding.addBtn.setOnClickListener {
            val newItem = "Item ${datas.size + 1}"
            datas.add(newItem)
            binding.recyclerView.adapter?.notifyItemInserted(datas.size - 1)
        }
        binding.delBtn.setOnClickListener {
            if (datas.isNotEmpty()) {
                datas.removeAt(datas.size - 1)
                binding.recyclerView.adapter?.notifyItemRemoved(datas.size)
            }
        }

    }
}