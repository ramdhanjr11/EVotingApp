package com.muramsyah.evotingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.muramsyah.evotingapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var isExistUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            authentication()
        }
    }

    private fun authentication() {
        val nim = binding.edtNim.text.toString()
        val name = binding.edtName.text.toString()
        val force = binding.edtForce.text.toString()

        if (nim.isEmpty()) {
            binding.edtNim.setError("Isi nim terlebih dahulu!")
            binding.edtNim.requestFocus()
            return
        }

        if (name.isEmpty()) {
            binding.edtName.setError("Isi name terlebih dahulu!")
            binding.edtNim.requestFocus()
            return
        }

        if (force.isEmpty()) {
            binding.edtForce.setError("Isi angkatan terlebih dahulu!")
            binding.edtNim.requestFocus()
            return
        }

        if (nim.isNotEmpty() && name.isNotEmpty() && force.isNotEmpty()) {
            if (isValidation(nim, force)) {
                if (isExistUser) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Tidak ada pengguna dengan nim $nim", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Invalid data!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun isValidation(nim: String, force: String): Boolean {
        val firstNim = nim.take(2).toInt()
        val lastNim = nim.takeLast(3).toInt()

        val isValidFirstNim = !(firstNim > 21 || firstNim < 18)
        val isValidLastNim = !(lastNim > 400 || lastNim == 0)
        val isValidForce = !(force.toInt() > 2021 || force.toInt() < 2018)

        Log.d("isValid", isValidFirstNim.toString() + " " + isValidLastNim.toString() + " " + isValidForce.toString())
        if (isValidFirstNim && isValidLastNim && isValidForce) {
            val query = FirebaseDatabase.getInstance().getReference("mahasiswa")
                .orderByChild("nim")
                .equalTo(nim)

            query.addListenerForSingleValueEvent(valueEventListener)
            return true
        } else {
            return false
        }
    }

    val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                isExistUser = true
                for (i in snapshot.children) {
                    val mhs = i.getValue(Mahasiswa::class.java)
                    if (mhs != null) {
                        Log.d("mhs", mhs.name)
                    }
                }
            } else {
                Log.d("mhs", "Kosong")
                isExistUser = false
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}