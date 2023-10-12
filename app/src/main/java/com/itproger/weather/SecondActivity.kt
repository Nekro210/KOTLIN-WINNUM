package com.itproger.weather

//import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.jetbrains.anko.doAsync
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs


class SecondActivity : AppCompatActivity() {
    private var result_info2: TextView? = null
    private var text = ""
    private var pieChart: PieChart? = null
    private val entries: ArrayList<PieEntry> = ArrayList()
    private val tag_colors: ArrayList<Int> = ArrayList()
    private val password = "wnadmin"
    private val user = "wnadmin"
    //private val uuid= Array<String>
    private var ip_port = ""
    private var tags = ""
    //"http://10.0.2.2:82"
    //"http://192.168.0.101:80"
    private val objectMapper = ObjectMapper()
    private var json:JsonNode= objectMapper.createObjectNode()
    private var pi = -1

    //private val qrCodeValue = 1

    @SuppressLint("SetTextI18n")
    fun getCon(url: String){
            try{

                val url2 = URL("$url&usr=$user&pwd=$password")
                val con = url2.openConnection() as HttpURLConnection
                //con.setRequestProperty("cookie","JSESSIONID=P7gU7XsgvzM95dWk3Ztptk2BUJ-lBagIHcHMvbFq.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; Studio-346eca46=2707507b-8e30-41e9-9eed-0596a626828e")
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
                json = objectMapper.readValue(xml, JsonNode::class.java)
                json = json["item"]

        } catch (e: Exception) {
                text += " Error "
                Log.d("ERRORTYPE", e.message.toString())
            }
    }

    fun add_text(stroka:String,json:JsonNode){
        if (!json.isNull)
            text += stroka.replace("\"","")
    }

    fun get_product_info(qrCodeValue:String) {
        try {
            val folder_id = json["FolderInfo__idA6"].toString().replace("\"", "").toInt() - 1
            var url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.folder.WNFolder:$folder_id&mode=yes")
            getCon(url)
            add_text(URLDecoder.decode("${json["folderName"]}, ", "UTF-8"), json)
            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)
            add_text(
                URLDecoder.decode(
                    "${json["FolderInfo__folderName"]}\n\n${json["TemplateInfo__partNumber"]}, ${json["SerialNumber"]}\n${json["name"]}\n",
                    "UTF-8"
                ), json
            )
        }
        catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    fun get_operator_data(operator_code_signal:String,qrCodeValue:String){
        try {
            var url = ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_OPERATOR&mode=yes""")
            getCon(url)
            val operator_tag = URLDecoder.decode("${json["id"]}","UTF-8").replace("\"","").replace(", ","")
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&tid=$operator_tag&mode=yes""")
            getCon(url)
            add_text(URLDecoder.decode("Оператор: ${json["value"]}, ", "UTF-8"), json)

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$operator_code_signal&stype=bycount&count=1&mode=yes")
            getCon(url)
            add_text(
                URLDecoder.decode("${json["value"]}\n\n", "UTF-8"),
                json
            )
        }
        catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    fun get_program_name(qrCodeValue:String) {
        try {
            var url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_PROGRAM_NAME&mode=yes""")
            getCon(url)
            val program_name_tag =
                URLDecoder.decode("${json["id"]}", "UTF-8").replace("\"", "").replace(", ", "")
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&tid=$program_name_tag&mode=yes""")
            getCon(url)
            add_text(URLDecoder.decode("УП\n\n${json["value"]}, ", "UTF-8"), json)
        } catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    fun get_DSE_data(DSE_name:String,DSE_id:String,qrCodeValue:String): String {
        var DSE_id_time = ""
        try {
            var url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$DSE_name&stype=bycount&count=1&mode=yes")
            getCon(url)
            add_text(URLDecoder.decode("${json["value"]} ", "UTF-8"), json)

            url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$DSE_id&stype=bycount&count=1000&mode=yes")
            getCon(url)
            //add_text(URLDecoder.decode("${json["value"]},\n", "UTF-8"), json)
            var i = 1
            while (i < json.size()) {
                if (json[i-1]["value"].toString() != json[i]["value"].toString()){
                    add_text(URLDecoder.decode("${json[i-1]["value"]},\n", "UTF-8"), json)
                    DSE_id_time = URLDecoder.decode("${json[i-1]["event_time"]},\n", "UTF-8").replace("\"","")
                    break
                }
                i++
            }

        } catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
        return DSE_id_time
    }

