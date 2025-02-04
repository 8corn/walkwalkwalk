package com.example.walkwalkwalk.mainfragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.walkwalkwalk.databinding.ActivitySearchRoadBinding

class SearchRoadActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchRoadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchRoadBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}