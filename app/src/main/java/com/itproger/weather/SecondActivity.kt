package com.itproger.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL

class SecondActivity : AppCompatActivity() {
    private var result_info2: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        result_info2 = findViewById(R.id.result_info2)
            //Toast.makeText(this, "Вы успешно вошли", Toast.LENGTH_SHORT).show()
        val ip_port: String = "http://10.0.2.2:83"
        var mech = "91528AAD-2D7C-43BE-B765-192E89BF3C77"
        ///http://localhost:83/WinnumCloud/rest/unauth/get/url/91528AAD-2D7C-43BE-B765-192E89BF3C77?id=A13"
        var url: String = ("$ip_port/WinnumCloud/rest/unauth/get/url/$mech?id=A51")
        doAsync {
            var apiResponse = URL(url).readText()
            var result = JSONObject(apiResponse).getJSONArray("message").getJSONObject(0).getString("VALUE")
            result_info2?.text = "Оператор: $result\nВремя последнего включения станка:"

            }
        }
    }
//val appid = winnum.org.app.WNApplicationInstance:1
//var oid = "winnum.org.product.WNProduct:1"
//val pid=winnum.org.product.WNProduct:1
//http://127.0.0.1:82/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:1&mode=yes"