package com.example.docstatus.ui.login

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.docstatus.data.TokenManager
import com.example.docstatus.data.model.LoginResponse
import com.example.docstatus.data.repository.LoginRepository
import com.example.docstatus.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val loginRepository = LoginRepository()
    private val sessionManager = SessionManager(application)

    val loginResult = mutableStateOf<LoginResponse?>(null)
    val error = mutableStateOf<String?>(null)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = loginRepository.login(username, password)
                sessionManager.saveAuthToken(response.accessToken)
                TokenManager.accessToken = response.accessToken
                loginResult.value = response
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }
}