package com.yangmaolie.hunter.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yangmaolie.hunter.core.base.BaseActivity
import com.yangmaolie.hunter.databinding.ActivityAuthBinding
import com.yangmaolie.hunter.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {

    override val binding: ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Already logged in? Go straight to main
        if (viewModel.authState.value is AuthViewModel.AuthState.Success) {
            navigateToMain()
            return
        }

        observeData()
        lifecycleScope.launch {
            viewModel.anonymousSignIn()
        }
    }

    override fun observeData() {
        super.observeData()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is AuthViewModel.AuthState.Loading -> {
                            // Already showing loading
                        }
                        is AuthViewModel.AuthState.Success -> {
                            navigateToMain()
                        }
                        is AuthViewModel.AuthState.Error -> {
                            binding.tvLoading.text = "错误: ${state.message}"
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
