package com.example.walkwalkwalk.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}