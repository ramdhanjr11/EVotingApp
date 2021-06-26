package com.muramsyah.evotingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.muramsyah.evotingapp.databinding.ActivityLoginBinding
import com.muramsyah.evotingapp.utils.FireBaseUtils
import io.reactivex.Observable

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            loginUser()
        }

        authentication()
    }

    private fun loginUser() {
        FireBaseUtils.auth.signInWithEmailAndPassword(binding.edtEmail.text.toString().trim(), binding.edtPassword.text.toString().trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                        if (FireBaseUtils.auth.currentUser!!.isEmailVerified) {
                            binding.progressBar.visibility = View.GONE
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, "Email belum terverifikasi!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Email belum terdaftar atau salah password!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("CheckResult")
    private fun authentication() {
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

        val invalidFieldsStream = Observable.combineLatest(
            email,
            password,
            { email, password ->
                !email && !password
            }
        )
        invalidFieldsStream.subscribe { isValid ->
            binding.btnLogin.isEnabled = isValid
        }
    }

    private fun showEmailAlert(isNotValid: Boolean) {
        binding.tilEmail.error = if(isNotValid) "Email tidak valid!" else null
    }

    private fun showPasswordAlert(isNotValid: Boolean) {
        binding.tilPassword.error = if(isNotValid) "Password kurang dari 7!" else null
    }

    override fun onStart() {
        super.onStart()
        if (FireBaseUtils.auth.currentUser != null) {
            if (FireBaseUtils.auth.currentUser!!.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Silahkan verfikasi di email anda!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}