package com.example.storyapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.customview.CustomButton
import com.example.storyapp.customview.InputEmail
import com.example.storyapp.customview.InputPassword
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.remote.response.RequestLogin
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.LoginViewModel
import com.example.storyapp.ui.viewmodel.RepoViewModelFactory
import com.example.storyapp.ui.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels{
        RepoViewModelFactory(this)
    }

    private lateinit var email: String
    private lateinit var password: String

    private lateinit var myButton: CustomButton
    private lateinit var myEditEmail: InputEmail
    private lateinit var myEditPass: InputPassword

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAction()

        myButton = binding.myButtonLogin
        myEditEmail = binding.emailInput
        myEditPass = binding.passwordInput

        playAnimation()
        setMyButtonEnable()
        supportActionBar?.title = "Login"

        val pref = UserPreference.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLoginState().observe(this) { state ->
            if (state) {
                val intent = Intent(this, ListStoryActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.message.observe(this) {
            val user = loginViewModel.userLogin.value
            checkResponseLogin(it, user?.loginResult?.token, dataStoreViewModel)
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        myEditEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        myEditPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun playAnimation() {
        val email = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1F).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1F).setDuration(500)
        val loginDesc = ObjectAnimator.ofFloat(binding.descLogin, View.ALPHA, 1F).setDuration(500)
        val come = ObjectAnimator.ofFloat(binding.comeSignup, View.ALPHA, 1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1F).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.loginDesc, View.ALPHA, 1F).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.myButtonLogin, View.ALPHA, 1F).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginDesc, come)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, email, pass, btn, together)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val result = myEditEmail.text;myEditPass.text
        myButton.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun setAction() {
        binding.comeSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.myButtonLogin.setOnClickListener {
            binding.apply {
                emailInput.clearFocus()
                passwordInput.clearFocus()
            }
            if (isLoginValid()) {
                email = binding.emailInput.text.toString().trim()
                password = binding.passwordInput.text.toString().trim()
                val user = RequestLogin(
                    email,
                    password
                )
                loginViewModel.getLoginResponse(user)
            }
        }
    }

    private fun isLoginValid(): Boolean {
        return binding.emailInput.isEmailValid && binding.passwordInput.isPasswordValid
    }

    private fun checkResponseLogin(
        message: String,
        token: String?,
        viewModel: DataStoreViewModel
    ) {
        if (message.contains("Login as")) {
            Toast.makeText(
                this,
                "${getString(R.string.login_success)} $message",
                Toast.LENGTH_LONG
            ).show()
            viewModel.saveLoginState(true)
            if (token != null) viewModel.saveToken(token)
            viewModel.saveName(loginViewModel.userLogin.value?.loginResult?.name.toString())
        } else {
            when (message) {
                "Unauthorized" -> {
                    Toast.makeText(this, getString(R.string.unauthorized), Toast.LENGTH_SHORT)
                        .show()
                    binding.emailInput.apply {
                        setText("")
                        requestFocus()
                    }
                    binding.passwordInput.setText("")
                }
                else -> {
                    Toast.makeText(
                        this,
                        "${getString(R.string.message_error)} $message",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}