package com.muramsyah.evotingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.muramsyah.evotingapp.databinding.ActivityMainBinding
import com.muramsyah.evotingapp.model.Mahasiswa
import com.muramsyah.evotingapp.utils.FireBaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var mahasiswa: Mahasiswa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.Main).launch {
            FireBaseUtils.auth.currentUser?.let {
                FireBaseUtils.ref.getReference("Users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(Mahasiswa::class.java)
                        if (user != null) {
                            mahasiswa = user
                            binding.tvName.text = user.nim

                            if (user.isVote == true) {
                                binding.btn1.isEnabled = false
                                binding.btn2.isEnabled = false
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }

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
            voteData1()
        }

        binding.btn2.setOnClickListener {
            voteData2()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun voteData1() {
        FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("voteId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == "1" || snapshot.value == "1") {
                    Toast.makeText(this@MainActivity, "Kamu hanya bisa memvoting 1x", Toast.LENGTH_SHORT).show()
                } else {
                    FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("voteId").setValue("2")
                    FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("vote").setValue(true).addOnCompleteListener {
                        Toast.makeText(this@MainActivity, "Kamu telah memvoting Ramdhan!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun voteData2() {
        FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("voteId").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == "1" || snapshot.value == "2") {
                    Toast.makeText(this@MainActivity, "Kamu hanya bisa memvoting 1x", Toast.LENGTH_SHORT).show()
                } else {
                    FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("voteId").setValue("2")
                    FireBaseUtils.ref.getReference("Users").child(mahasiswa.id).child("vote").setValue(true).addOnCompleteListener {
                        Toast.makeText(this@MainActivity, "Kamu telah memvoting Ramdhan!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun logout() {
        FireBaseUtils.auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
        Toast.makeText(this, "Kamu telah keluar!", Toast.LENGTH_SHORT).show()
    }
}