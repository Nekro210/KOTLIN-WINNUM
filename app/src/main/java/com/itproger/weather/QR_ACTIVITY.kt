package com.itproger.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

@Suppress("DEPRECATION")
class QR_ACTIVITY : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Сканируйте QR-код")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        // Запуск сканера по нажатию на кнопку
        integrator.initiateScan()
    }
    // Обработка результата сканирования QR-кода
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Отменено", Toast.LENGTH_LONG).show()
            } else {
                var qrCodeValue = result.contents
                Toast.makeText(this, "Станок №$qrCodeValue", Toast.LENGTH_LONG).show()
                val intent = (Intent(this, SecondActivity::class.java))
                intent.putExtra("qrCodeValue", qrCodeValue)
                startActivity(intent)

                // Дальнейшая обработка значения QR-кода
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}