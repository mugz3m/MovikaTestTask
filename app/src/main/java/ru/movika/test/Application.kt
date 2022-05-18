package ru.movika.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.movika.test.databinding.ActivityMainBinding

class Application : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
