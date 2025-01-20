package com.example.walkwalkwalk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.walkwalkwalk.alarmfragment.AlarmFragment
import com.example.walkwalkwalk.databinding.ActivityMainBinding
import com.example.walkwalkwalk.mainfragment.MainFragment
import com.example.walkwalkwalk.markfragment.BookMarkFragment
import com.example.walkwalkwalk.settingfragment.SettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(MainFragment())

        binding.mainBottomNavi.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when(item.itemId) {
                R.id.Home -> selectedFragment = MainFragment()
                R.id.Search -> selectedFragment = BookMarkFragment()
                R.id.Alarm -> selectedFragment = AlarmFragment()
                R.id.Setting -> selectedFragment = SettingFragment()
            }
            loadFragment(selectedFragment!!)
            true
        }

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_control_nav, fragment)
            .commit()
    }
}