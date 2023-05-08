package com.itproger.weather

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var user_field: EditText? = null
    private var main_btn: Button? = null
    private var result_info: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_field = findViewById(R.id.user_field)
        main_btn = findViewById(R.id.main_btn)

        main_btn?.setOnClickListener {
            if(user_field?.text?.toString()?.trim()?.equals("777") == true)
                startActivity(Intent(this, SecondActivity::class.java))
            else
                Toast.makeText(this, "Неправильный пароль", Toast.LENGTH_SHORT).show()
        }
    }
}