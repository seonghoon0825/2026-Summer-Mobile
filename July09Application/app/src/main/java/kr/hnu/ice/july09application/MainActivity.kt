package kr.hnu.ice.july09application

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kr.hnu.ice.july09application.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        Toast.makeText(this, "Back button pressed", Toast.LENGTH_SHORT).show()
        binding.textView.text = "뒤로가기 버튼이 눌렸습니다."
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val menuItem1 : MenuItem = it.add(Menu.NONE, 1, Menu.NONE, "Menu Item 1")
            val menuItem2 : MenuItem = it.add(Menu.NONE, 2, Menu.NONE , "Menu Item 2")
            val menuItem3 : MenuItem = it.add(Menu.NONE, 3, Menu.NONE , "Menu Item 3")

            val searchItem = it.add(Menu.NONE, 4, Menu.NONE, "Search")
            searchItem?.setIcon(android.R.drawable.ic_menu_search)
            searchItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

            val searchView = SearchView(this)
            searchView.queryHint = "Search here"
            searchItem?.actionView = searchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(this@MainActivity,
                        "Search submitted: $query", Toast.LENGTH_SHORT).show()
                    binding.textView.text = "검색어 제출됨: $query"
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    binding.textView.text = "검색어 변경됨: $newText"
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            1 -> {
                Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show()
                binding.textView.text = "메뉴 아이템 1이 선택되었습니다."
            }
            2 -> {
                Toast.makeText(this, "Menu Item 2 selected", Toast.LENGTH_SHORT).show()
                binding.textView.text = "메뉴 아이템 2가 선택되었습니다."
            }
            3 -> {
                Toast.makeText(this, "Menu Item 3 selected", Toast.LENGTH_SHORT).show()
                binding.textView.text = "메뉴 아이템 3이 선택되었습니다."
            }
        }
        return super.onOptionsItemSelected(item)
    }
}