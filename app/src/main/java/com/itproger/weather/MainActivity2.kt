package com.itproger.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    private var user_field2: EditText? = null
    private var user_field3: EditText? = null
    private var main_btn2: Button? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val pref = getSharedPreferences("test", MODE_PRIVATE)

        user_field2 = findViewById(R.id.user_field2)
        user_field3 = findViewById(R.id.user_field3)

        user_field2?.setText(pref.getString("ip",getString(R.string.ip_port)))
        user_field3?.setText(pref.getString("tags",getString(R.string.tags)))

        main_btn2 = findViewById(R.id.main_btn2)

        main_btn2?.setOnClickListener {
            val edit = pref.edit()
            edit.putString("ip",user_field2?.text.toString()) // сохранение ip и тегов
            edit.putString("tags",user_field3?.text.toString())
            edit.apply()
            user_field2?.setText(pref.getString("ip", getString(R.string.ip_port)))
            user_field3?.setText(pref.getString("tags",getString(R.string.tags)))
            val intent = (Intent(this, MainActivity::class.java))
            startActivity(intent)
            Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("SuspiciousIndentation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(
            this,
            MainActivity::class.java // если нажали кнопку назад возвращаемся назад
        )
        startActivity(intent)
        finish()

    }
}