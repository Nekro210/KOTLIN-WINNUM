package com.itproger.weather

//import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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
    private val entries: ArrayList<PieEntry> = ArrayList() // данные диаграммы
    private val tag_colors: ArrayList<Int> = ArrayList() // массив цветов диаграммы
    //private val password = "wnadmin"
   // private val user = "wnadmin"
    //private /val uuid= Array<String>
    private var ip_port = ""
    private var tags = "" // теги простоя
    //"http://10.0.2.2:82"
    //"http://192.168.0.101:80"
    private val objectMapper = ObjectMapper()
    private var json:JsonNode= objectMapper.createObjectNode()
    private var pi = -1 // product id
    private var pi_check = false
    private var login = ""
    private var password = ""
    val intent33 = Intent(
        this,
        SecondActivity::class.java
    )


    @SuppressLint("SetTextI18n")
    fun getCon(url: String){
            try{
                val url2 = URL("$url&usr=$login&pwd=$password")
                val con = url2.openConnection() as HttpURLConnection
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
                json = objectMapper.readValue(" ", JsonNode::class.java)
                if (pi_check)
                    Toast.makeText(this, "Проверьте логин, пароль и ip", Toast.LENGTH_LONG).show()
                Log.d("ERRORTYPE", e.message.toString())
            }
    } // HTTP GET запрос

    fun add_text(stroka:String,json:JsonNode){
        if (!json.isNull)
            text += stroka.replace("\"","")
    } // добавляем текст

    fun get_product_info() {
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
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // информация о станке

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
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // данные по оператору

    fun get_program_name() {
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
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // данные о программе

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
            Log.d("ERRORTYPE", e.message.toString())
        }
        return DSE_id_time
    } // DSE_NAME и DSE_ID

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
            if (ready_time.toInt() <= 100)
                add_text("готовность ${ready_time}%\n\n", json)
            else
                add_text("готовность > 100% !!!\n\n", json)


        }catch (e: Exception) {
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // данные по операции и ее готовности

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
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // Готово n из m

    @SuppressLint("SuspiciousIndentation")
    fun get_shift_data(currentDateTime:String, dateFormat:SimpleDateFormat, qrCodeValue:String, arrayOfTags: Array<String>) {
        try {
            var url =
                ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShiftStartDate&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)
            val shift_start =
                URLDecoder.decode(json["datetime"].toString(), "UTF-8").replace("\"", "")
                    .replace(" ", "%20")

            var i = 0
            val no_work_time = arrayOf(0.00F) // время простоя
            val off_time = arrayOf(0.00F) // время NC_OFF
            val tags_ids: ArrayList<String> = ArrayList()
            var x = 1
            val NC_ON_time = arrayOf(0.00F)
            val NC_PROGRAM_RUN_time = arrayOf(0.00F)
            val complexTagsArray = arrayOf("NC_OFF","NC_ON","NC_PROGRAM_RUN")
            while (i < 5) {
                url = ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getSimpleTagCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&tid=winnum.org.tag.WNTag:$x&from=$shift_start&till=$currentDateTime&timeout=60000&mode=yes""")
                getCon(url)
                    Log.d(URLDecoder.decode(("${json["tagName"]}"), "UTF-8"),URLDecoder.decode("${json["hours"]}", "UTF-8").toString().replace("\"", ""))
                    if (URLDecoder.decode("${json["percent"]}", "UTF-8").toString()
                            .replace("\"", "").toFloat() > 0.0F
                    ) {
                        if ((URLDecoder.decode(
                                json["tagId"].toString(),
                                "UTF-8"
                            ).replace("\"", "") != "NC_OFF")
                        ) {
                            off_time[0] -= json["hours"].toString().replace("\"", "").toFloat()
                        }

                        if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") == "NC_ON")){
                            NC_ON_time[0]+= json["hours"].toString().replace("\"", "").toFloat()
                            i++
                            }
                        else if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") == "NC_EMERGENCY_STOP")) {
                            NC_ON_time[0] -= json["hours"].toString().replace("\"", "").toFloat()
                            i++
                        }
                        else if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") == "NC_PROGRAM_RUN")) {
                            NC_ON_time[0] -= json["hours"].toString().replace("\"", "").toFloat()
                            NC_PROGRAM_RUN_time[0] += json["hours"].toString().replace("\"", "").toFloat()
                            i++
                        }
                        else if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") == "NC_WIP")) {
                            NC_PROGRAM_RUN_time[0] -= json["hours"].toString().replace("\"", "")
                                .toFloat()
                            i++
                        }
                        if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") in arrayOfTags))
                            no_work_time[0] += json["hours"].toString().replace("\"", "").toFloat()
                        else{
                           if ((URLDecoder.decode(json["tagId"].toString(), "UTF-8").replace("\"", "") !in complexTagsArray)){
                               entries.add(PieEntry(URLDecoder.decode("${json[i]["hours"]}", "UTF-8").toString().replace("\"", "").toFloat(), URLDecoder.decode(("${json[i]["tagName"]}"), "UTF-8")))
                               tags_ids.add(URLDecoder.decode(("${json[i]["tagName"]}"), "UTF-8"))
                           }
                        }
                    }
                x++
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

            if ("NC_ON" in arrayOfTags)
                no_work_time[0] += NC_ON_time[0]
            else
            {
                entries.add(PieEntry(NC_ON_time[0], "Станок включен"))
                tags_ids.add("NC_ON")
            }

            if ("NC_PROGRAM_RUN" in arrayOfTags)
                no_work_time[0] += NC_PROGRAM_RUN_time[0]
            else
            {
                entries.add(PieEntry(NC_PROGRAM_RUN_time[0], "Программа выполняется"))
                tags_ids.add("NC_PROGRAM_RUN")
            }

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
            } // цвета диаграммы
        }catch (e: Exception) {
            Log.d("ERRORTYPE", e.message.toString())
        }
    } // получаем начало смены и рассчитываем диаграмму

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
        Log.d("ERRORTYPE", e.message.toString())
    }
    } // время начала работы оператора

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
        pi_check = true
    } // получаем порядковый номер станка

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getData() {
        try {
            val intent = intent
            val pref = getSharedPreferences("test", MODE_PRIVATE)
            password = pref.getString("password", getString(R.string.hint_user_field)).toString()
            login = pref.getString("login",getString(R.string.login)).toString()
            ip_port = "http://${pref.getString("ip", getString(R.string.ip_port))}"
            tags = pref.getString("tags",getString(R.string.tags)).toString() // теги простоя

            val qrCodeValue = intent.getStringExtra("qrCodeValue").toString()

            val zoneId = ZoneOffset.UTC
            val currentDate = LocalDateTime.now(zoneId)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

            val arrayOfTags = tags.split(" ").toTypedArray()
            val currentDateTime =  currentDate.toString().replace("T","%20").substring(0,25)

            getpi(qrCodeValue)
            var url =("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.product.WNProduct:$pi&mode=yes")
            getCon(url)

            get_product_info()
            val template = (URLDecoder.decode("${json["TemplateInfo__idA12"]}, ","UTF-8")).replace("\"","").replace(", ","")

            url = ("$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNProductHelper&men=getSignalsByProductTemplate&oid=winnum.org.modeling.WNProductTemplate:$template&mode=yes")
            getCon(url)

            // получаем номера сигналов
            var operator_code_signal = ""
            var operation_number_signal = ""
            var operator_signal = ""
            var done_operations_signal = ""
            var not_done_operations_signal = ""
            var DSE_id = ""
            var DSE_name = ""

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

            val DSE_time = URLDecoder.decode("${json["asciiPartNumber"]}, ","UTF-8").replace("\"","").replace(", ","")

            url = ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_PART_COUNTER&mode=yes""")
            getCon(url)
            val done_operations_signal_id = URLDecoder.decode("${json["id"]}, ","UTF-8").replace("\"","").replace(", ","")
            url =
                ("""$ip_port/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$pi&tid=$done_operations_signal_id&mode=yes""")
            getCon(url)
            done_operations_signal = URLDecoder.decode("${json["value"]}, ","UTF-8").replace("\"","").replace(", ","")

            get_operator_data(operator_code_signal,qrCodeValue)

            get_program_name()

            val DSE_id_time = get_DSE_data(DSE_name,DSE_id,qrCodeValue)

            get_operation_data(operation_number_signal, DSE_time, DSE_id_time, currentDateTime, dateFormat,qrCodeValue)

            get_ready_data(done_operations_signal, not_done_operations_signal,qrCodeValue)

            get_work_start_data(operator_signal,qrCodeValue)

            get_shift_data(currentDateTime, dateFormat,qrCodeValue,arrayOfTags)

        }
        catch (e: Exception) {
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
        //val handler: Handler = Handler()
        Handler(Looper.getMainLooper()).postDelayed(Runnable { // Создаем намерение (Intent) для запуска Activity

            //finish()
            startActivity(intent33)

            // Завершаем текущую Activity (если это необходимо)
            //finish()
        }, 30000) // 60,000 миллисекунд = 1 минута
        //result_info2?.text = ""

    }
    @SuppressLint("SuspiciousIndentation")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent4 = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent4)
            finish()

    }


}

// диаграмма +
// логин и пароль +
// настройки +
// Error и шрифты +
// Красный цвет процентов +
// Обновление страницы раз в минуту +
// Шрифт калибри или робот +
