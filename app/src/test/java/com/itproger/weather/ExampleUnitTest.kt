package com.itproger.weather

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val qrCodeValue = 1
    private val uuid= arrayOf("91528AAD-2D7C-43BE-B765-192E89BF3C77")
    private val cookie = "JSESSIONID=gV5a7VltRCxKQnuuIB7D1emX_IlSTeCkEMNJS0WT.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; Studio-346eca46=2707507b-8e30-41e9-9eed-0596a626828e"
    private val ip_port_pl = "http://127.0.0.1:82"
    private var time = SimpleDateFormat("yyyy-MM-dd%20hh:mm:ss").format(Date())
    var format = SimpleDateFormat(time)
    var date: Date = format.parse(time) as Date

    //private val ip_port_cl = "http://127.0.0.1:83"


    fun getCon(url: URL, cookie: String): JsonNode {
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
    @Test
    fun getData() {
       try {
           val currentDate = LocalDate.now()
           val previousDate = currentDate.minusDays(30)

           println("Текущая дата: $currentDate")
           println("Дата, отстоящая от текущей на 7 дней: $previousDate")
           var url =
               URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNFactory&men=getPersistable&oid=winnum.org.modeling.WNProductTemplate:$qrCodeValue&mode=yes")
           var json = getCon(url,cookie)
            println(URLDecoder.decode("Описание: ${json["Description"]}\n" +
                    "Инв. номер: ${json["master__partNumber"]}\n" +
                    "Имя: ${json["master__name"]}\n" +
                    "Модель: ${json["Model"]}\n" +
                    "Создано: ${json["PersistInfo__createStamp"]}\n" +
                    "Информация о ТО: ${json["RevisionInfo__state"]}","UTF-8"))
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A12&stype=bycount&count=1&mode=yes")
           //url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:3&mode=yes""")
           json = getCon(url,cookie)
           if (json["value"].toString() == "\"0\"") {
               println(URLDecoder.decode("${json["event_time"]} станок был во включенном состоянии","UTF-8"))
               }
           else if (json["value"].toString()=="\"1\""){
               println(URLDecoder.decode("${json["event_time"]} станок был в состоянии аварийной остановки","UTF-8"))
           }
           url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:32&mode=yes""")
           json = getCon(url,cookie)
           println(URLDecoder.decode("Оператор ${json["value"]}","UTF-8"))
//           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A52&stype=bycount&count=1&mode=yes")
//           json = getCon(url,cookie)
//           println(URLDecoder.decode("${json["event_time"]} Оператор отсутствует ${json["value"]}","UTF-8"))

           // Код оператора через тег сделать мб тоже спросить как данные поступают
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A66&stype=bycount&count=1&mode=yes")
           json = getCon(url,cookie)
           println(URLDecoder.decode("Код последнего оператора: ${json["value"]}","UTF-8"))
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getCurrentWorkShift&oid=winnum.org.product.WNProduct:$qrCodeValue&mode=yes")
           json = getCon(url,cookie)
           //println(json)
           val shift_start = URLDecoder.decode(json[0]["PersistInfo__createStamp"].toString(),"UTF-8").replace("\"","").replace(" ","%20")
           val shift_id = URLDecoder.decode(json[0]["id"].toString(),"UTF-8").replace("\"","")
           //var shift_id = "winnum.org.time.WNWorkShift:1"
           //url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getWorkShifts&oid=winnum.org.product.WNProduct:$qrCodeValue&mode=yes")
           //json = getCon(url,cookie)
           //println(URLDecoder.decode(json.toString(),"UTF-8"))
           //url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getWorkShiftTotalTime&oid=$shift_id&mode=yes")
           //json = getCon(url, cookie)
           //var work_min = json["duration_seconds"].toString().replace("\"","").toInt()/60
           //println(URLDecoder.decode("Время текущей смены: $work_min минут","UTF-8"))
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationTagHelper&men=getLoadingCalculation&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&from=$shift_start&timeout=60000&rounding=2&adjust=true&mode=yes")
           // Сделать за неделю месяц
           json = getCon(url, cookie)
           //println(json)
           var i =0
           var text_work = "Показатели по текущей смене: ${shift_id}\n"
           while (i<json.size()){
               if (URLDecoder.decode(json[i]["shiftOid"].toString(),"UTF-8")=="\"$shift_id\"") {
                   text_work += URLDecoder.decode(("${json[i]["tagName"]}: ${json[i]["hours"]} часов (${json[i]["percent"]}"),"UTF-8")
                   text_work +=" %)\n"
               }
               i++
           }
           println(text_work)
           // ВЫПОЛНЕННЫЕ ОПЕРАЦИИ за смену текущую
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCNCApplicationCompletedQtyHelper&men=getOperationSummary&appid=winnum.org.app.WNApplicationInstanc:1&pid=winnum.org.product.WNProduct:$qrCodeValue&from=$shift_start&mode=yes")
           // Сделать за неделю за месяц
           // ЧТО ТАКОЕ ПРОСТОЙ
           // Сделать симуляцию деталей типа
           //json = getCon(url,cookie)
           //println(URLDecoder.decode(json.toString(),"UTF-8"))
           //url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_PROGRAM_NAME&mode=yes""")
          // url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:19&mode=yes""")
           //url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getTag&oid=winnum.org.app.WNApplicationInstance:1&mode=yes")
           //json = getCon(url,cookie)
           //println(URLDecoder.decode(json.toString(),"UTF-8"))
           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNConnectorHelper&men=getSignal&uuid=${uuid[qrCodeValue-1]}&signal=A13&stype=bycount&count=1&mode=yes")
           json = getCon(url,cookie)
           //println(json["value"].toString())
           if (json["value"].toString()=="\"1\"") {
               println(URLDecoder.decode("${json["event_time"]} Программа выполняется", "UTF-8"))
           }
           else{
               println(URLDecoder.decode("Программа не выполняется", "UTF-8"))
           }
           url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:16&mode=yes""")
           json = getCon(url,cookie)
           if (json["value"].toString()=="\"2\"")
               println(URLDecoder.decode("Работа по производственной программе", "UTF-8"))
           url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getLastTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:2&mode=yes""")
           json = getCon(url,cookie)
           if (json["value"].toString() != "\"\"")
               println(URLDecoder.decode("Станок включен", "UTF-8"))
           else
               println(URLDecoder.decode("Станок выключен", "UTF-8"))
           url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationHelper&men=getApplicationTag&oid=winnum.org.app.WNApplicationInstance:1&tag=NC_EMERGENCY_STOP&mode=yes""")
//           json = getCon(url,cookie)
//           if (!json["value"].isNull)
//               println(json["value"].toString())


           url = URL("""$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNApplicationTagHelper&men=getTagCalculationValue&appid=winnum.org.app.WNApplicationInstance:1&pid=winnum.org.product.WNProduct:$qrCodeValue&tid=winnum.org.tag.WNTag:19&from=$previousDate%2000:00:00&timeout=60000&mode=yes""")
           json = getCon(url,cookie)
           //спросить как поступают сигналы типа постоянно или иногда например как с именем программы идея получить кучу сигналов и тупа почекать имена программ
           println(URLDecoder.decode("Имя программы: ${json}\n\n","UTF-8"))
           var names = arrayOf("","Нет программы","Нет программы")
           names[0] = URLDecoder.decode(json[0]["value"].toString(),"UTF-8")
           var k = 0
           i=1
           while (i<json.size() && k<2){
               if (json[i]["value"].toString() != names[k]){
                   k=k+1
                   names[k] = json[i]["value"].toString()
               }
               i++
           }
           println(names[0])
           println(names[1])
           println(names[2])
           //           url = URL("$ip_port_pl/Winnum/views/pages/app/agw.jsp?rpc=winnum.views.url.WNCalendarHelper&men=getWorkShiftBreakTime&oid=$shift_id&mode=yes")
//           json = getCon(url, cookie)
//           val no_work_min = json["duration_seconds"].toString().replace("\"","").toInt()/60
//           println(URLDecoder.decode("Время перерывов за текущую смену: $no_work_min минут","UTF-8"))


           // Как получать последних 3 операторов и их статистику под нагрузкой
       } catch (e: Exception) {
            println(e.message)
        }
    }
    //Toast.makeText(this, "Готово", Toast.LENGTH_LONG).show()
}
