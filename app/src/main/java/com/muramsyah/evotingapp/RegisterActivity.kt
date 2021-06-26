package com.muramsyah.evotingapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import com.muramsyah.evotingapp.databinding.ActivityLoginBinding
import com.muramsyah.evotingapp.databinding.ActivityMainBinding
import com.muramsyah.evotingapp.databinding.ActivityRegisterBinding
import com.muramsyah.evotingapp.model.Mahasiswa
import com.muramsyah.evotingapp.utils.FireBaseUtils
import io.reactivex.Observable
import io.reactivex.functions.Function6

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authentication()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener(this)
    }

    @SuppressLint("CheckResult")
    private fun authentication() {
        val nimStream = RxTextView.textChanges(binding.edtNim)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .map { nim ->
                nim.toString().take(2).toInt() < 18 || nim.toString().take(2).toInt() > 21 || nim.length > 10 || nim.length < 10
            }
        nimStream.subscribe {
            showNimAlert(it)
        }

        val force = RxTextView.textChanges(binding.edtForce)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .map { force ->
                force.toString().toInt() < 2018 || force.toString().toInt() > 2021 || force.length < 4
            }
        force.subscribe {
            showForceAlert(it)
        }

        val name = RxTextView.textChanges(binding.edtName)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .map { name ->
                name.length < 3 || name.length > 25
            }
        name.subscribe {
            showNameAlert(it)
        }

        val email = RxTextView.textChanges(binding.edtEmail)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        email.subscribe {
            showEmailAlert(it)
        }

        val password = RxTextView.textChanges(binding.edtPassword)
            .skipInitialValue()
            .filter { it.isNotEmpty() }
            .map { password ->
                password.length < 7
            }
        password.subscribe {
            showPasswordAlert(it)
        }

        val coPassword = Observable.merge(
            RxTextView.textChanges(binding.edtPassword)
                .skipInitialValue()
                .filter { it.isNotEmpty() }
                .map { password ->
                    password.toString() != binding.edtCoPassword.text.toString()
                },
            RxTextView.textChanges(binding.edtCoPassword)
                .skipInitialValue()
                .filter { it.isNotEmpty() }
                .map { coPassword ->
                    coPassword.toString() != binding.edtPassword.text.toString()
                }
        )
        coPassword.subscribe {
            showCoPasswordAlert(it)
        }

        val invalidFieldsStream = Observable.combineLatest(
            nimStream,
            force,
            name,
            email,
            password,
            coPassword,
            { t1, t2, t3, t4, t5, t6 ->
                !t1 && !t2 && !t3 && !t4 && !t5 && !t6
            }
        )
        invalidFieldsStream.subscribe { isValid ->
            binding.btnRegister.isEnabled = isValid
        }
    }

    private fun showNimAlert(isNotValid: Boolean) {
        binding.tilNim.error = if(isNotValid) "Nim tidak valid!" else null
    }

    private fun showForceAlert(isNotValid: Boolean) {
        binding.tilForce.error = if(isNotValid) "Hanya bisa angkatan 2018 - 2021!" else null
    }

    private fun showNameAlert(isNotValid: Boolean) {
        binding.tilName.error = if(isNotValid) "Nama tidak valid!" else null
    }

    private fun showEmailAlert(isNotValid: Boolean) {
        binding.tilEmail.error = if(isNotValid) "Email tidak valid!" else null
    }

    private fun showPasswordAlert(isNotValid: Boolean) {
        binding.tilPassword.error = if(isNotValid) "Password kurang dari 7!" else null
    }

    private fun showCoPasswordAlert(isNotValid: Boolean) {
        binding.tilCoPassword.error = if(isNotValid) "Password tidak sama!" else null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        FireBaseUtils.auth.createUserWithEmailAndPassword(binding.edtEmail.text.toString().trim(), binding.edtPassword.text.toString().trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val mahasiswa = FireBaseUtils.auth.currentUser?.let {
                        Mahasiswa(
                            it.uid,
                            binding.edtEmail.text.toString().trim(),
                            binding.edtNim.text.toString().trim(),
                            binding.edtName.text.toString().trim(),
                            binding.edtForce.text.toString().trim(),
                            false,
                            "0"
                        )
                    }

                    FireBaseUtils.ref.getReference("Users").child(FireBaseUtils.auth.currentUser!!.uid).setValue(mahasiswa)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Register sukses!", Toast.LENGTH_SHORT).show()
                            FireBaseUtils.auth.currentUser?.sendEmailVerification()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Register gagal : ${task.exception}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}