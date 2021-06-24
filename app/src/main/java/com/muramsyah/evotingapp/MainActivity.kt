package com.muramsyah.evotingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.muramsyah.evotingapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpItemKahim()
        prepare()
    }

    private fun setUpItemKahim() {
        binding.include2.container.backgroundTintList = resources.getColorStateList(R.color.cardViewColor1)
        binding.include3.container.backgroundTintList = resources.getColorStateList(R.color.cardViewColor2)
    }

    private fun prepare() {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val countDate = "25-06-2021 12:00:00"
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

        binding.btn1.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val ref = FirebaseDatabase.getInstance().getReference("mahasiswa")
        val mhsId = ref.push().key

        if (mhsId != null) {
            val mhs = Mahasiswa(mhsId, "1830511049", "M Ramdhan Syahputra", "2018-2019", true, true, "1")
            ref.child(mhsId).setValue(mhs).addOnCompleteListener {
                Toast.makeText(this, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    Log.d("dataFirebase", i.getValue(Mahasiswa::class.java)?.name.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}