package com.example.docstatus.ui.components

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.docstatus.ui.theme.AppColors
import com.example.docstatus.utils.QrCodeAnalyzer
import java.util.concurrent.Executors

/**
 * Кастомное текстовое поле в стиле приложения DocStatus.
 *
 * Это текстовое поле стилизовано в соответствии с темой приложения и предоставляет
 * встроенную поддержку для переключения видимости пароля.
 *
 * @param value Входной текст для отображения в поле.
 * @param onValueChange Колбэк, который вызывается при обновлении текста службой ввода.
 * @param label Метка, отображаемая внутри текстового поля.
 * @param isPassword Указывает, предназначено ли это поле для ввода пароля.
 * @param isPasswordVisible Указывает, должен ли пароль быть видимым.
 * @param onVisibilityToggle Колбэк, вызываемый при нажатии на иконку видимости.
 * @param keyboardOptions Опции программной клавиатуры, которые могут быть применены к этому полю.
 */
@Composable
fun DocStatusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AppColors.TextHint) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onVisibilityToggle?.invoke() }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Показать пароль",
                        tint = AppColors.TextHint
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppColors.Surface,
            unfocusedContainerColor = AppColors.Surface,
            focusedBorderColor = AppColors.PrimaryBlue.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = AppColors.TextWhite,
            unfocusedTextColor = AppColors.TextWhite
        )
    )
}

/**
 * Круглая кнопка для запуска биометрической аутентификации.
 *
 * @param onClick Колбэк, вызываемый при нажатии на кнопку.
 * @param size Размер кнопки.
 */
@Composable
fun BiometricButton(onClick: () -> Unit, size: Dp = 70.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .border(1.dp, AppColors.BiometricBorder, CircleShape)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .background(AppColors.BiometricBackground),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Fingerprint,
            contentDescription = "Биометрия",
            tint = AppColors.TextWhite,
            modifier = Modifier.size(size / 2)
        )
    }
}

/**
 * Composable-компонент, который отображает превью камеры и привязывает его к анализатору QR-кодов.
 *
 * Эта функция инкапсулирует шаблонный код, необходимый для настройки CameraX, включая
 * создание [PreviewView], привязку его к жизненному циклу и настройку сценария использования
 * [ImageAnalysis] с [QrCodeAnalyzer].
 *
 * @param onQrDetected Колбэк, который вызывается при успешном обнаружении QR-кода.
 */
@Composable
fun CameraPreview(onQrDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analyzer ->
                        analyzer.setAnalyzer(cameraExecutor, QrCodeAnalyzer(onQrDetected))
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}