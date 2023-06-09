package com.example.storyapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.customview.*
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.remote.response.RequestLogin
import com.example.storyapp.remote.response.RequestSignup
import com.example.storyapp.ui.viewmodel.*

class SignUp : AppCompatActivity() {
    private val signupViewModel: SignupViewModel by viewModels{
        RepoViewModelFactory(this)
    }
    private val loginViewModel: LoginViewModel by viewModels{
        RepoViewModelFactory(this)
    }
    private lateinit var binding: ActivitySignUpBinding
    private var isPasswordMatch: Boolean = false

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    private lateinit var myButton: CustomButton
    private lateinit var myEditName: InputName
    private lateinit var myEditEmail: InputEmail
    private lateinit var myEditPass: InputPassword
    private lateinit var myEditCPass: InputConfirmPass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAction()

        myButton = binding.myButtonSignup
        myEditName = binding.nameInput
        myEditEmail = binding.emailInput
        myEditPass = binding.passwordInput
        myEditCPass = binding.confirmPassInput

        playAnimation()
        setMyButtonEnable()
        supportActionBar?.title = "Sign Up"

        val pref = UserPreference.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLoginState().observe(this) { state ->
            if (state) {
                val intent = Intent(this, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                dataStoreViewModel.saveToken("")
                dataStoreViewModel.saveName("")
            }
        }

        signupViewModel.message.observe(this) {
            checkResponseSignup(it)
        }
        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.userLogin.observe(this) {
            dataStoreViewModel.saveLoginState(true)
            dataStoreViewModel.saveToken(it.loginResult.token)
            dataStoreViewModel.saveName(it.loginResult.name)

        }
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        myEditName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
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

        myEditCPass.addTextChangedListener(object : TextWatcher {
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
        val name = ObjectAnimator.ofFloat(binding.nameInput, View.ALPHA, 1F).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1F).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.passwordInput, View.ALPHA, 1F).setDuration(500)
        val cpass = ObjectAnimator.ofFloat(binding.confirmPassInput, View.ALPHA, 1F).setDuration(500)
        val loginDesc = ObjectAnimator.ofFloat(binding.descLogin, View.ALPHA, 1F).setDuration(500)
        val come = ObjectAnimator.ofFloat(binding.comeLogin, View.ALPHA, 1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.signupTitle, View.ALPHA, 1F).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.signupDesc, View.ALPHA, 1F).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.myButtonSignup, View.ALPHA, 1F).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(loginDesc, come)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, name, email, pass, cpass, btn, together)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val result = myEditName.text;myEditEmail.text;myEditPass.text;myEditCPass.text
        myButton.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun setAction() {
        binding.passwordInput.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }
        binding.confirmPassInput.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }
        binding.comeLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        binding.myButtonSignup.setOnClickListener {
            binding.apply {
                emailInput.clearFocus()
                nameInput.clearFocus()
                passwordInput.clearFocus()
                confirmPassInput.clearFocus()
            }
            if (isSignupValid()) {
                name = binding.nameInput.text.toString().trim()
                email = binding.emailInput.text.toString().trim()
                password = binding.passwordInput.text.toString().trim()
                val user = RequestSignup(
                    name,
                    email,
                    password
                )
                signupViewModel.getSignupResponse(user)
            }
        }
    }

    private fun checkResponseSignup(message: String) {
        if (message == "User created") {
            Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_SHORT).show()
            val user = RequestLogin(
                email,
                password
            )
            loginViewModel.getLoginResponse(user)
        } else {
            when (message) {
                "Bad Request" -> {
                    Toast.makeText(this, getString(R.string.email_taken), Toast.LENGTH_SHORT).show()
                    binding.emailInput.apply {
                        setText("")
                        requestFocus()
                    }
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

    private fun isSignupValid(): Boolean {
        return binding.nameInput.isNameValid && binding.emailInput.isEmailValid &&
                binding.passwordInput.isPasswordValid && binding.confirmPassInput.isConfirmPassValid && isPasswordMatch
    }

    private fun isPasswordMatch() {
        if (binding.passwordInput.text.toString().trim() != binding.confirmPassInput.text.toString().trim()) {
            binding.confirmPassInput.error = resources.getString(R.string.pass_not_match)

            isPasswordMatch = false
        } else {
            isPasswordMatch = true
            binding.confirmPassInput.error = null
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