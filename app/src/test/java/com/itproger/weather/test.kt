import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.io.BufferedReader
import java.io.InputStreamReader
import org.jetbrains.anko.doAsync

fun main() {
    var url = URL("http://127.0.0.1:82/Winnum/views/pages/auth/loginPage.jsp")
    var con = url.openConnection() as HttpURLConnection
    con.requestMethod = "POST"
    con.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    con.setRequestProperty("accept-language", "ru,en;q=0.9")
    con.setRequestProperty("cache-control", "max-age=0")
    con.setRequestProperty("content-type", "application/x-www-form-urlencoded")
    con.setRequestProperty("sec-fetch-dest", "document")
    con.setRequestProperty("sec-fetch-mode", "navigate")
    con.setRequestProperty("sec-fetch-site", "same-origin")
    con.setRequestProperty("sec-fetch-user", "?1")
    con.setRequestProperty("upgrade-insecure-requests", "1")
    con.setRequestProperty("cookie","JSESSIONID=OGTX3-T__OFkNQhIoghi4bo7AD3xPYFoAUXF_toi.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; _gid=GA1.1.1752033319.1683486154")
    con.setRequestProperty("Referer","http://localhost:82/Winnum/views/pages/auth/loginPage.jsp")
    con.setRequestProperty("Referrer-Policy","strict-origin-when-cross-origin")
    con.doOutput = true
    val postData = "uid=wnadmin&_uid_=wnadmin&pwd=wnadmin&_pwd_=wnadmin"
    val postDataBytes = postData.toByteArray(StandardCharsets.UTF_8)
    con.setRequestProperty("Content-Length", postDataBytes.size.toString())
    con.outputStream.write(postDataBytes)
    con.connect()
    url = URL("http://127.0.0.1:82/Winnum/views/navigation/home/list.jsp")
    con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    con.setRequestProperty("accept-language", "ru,en;q=0.9")
    con.setRequestProperty("cache-control", "max-age=0")
    con.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"YaBrowser\";v=\"23\"")
    con.setRequestProperty("sec-ch-ua-mobile", "?0")
    con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"")
    con.setRequestProperty("sec-fetch-dest", "document")
    con.setRequestProperty("sec-fetch-mode", "navigate")
    con.setRequestProperty("sec-fetch-site", "same-origin")
    con.setRequestProperty("sec-fetch-user", "?1")
    con.setRequestProperty("upgrade-insecure-requests", "1")
    con.setRequestProperty("cookie", "JSESSIONID=E6lZkynx1_FElvS-0O_1e4-JFB2y3-7HuDH2Ps8P.laptop-m9c07f23; _ga=GA1.1.1839282877.1678651639")
    con.setRequestProperty("Referer", "http://127.0.0.1:82/Winnum/views/pages/auth/loginPage.jsp")
    con.setRequestProperty("Referrer-Policy", "strict-origin-when-cross-origin")
    con.connect()
    val responseCode = con.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {

        val reader = BufferedReader(InputStreamReader(con.inputStream))
        var inputLine: String?
        val response = StringBuffer()
        while (reader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        reader.close()
        println(response.toString())
    } else {
        println("POST request failed with HTTP error code: $responseCode")
    }
}


fun getData(){
    try{
        //doAsync
        {
            var url = URL("http://127.0.0.1:82/Winnum/views/pages/auth/loginPage.jsp")
            var con = url.openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            con.setRequestProperty("accept-language", "ru,en;q=0.9")
            con.setRequestProperty("cache-control", "max-age=0")
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded")
            con.setRequestProperty("sec-fetch-dest", "document")
            con.setRequestProperty("sec-fetch-mode", "navigate")
            con.setRequestProperty("sec-fetch-site", "same-origin")
            con.setRequestProperty("sec-fetch-user", "?1")
            con.setRequestProperty("upgrade-insecure-requests", "1")
            con.setRequestProperty("cookie","JSESSIONID=OGTX3-T__OFkNQhIoghi4bo7AD3xPYFoAUXF_toi.laptop-m9c07f23; _ga=GA1.1.441910024.1683486154; _gid=GA1.1.1752033319.1683486154")
            con.setRequestProperty("Referer","http://localhost:82/Winnum/views/pages/auth/loginPage.jsp")
            con.setRequestProperty("Referrer-Policy","strict-origin-when-cross-origin")
            con.doOutput = true
            val postData = "uid=wnadmin&_uid_=wnadmin&pwd=wnadmin&_pwd_=wnadmin"
            val postDataBytes = postData.toByteArray(StandardCharsets.UTF_8)
            con.setRequestProperty("Content-Length", postDataBytes.size.toString())
            con.outputStream.write(postDataBytes)
            con.connect()
            url = URL("http://127.0.0.1:82/Winnum/views/navigation/home/list.jsp")
            con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
            con.setRequestProperty("accept-language", "ru,en;q=0.9")
            con.setRequestProperty("cache-control", "max-age=0")
            con.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"110\", \"Not A(Brand\";v=\"24\", \"YaBrowser\";v=\"23\"")
            con.setRequestProperty("sec-ch-ua-mobile", "?0")
            con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"")
            con.setRequestProperty("sec-fetch-dest", "document")
            con.setRequestProperty("sec-fetch-mode", "navigate")
            con.setRequestProperty("sec-fetch-site", "same-origin")
            con.setRequestProperty("sec-fetch-user", "?1")
            con.setRequestProperty("upgrade-insecure-requests", "1")
            con.setRequestProperty("cookie", "JSESSIONID=E6lZkynx1_FElvS-0O_1e4-JFB2y3-7HuDH2Ps8P.laptop-m9c07f23; _ga=GA1.1.1839282877.1678651639")
            con.setRequestProperty("Referer", "http://127.0.0.1:82/Winnum/views/pages/auth/loginPage.jsp")
            con.setRequestProperty("Referrer-Policy", "strict-origin-when-cross-origin")
            con.connect()
            val responseCode = con.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {

                val reader = BufferedReader(InputStreamReader(con.inputStream))
                var inputLine: String?
                val response = StringBuffer()
                while (reader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                reader.close()
                println(response.toString())
            } else {
                println("POST request failed with HTTP error code: $responseCode")
            }
        }
    }
    catch(e: Exception){
        println("ERROR")
    }
}