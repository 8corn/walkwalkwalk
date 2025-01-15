package com.example.walkwalkwalk.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.LoadingActivity
import com.example.walkwalkwalk.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.startLoginBtn.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
        }

        binding.startJoinBtn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }
}