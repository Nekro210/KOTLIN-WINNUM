package com.itproger.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var user_field2: EditText? = null
    private var user_field: EditText? = null
    private var main_btn: Button? = null
    private var bottomRightButton: Button? = null



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        user_field2 = findViewById(R.id.user_field2)
        user_field = findViewById(R.id.user_field)
        val pref = getSharedPreferences("test", MODE_PRIVATE)
        val edit = pref.edit()

        user_field2?.setText(pref.getString("password",getString(R.string.hint_user_field)))
        user_field?.setText(pref.getString("login",getString(R.string.login)))

        main_btn = findViewById(R.id.main_btn)
        bottomRightButton = findViewById((R.id.bottomRightButton))

        main_btn?.setOnClickListener {

            edit.putString("password",user_field2?.text.toString()) // сохранение пароля и логина
            edit.putString("login",user_field?.text.toString())
            edit.apply()
            user_field2?.setText(pref.getString("password",getString(R.string.hint_user_field))) // новые логин и пароль становятся текстом по умолчанию
            user_field?.setText(pref.getString("login",getString(R.string.login)))
            val intent = (Intent(this, QR_ACTIVITY::class.java))
            startActivity(intent)
        }
        bottomRightButton?.setOnClickListener {// кнопка настроек
            val intent = (Intent(this, MainActivity2::class.java))
            edit.putString("password",user_field2?.text.toString())
            edit.putString("login",user_field?.text.toString())
            edit.apply()
            startActivity(intent)
        }

    }
}