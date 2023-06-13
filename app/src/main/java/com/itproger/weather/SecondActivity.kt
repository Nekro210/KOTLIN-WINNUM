package com.itproger.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder


class SecondActivity : AppCompatActivity() {
    private var result_info2: TextView? = null
    private var text = ""
    private var pieChart: PieChart? = null
    private val entries: ArrayList<PieEntry> = ArrayList()
    private val password = "wnadmin"
    private val user = "wnadmin"
    private val uuid= arrayOf("91528AAD-2D7C-43BE-B765-192E89BF3C77")
    private val ip_port = "http://10.0.2.2:82"

    @SuppressLint("SetTextI18n")
    fun getCon(url: String): JsonNode {
        val url2 = URL("$url&usr=$user&pwd=$password")
        val con = url2.openConnection() as HttpURLConnection
        con.setRequestProperty("usr", user)
        con.setRequestProperty("pwd", password)
        con.requestMethod = "GET"
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
        try {
            val qrCodeValue = 1
            //val qrCodeValue = intent.getStringExtra("qrCodeValue").toString().toInt()
            var url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.modeling.WNProductTemplate:$qrCodeValue&mode=yes")
            var json = getCon(url)
            add_text(
                URLDecoder.decode(
                    "Описание: ${json["Description"]}\nИнв. номер: ${json["master__partNumber"]}\nИмя: ${json["master__name"]}\nМодель: ${json["Model"]}\nСоздано: ${json["PersistInfo__createStamp"]}\nИнформация о ТО: ${json["RevisionInfo__state"]}\n\n",
                    "UTF-8"
                ), json
            )
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:19&mode=yes""")
            json = getCon(url)
            add_text(URLDecoder.decode("Имя программы: ${json["value"]}\n\n", "UTF-8"), json)

            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:32&mode=yes""")
            json = getCon(url)
            add_text(URLDecoder.decode("Текущий оператор ${json["value"]}\n", "UTF-8"), json)

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue - 1]}&signal=A66&stype=bycount&count=1&mode=yes")
            json = getCon(url)
            add_text(
                URLDecoder.decode("Последний код оператора: ${json["value"]}\n\n", "UTF-8"),
                json
            )

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShift&oid=winnum.org.product.WNProduct:$qrCodeValue&mode=yes")
            json = getCon(url)

            val shift_start = json[0]["PersistInfo__createStamp"].toString().replace("\"", "")
            val shift_id = json[0]["id"].toString().replace("\"", "")

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationTagHelper&men=getLoadingCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&from=$shift_start&timeout=60000&rounding=2&adjust=true&mode=yes")
            json = getCon(url)
            var i = 0
            val no_work_time = arrayOf(0.00F,0.00F)
            var text_work = "Показатели по текущей смене:\n"
            while (i < json.size()) {
                if (URLDecoder.decode(json[i]["shiftOid"].toString(), "UTF-8") == "\"$shift_id\"") {
                    text_work += URLDecoder.decode(
                        ("${json[i]["tagName"]}: ${json[i]["hours"]} часов (${json[i]["percent"]}"),
                        "UTF-8"
                    )
                    entries.add(
                        PieEntry(
                            URLDecoder.decode("${json[i]["hours"]}", "UTF-8").toString()
                                .replace("\"", "").toFloat(),
                            URLDecoder.decode(("${json[i]["tagName"]}"), "UTF-8")
                        )
                    )
                    if (URLDecoder.decode(json[i]["tagName"].toString(),"UTF-8") in arrayOf("\"Аварийная остановка\"","\"Станок выключен\"","\"Станок включен\"")) {
                        no_work_time[0] += json[i]["hours"].toString().replace("\"", "").toFloat()
                        no_work_time[1] += json[i]["percent"].toString().replace("\"", "").toFloat()
                    }
                    text_work += " %)\n"
                }
                i++
            }
            text_work += URLDecoder.decode(("Время простоя: ${no_work_time[0]} часов (${no_work_time[1]}"),"UTF-8")
            text_work +=" %)\n"
            entries.add(PieEntry(no_work_time[0], "Время простоя"))
            //add_text(text_work, json)

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue - 1]}&signal=A13&stype=bycount&count=1&mode=yes")
            json = getCon(url)
            if (json["value"].toString() == "\"1\"") {
                add_text(
                    URLDecoder.decode(
                        "${json["event_time"]} Программа выполнялась\n",
                        "UTF-8"
                    ), json
                )
            }
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:16&mode=yes""")
            json = getCon(url)
            if (json["value"].toString() == "\"2\"")
                add_text(("\nРабота по производственной программе\n"), json)
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:2&mode=yes""")
            json = getCon(url)
            if (!json["value"].isNull)
                add_text("Станок включен\n", json)
            else
                add_text("Станок выключен\n", json)
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:4&mode=yes""")
            json = getCon(url)
            if (json["value"].toString() != "\"\"")
                add_text("Аварийная остановка\n", json)
        }
        catch (e: Exception) {
            text += "Произошла ошибка, не удалось получить данные("
            Log.d("ERRORTYPE", e.message.toString())
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        result_info2 = findViewById(R.id.result_info2)
        result_info2?.text="Загрузка..."

        runOnUiThread {
                getData()
        }

        result_info2?.text=text

        pieChart = findViewById(R.id.pieChart)

        // Создание данных для диаграммы

        // Создание данных для диаграммы


        // Создание датасета

        // Создание датасета
        val dataSet = PieDataSet(entries, "Диаграмма")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        // Создание PieData объекта

        // Создание PieData объекта
        val pieData = PieData(dataSet)

        // Настройка диаграммы

        // Настройка диаграммы
        pieChart?.setData(pieData)
        pieChart?.setUsePercentValues(true)
        pieChart?.getDescription()?.isEnabled ?: false
        pieChart?.setCenterText("Показатели по текущей смене:")
        pieChart?.animateXY(1000, 1000)
        pieChart?.invalidate()
    }
}