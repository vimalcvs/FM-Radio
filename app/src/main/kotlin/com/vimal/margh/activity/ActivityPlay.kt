package com.vimal.margh.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vimal.margh.databinding.ActivityPlaysBinding


class ActivityPlay : AppCompatActivity() {

    private lateinit var binding: ActivityPlaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}