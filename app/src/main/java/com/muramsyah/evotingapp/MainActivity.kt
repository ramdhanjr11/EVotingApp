package com.muramsyah.evotingapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.muramsyah.evotingapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val countDate = "23-06-2021 12:00:00"
        val now = Date()

        val date: Date = sdf.parse(countDate)
        val currentTime: Long = now.getTime()
        val newYearDate: Long = date.getTime()
        val countDownToNewYear = newYearDate - currentTime

        binding.mycountdown.start(countDownToNewYear)
        binding.mycountdown.setOnCountdownEndListener {
            Toast.makeText(this, "Waktu Voting Selesai", Toast.LENGTH_SHORT).show()
        }

        binding.include2.container.setOnClickListener {
            if (binding.expandableLayout.isExpanded) {
                binding.expandableLayout.collapse()
            } else {
                binding.expandableLayout.expand()
            }
        }

        binding.include3.container.setOnClickListener {
            if (binding.expandableLayout2.isExpanded) {
                binding.expandableLayout2.collapse()
            } else {
                binding.expandableLayout2.expand()
            }
        }

    }
}