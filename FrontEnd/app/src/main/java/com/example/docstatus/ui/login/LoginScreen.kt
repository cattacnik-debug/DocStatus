package com.example.docstatus.ui.login

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.docstatus.ui.components.DocStatusTextField
import com.example.docstatus.ui.theme.AppColors
import kotlinx.coroutines.delay

/**
 * Composable-функция, которая отрисовывает экран входа в приложение.
 *
 * Этот экран предоставляет элементы UI для аутентификации пользователя, включая текстовые поля
 * для имени пользователя и пароля, кнопку входа и обратную связь при неудачных попытках входа.
 * Он спроектирован так, чтобы быть вертикально прокручиваемым и адаптироваться к появлению
 * экранной клавиатуры.
 *
 * @param onLoginSuccess Лямбда-функция, которая будет вызвана после успешной аутентификации.
 * Обычно используется для навигации к основной части приложения.
 * @param loginViewModel Экземпляр [LoginViewModel], который управляет логикой аутентификации
 * и состоянием этого экрана.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var failedAttempts by remember { mutableStateOf(0) }
    var showFailedAttemptsMessage by remember { mutableStateOf(false) }

    val loginResult by loginViewModel.loginResult
    val error by loginViewModel.error

    LaunchedEffect(loginResult) {
        if (loginResult != null) {
            failedAttempts = 0
            onLoginSuccess()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            failedAttempts++
            loginViewModel.error.value = null 
        }
    }

    LaunchedEffect(failedAttempts) {
        if (failedAttempts > 0) {
            showFailedAttemptsMessage = true
            delay(4000)
            showFailedAttemptsMessage = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .imePadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(AppColors.LogoBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCodeScanner, "Logo", tint = AppColors.IconColor, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("ДокСтатус", color = AppColors.TextWhite, fontSize = 24.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DocStatusTextField(username, { username = it }, "Логин", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                DocStatusTextField(password, { password = it }, "Пароль", isPassword = true, isPasswordVisible = isPasswordVisible, onVisibilityToggle = { isPasswordVisible = !isPasswordVisible })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { loginViewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Войти", fontSize = 16.sp, color = Color.White)
            }

            AnimatedVisibility(visible = showFailedAttemptsMessage, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Неудачных попыток: $failedAttempts",
                        color = AppColors.StatusRed,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}