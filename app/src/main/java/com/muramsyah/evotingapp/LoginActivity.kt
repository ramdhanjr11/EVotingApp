package com.muramsyah.evotingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.muramsyah.evotingapp.databinding.ActivityLoginBinding
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.functions.Function3

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        authentication()
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
}