    fun get_operation_data(operation_number_signal:String,DSE_time:String,DSE_id_time:String,currentDateTime:String,dateFormat:SimpleDateFormat,qrCodeValue:String){
        try {
            var url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$operation_number_signal&stype=bycount&count=1&mode=yes")
            getCon(url)
            add_text(URLDecoder.decode("операция ${json["value"]}, ", "UTF-8"), json)

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$DSE_time&stype=bycount&count=1&mode=yes")
            getCon(url)
            val df = DecimalFormat("0.00")
            val ready_time = df.format(
                100 * abs(
                    dateFormat.parse(
                        currentDateTime.replace(
                            "%20",
                            " "
                        )
                    ).time - dateFormat.parse(DSE_id_time).time
                ).toFloat() / (1000 * 60 * 60 * json["value"].toString().replace("\"", "")
                    .toFloat())
            )

            add_text("готовность ${ready_time}%\n\n", json)
        }catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    fun get_ready_data(done_operations_signal:String,not_done_operations_signal:String,qrCodeValue:String) {
        try {
            var url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$done_operations_signal&stype=bycount&count=1&mode=yes")
            getCon(url)
            add_text(URLDecoder.decode("Готово ${json["value"]} из ", "UTF-8"), json)

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$not_done_operations_signal&stype=bycount&count=1&mode=yes")
            getCon(url)
            add_text(URLDecoder.decode("${json["value"]}\n\n", "UTF-8"), json)
        }catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun get_shift_data(currentDateTime:String, dateFormat:SimpleDateFormat, qrCodeValue:String, arrayOfTags: Array<String>) {
        try {
            var url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShift&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)
            val shift_id = URLDecoder.decode(json[0]["id"].toString(), "UTF-8").replace("\"", "")

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShiftStartDate&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)
            val shift_start =
                URLDecoder.decode(json["datetime"].toString(), "UTF-8").replace("\"", "")
                    .replace(" ", "%20")

            url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationTagHelper&men=getLoadingCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&from=$shift_start&till=$currentDateTime&rounding=2&adjust=true&mode=yes")
            getCon(url)
            //http://localhost:82/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationTagHelper&men=getLoadingCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:1&from=2023-10-01%2008:00:00&till=2023-10-01%2015:51:00&rounding=2&adjust=true&mode=yes
            var i = 0
            //val work_time = arrayOf(0.00F)
            val no_work_time = arrayOf(0.00F)
            val off_time = arrayOf(0.00F)
            val tags_ids: ArrayList<String> = ArrayList()
            while (i < json.size()) {
                if (URLDecoder.decode(json[i]["shiftOid"].toString(), "UTF-8") == "\"$shift_id\"") {
                    Log.d(URLDecoder.decode(("${json[i]["tagName"]}"), "UTF-8"),
                        URLDecoder.decode("${json[i]["hours"]}", "UTF-8").toString()
                            .replace("\"", "")
                    )
                    if (URLDecoder.decode("${json[i]["percent"]}", "UTF-8").toString()
                            .replace("\"", "").toFloat() > 0.0F
                    ) {
                        if ((URLDecoder.decode(
                                json[i]["tagId"].toString(),
                                "UTF-8"
                            ).replace("\"", "") != "NC_OFF")
                        ) {
                            off_time[0] -= json[i]["hours"].toString().replace("\"", "")
                                .toFloat()

                            if (URLDecoder.decode(
                                    json[i]["tagId"].toString(),
                                    "UTF-8"
                                ).replace("\"", "") !in arrayOfTags
                            ) {
                                tags_ids.add(
                                    URLDecoder.decode(
                                        json[i]["tagId"].toString(),
                                        "UTF-8"
                                    ).replace("\"", "")
                                )
                                entries.add(
                                    PieEntry(
                                        URLDecoder.decode("${json[i]["hours"]}", "UTF-8")
                                            .toString()
                                            .replace("\"", "").toFloat(),
                                        URLDecoder.decode(("${json[i]["tagName"]}"), "UTF-8")
                                    )
                                )
                                //add_text(no_work_time[0].toString(), json)

                                //text_work += URLDecoder.decode(("${json[i]["tagName"]}: ${json[i]["hours"]} часов (${json[i]["percent"]}"),"UTF-8")
                            } else {
                                    no_work_time[0] += json[i]["hours"].toString().replace("\"", "")
                                        .toFloat()


                            }
                        }
                    }
                }
                i++
            }
            //text_work +=" %)\n"
            off_time[0] += ((
                dateFormat.parse(
                    currentDateTime.replace(
                        "%20",
                        " "
                    )
                ).time - dateFormat.parse(shift_start.replace("%20", " ")).time
            )).toFloat() / (1000 * 60 * 60)
            if (off_time[0]<=0F)
                off_time[0]= 0F
            else {

                if ("NC_OFF" in arrayOfTags)
                    no_work_time[0] += off_time[0]
                else {
                        entries.add(PieEntry(off_time[0], "Станок выключен"))
                        tags_ids.add("NC_OFF")
                }
            }
            add_text("Показатели cмены", json)
            if (no_work_time[0]>0.0F) {
                entries.add(PieEntry(no_work_time[0], "Время простоя"))

                if ("NC_OFF" in tags_ids)
                    tags_ids.add("NC_ON")
                else
                    tags_ids.add("NC_OFF")
            }
            i=0
            while (i < tags_ids.size) {
                url =("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=${tags_ids[i]}&mode=yes""")
                getCon(url)
                tag_colors.add(Color.parseColor(
                    URLDecoder.decode("${json["tagColor"]}", "UTF-8").toString()
                        .replace("\"", "")))
                i++
            }
        }catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }

