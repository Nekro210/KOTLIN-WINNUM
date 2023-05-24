package com.itproger.weather

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.jetbrains.anko.doAsync
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder


@RequiresApi(Build.VERSION_CODES.KITKAT)
class SecondActivity : AppCompatActivity() {
    private var result_info2: TextView? = null
    private var text: String? = null
    @SuppressLint("SetTextI18n")
    fun getCon(url: URL,cookie:String): JsonNode {
        var con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("cookie", cookie)
        con.connect()
        //val responseCode = con.responseCode
        val objectMapper = XmlMapper()
        val reader = BufferedReader(InputStreamReader(con.inputStream))
        var inputLine: String?
        val response = StringBuffer()
        while (reader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        reader.close()
        val xml = response.toString()
        var json = objectMapper.readValue(xml, JsonNode::class.java)
        json = json["item"]
        return json
    }
    fun getData() {
        val uuid= arrayOf("91528AAD-2D7C-43BE-B765-192E89BF3C77")
        val ip_port = "http://10.0.2.2:82"
        val cookie = "JSESSIONID=TL0PWH6XtXr2Uhu0bVPBLaucdRuXr5yYxDKX9jtA.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; Studio-346eca46=2707507b-8e30-41e9-9eed-0596a626828e"
        val qrCodeValue = 1
        var url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.modeling.WNProductTemplate:$qrCodeValue&mode=yes")
        var json = getCon(url,cookie)
        text=URLDecoder.decode("Описание: ${json["Description"]}\nИнв. номер: ${json["master__partNumber"]}\nИмя: ${json["master__name"]}\nМодель: ${json["Model"]}\nСоздано: ${json["PersistInfo__createStamp"]}\nИнформация о ТО: ${json["RevisionInfo__state"]}\n","UTF-8")
        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A12&stype=bycount&count=1&mode=yes")

        json = getCon(url,cookie)

        if (json["value"].toString() == "\"0\"") {
            text += URLDecoder.decode("${json["event_time"]} станок был во включенном состоянии.\n","UTF-8")
        }
        else if (json["value"].toString()=="\"1\""){
            text+=URLDecoder.decode("${json["event_time"]} станок был в состоянии аварийной остановки.\n","UTF-8")
        }
        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A51&stype=bycount&count=1&mode=yes")
        json = getCon(url,cookie)
        text+=URLDecoder.decode("${json["event_time"]} Оператор ${json["value"]}\n","UTF-8")
        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A66&stype=bycount&count=1&mode=yes")
        json = getCon(url,cookie)
        text += URLDecoder.decode("Код оператора: ${json["value"]}\n","UTF-8")
        //text = URLDecoder.decode(text,"UTF-8")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        result_info2 = findViewById(R.id.result_info2)
        //result_info2?.text="chek"
        val thread = Thread{
            try {
                getData()
                //Log.d("TEXT", text.toString())
            } catch (e: Exception) {
                result_info2?.text="Произошла ошибка, не удалось получить данные("
                Log.d("ERRORTYPE", e.message.toString())
            }
        }
        thread.start()
        thread.join()
        result_info2?.text=text.toString()

    }
    }
//val appid = winnum.org.app.WNApplicationInstance:1

//http://127.0.0.1:82/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:1&mode=yes"