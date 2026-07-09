package kr.hnu.ice.viewpage

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.hnu.ice.viewpage.databinding.ActivityMainBinding

class MyFragmentPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val fragments = listOf(
        BlankFragment(),
        BlankFragment2(),
        BlankFragment3()
    )
    
    override fun getItemCount(): Int = fragments.size
    
    override fun createFragment(position: Int): Fragment = fragments[position]
    }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MyFragmentPagerAdapter(this)
    }
}