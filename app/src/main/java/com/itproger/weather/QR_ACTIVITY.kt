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
                startActivity(Intent(this, MainActivity2::class.java))
            } else {
                val qrCodeValue = result.contents
                var intent = getIntent()
                val ip_port = intent.getStringExtra("ip_port").toString()
                val tags = intent.getStringExtra("tags").toString()
                Toast.makeText(this, "Станок $qrCodeValue", Toast.LENGTH_LONG).show()
                intent = (Intent(this, SecondActivity::class.java))
                intent.putExtra("qrCodeValue", qrCodeValue)
                intent.putExtra("tags", tags)
                intent.putExtra("ip_port",ip_port)
                startActivity(intent)

                // Дальнейшая обработка значения QR-кода
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}