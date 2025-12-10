package com.example.docstatus.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Реализация [ImageAnalysis.Analyzer], которая обнаруживает QR-коды и другие штрих-коды в кадрах с камеры.
 *
 * Этот класс использует Google ML Kit Barcode Scanning API для обработки изображений с камеры.
 * При успешном обнаружении штрих-кода он вызывает предоставленный колбэк с необработанным
 * строковым значением штрих-кода.
 *
 * @param onQrCodeScanned Лямбда-функция, которая будет вызвана при обнаружении штрих-кода.
 * Функция получает декодированную строку из штрих-кода.
 */
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes: List<Barcode> ->
                    barcodes.firstOrNull()?.rawValue?.let {
                        onQrCodeScanned(it)
                    }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}