    fun get_work_start_data(operator_signal:String,qrCodeValue:String){
        try{
        val url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=$qrCodeValue&signal=$operator_signal&stype=bycount&count=1000&mode=yes")
        getCon(url)
        var i = 1
        while (i < json.size()) {
            if (json[i-1]["value"].toString() != json[i]["value"].toString()){
                add_text(URLDecoder.decode("Начало работы: ${json[i-1]["event_time"]}\n\n","UTF-8").substring(0,35), json)
                add_text("\n",json)
                break
            }
            i++
        }
    }catch (e: Exception) {
        text += " Error "
        Log.d("ERRORTYPE", e.message.toString())
    }
    }

    fun getpi(qrCodeValue: String){
        var url = "fdfd"
        fun getpi2() {
            try {
                pi += 1
                url =
                    "$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:$pi&mode=yes"
                getCon(url)
                if (json["ProductUUID"].toString().replace("\"", "") != qrCodeValue) {
                    getpi2()
                }
            } catch (e: Exception) {
                text = ""
                getpi2()
            }
        }
        getpi2()
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {
        try {
            val intent = getIntent()
            ip_port = "http://${intent.getStringExtra("ip_port").toString()}"
            tags = intent.getStringExtra("tags").toString()
            val qrCodeValue = "91528AAD-2D7C-43BE-B765-192E89BF3C77"
                ///intent.getStringExtra("qrCodeValue").toString()
                //"91528AAD-2D7C-43BE-B765-192E89BF3C77"
            val zoneId = ZoneOffset.UTC
            val currentDate = LocalDateTime.now(zoneId)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            //val month = currentDate.minusDays(30).toString().replace("T","%20")
            //val week = currentDate.minusDays(7).toString().replace("T","%20")
            val arrayOfTags = tags.split(" ").toTypedArray()
            val currentDateTime =  currentDate.toString().replace("T","%20").substring(0,25)
            getpi(qrCodeValue)
            var url =("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)
            get_product_info(qrCodeValue)
            val template = (URLDecoder.decode("${json["TemplateInfo__idA12"]}, ","UTF-8")).replace("\"","").replace(", ","")

            url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNProductHelper&men=getSignalsByProductTemplate&oid=winnum.org.modeling.WNProductTemplate:$template&mode=yes")
            getCon(url)

            var operator_code_signal = ""
            var operation_number_signal = ""
            var operator_signal = ""
            var done_operations_signal = ""
            var not_done_operations_signal = ""
            var DSE_id = ""
            var DSE_name = ""
            var DSE_time = ""
            var i = 0
            while (i < json.size()) {
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Код оператора")
                    operator_code_signal = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Номер операции")
                    operation_number_signal = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Оператор (ФИО)")
                    operator_signal = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
               // if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Количество сданных деталей")
                //    done_operations_signal = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Количество полученных деталей")
                    not_done_operations_signal = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "ДСЕ")
                    DSE_id = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                if (URLDecoder.decode("${json[i]["signalName"]}, ","UTF-8").replace("\"","").replace(", ","") == "Наименование ДСЕ")
                    DSE_name = URLDecoder.decode("${json[i]["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")
                i++
            }
            url = "$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNModelingHelper&men=getSignalByParam&pid=winnum.org.product.WNProduct:$pi&paramName=code_WNWPadNoResizeSignal&paramValue=current_part_rate&signalType=0&mode=yes"
            getCon(url)

            DSE_time = URLDecoder.decode("${json["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")

            url = ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_PART_COUNTER&mode=yes""")
            getCon(url)
            val done_operations_signal_id = URLDecoder.decode("${json["id"]}, ","UTF-8").replace("\"","").replace(", ","")
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&tid=$done_operations_signal_id&mode=yes""")
            getCon(url)
            done_operations_signal = URLDecoder.decode("${json["value"]}, ","UTF-8").replace("\"","").replace(", ","")

            get_operator_data(operator_code_signal,qrCodeValue)

            get_program_name(qrCodeValue)

            val DSE_id_time = get_DSE_data(DSE_name,DSE_id,qrCodeValue)

            get_operation_data(operation_number_signal, DSE_time, DSE_id_time, currentDateTime, dateFormat,qrCodeValue)

            get_ready_data(done_operations_signal, not_done_operations_signal,qrCodeValue)

            get_work_start_data(operator_signal,qrCodeValue)

            get_shift_data(currentDateTime, dateFormat,qrCodeValue,arrayOfTags)


            //add_text(URLDecoder.decode("Начало работы: ${json["event_time"]}","UTF-8"), json) // изменить!!!!!!!!!
//            url =
//                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue - 1]}&signal=A13&stype=bycount&count=1&mode=yes")
//            json = getCon(url)
//            if (json["value"].toString() == "\"1\"") {
//                add_text(
//                    URLDecoder.decode(
//                        "${json["event_time"]} Программа выполнялась\n",
//                        "UTF-8"
//                    ), json
//                )
//            }
//            url =
//                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:16&mode=yes""")
//            json = getCon(url)
//            if (json["value"].toString() == "\"2\"")
//                add_text(("\nРабота по производственной программе\n"), json)
//            url =
//                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:2&mode=yes""")
//            json = getCon(url)
//            if (json["value"].toString() != "\"\"")
//                add_text("Станок включен\n", json)
//            else
//                add_text("Станок выключен\n", json)
//            url =
//                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:4&mode=yes""")
//            json = getCon(url)
//            if (json["value"].toString() != "\"\"")
//                add_text("Аварийная остановка\n", json)
        }
        catch (e: Exception) {
            text += " Error "
            Log.d("ERRORTYPE", e.message.toString())
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        result_info2 = findViewById(R.id.result_info2)
        result_info2?.text = "Загрузка..."
        doAsync {
            getData()
            runOnUiThread() {


        //  thread.start()
        //thread.join()

        result_info2?.text=text

        pieChart = findViewById(R.id.pieChart)

        // Создание датасета
        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        dataSet.valueTextSize = 11f
        dataSet.valueTextColor = Color.BLACK
        dataSet.setColors(tag_colors)
        // Создание PieData объекта
        val pieData = PieData(dataSet)

        // Настройка диаграммы
        pieChart?.data = pieData
        pieChart?.setEntryLabelColor(Color.WHITE)
        pieChart?.setEntryLabelTextSize(0f)
        pieChart?.setDrawCenterText(true)
        pieChart?.setUsePercentValues(true)
        val l: Legend? = pieChart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l?.orientation = Legend.LegendOrientation.VERTICAL
        l?.setDrawInside(false)
        l?.xEntrySpace = 0f
        l?.yEntrySpace = 0f
        l?.yOffset = 0f
        pieChart?.holeRadius = 30f
        pieChart?.description = null
        pieChart?.animateXY(1000, 1000)
        pieChart?.invalidate()
            }
        }
    }
    @SuppressLint("SuspiciousIndentation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(
                this,
                MainActivity2::class.java
            ) // Замените Page1Activity на ваш класс Activity
            startActivity(intent)
            finish()

    }


}

// диаграмма
// логин и пароль
// настройки
// Error и шрифты
// Красный цвет процентов
// Обновление страницы раз в минуту
// Шрифт калибри или робот
