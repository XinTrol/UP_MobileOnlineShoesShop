package com.example.up_mobileappv2.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object BarcodeGenerator {
    fun generateBarcode(content: String, width: Int, height: Int): Bitmap? {
        return try {
            val writer = QRCodeWriter() // или Code128Writer для штрих-кода
            val bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}