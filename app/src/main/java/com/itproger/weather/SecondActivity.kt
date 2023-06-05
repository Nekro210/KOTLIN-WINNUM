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
    private var text = ""
    @SuppressLint("SetTextI18n")
    fun getCon(url: URL,cookie:String): JsonNode {
        var con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("cookie", cookie)
        con.connect()
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
    fun add_text(stroka:String,json:JsonNode){
        if (!json.isNull)
            text += stroka
    }
    fun getData() {
        val uuid= arrayOf("91528AAD-2D7C-43BE-B765-192E89BF3C77")
        val ip_port = "http://10.0.2.2:82"
        val cookie = "JSESSIONID=cAPOaK0vuiGWSDVEhCZsYeoCvN6dxuuYe1zsRhpx.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; Studio-346eca46=2707507b-8e30-41e9-9eed-0596a626828e"
        val qrCodeValue = 1
        //val qrCodeValue = intent.getStringExtra("qrCodeValue")
        var url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.modeling.WNProductTemplate:$qrCodeValue&mode=yes")
        var json = getCon(url,cookie)
        add_text(URLDecoder.decode("Описание: ${json["Description"]}\nИнв. номер: ${json["master__partNumber"]}\nИмя: ${json["master__name"]}\nМодель: ${json["Model"]}\nСоздано: ${json["PersistInfo__createStamp"]}\nИнформация о ТО: ${json["RevisionInfo__state"]}\n\n","UTF-8"),json)

        url = URL("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:19&mode=yes""")
        json = getCon(url,cookie)
        add_text(URLDecoder.decode("Имя программы: ${json["value"]}\n\n","UTF-8"),json)

        url = URL("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:32&mode=yes""")
        json = getCon(url,cookie)
        add_text(URLDecoder.decode("Текущий оператор ${json["value"]}\n","UTF-8"),json)

        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A66&stype=bycount&count=1&mode=yes")
        json = getCon(url,cookie)
        add_text(URLDecoder.decode("Последний код оператора: ${json["value"]}\n\n","UTF-8"),json)

        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShift&oid=winnum.org.product.WNProduct:$qrCodeValue&mode=yes")
        json = getCon(url,cookie)

        val shift_start = json[0]["PersistInfo__createStamp"].toString().replace("\"","")
        val shift_id = json[0]["id"].toString().replace("\"","")

        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationTagHelper&men=getLoadingCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&from=$shift_start&timeout=60000&rounding=2&adjust=true&mode=yes")
        json = getCon(url, cookie)
        var i =0
        var text_work = "Показатели по текущей смене:\n"
        while (i<json.size()){
            if (URLDecoder.decode(json[i]["shiftOid"].toString(),"UTF-8")=="\"$shift_id\"") {
                text_work += URLDecoder.decode(("${json[i]["tagName"]}: ${json[i]["hours"]} часов (${json[i]["percent"]}"),"UTF-8")
                text_work +=" %)\n"
            }
            i++
        }
        add_text(text_work,json)

        url = URL("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A13&stype=bycount&count=1&mode=yes")
        json = getCon(url,cookie)
        if (json["value"].toString()=="\"1\"") {
            add_text(URLDecoder.decode("${json["event_time"]} Программа выполняется\n", "UTF-8"),json)
        }
        url = URL("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:16&mode=yes""")
        json = getCon(url,cookie)
        if (json["value"].toString()=="\"2\"")
            add_text(("\nРабота по производственной программе\n"),json)
        url = URL("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:2&mode=yes""")
        json = getCon(url,cookie)
        if (!json["value"].isNull)
            add_text("Станок включен\n",json)
        else
            add_text("Станок выключен\n",json)
        url = URL("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_EMERGENCY_STOP&mode=yes""")
        json = getCon(url,cookie)
        if (!json["value"].isNull)
            add_text("Аварийная остановка\n",json)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        result_info2 = findViewById(R.id.result_info2)
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