@file:Suppress("UNCHECKED_CAST")

package com.seon_schedule

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun getSCD(date: String, ay: String, sem: String,grade: String, class_: String, apiKey: String): JSONObject{
    var url_ = "https://open.neis.go.kr/hub/hisTimetable?Type=json&KEY=" + apiKey
    url_ += "&ATPT_OFCDC_SC_CODE=B10&SD_SCHUL_CODE=7010738&GRADE=${grade}&ALL_TI_YMD=${date}&AY=${ay}&SEM=${sem}&CLRM_NM=${class_}"
    Log.i("API", url_)
    val url =
        URL(url_)
    val connection = url.openConnection() as HttpsURLConnection
    connection.instanceFollowRedirects = true
    connection.requestMethod = "GET"
    connection.connectTimeout = 10000
    connection.readTimeout = 10000

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = reader.use { it.readText() }
        reader.close()


        val result = JSONObject(response).getJSONArray("hisTimetable").getJSONObject(1)
            .getJSONArray("row")

        var al = JSONObject("{\"type\":\"suc\"}")

        for (i in 0 until result.length()){
            val item = result.getJSONObject(i)
            Log.i("ASDASD", item.toString())
            val perio = item.getString("PERIO")
            if (!al.has(perio)){
                al.put(perio, JSONArray())
            }
            var perios: JSONArray = al.get(perio) as JSONArray
            perios.put(item.getString("ITRT_CNTNT"))
            al.put(perio, perios)
        }
        val a = al.get("1") as JSONArray
        for (i in 0 until a.length()){
            Log.i("alal", a.getString(i))
        }
        return al
    }
    Log.i("API_NO", responseCode.toString())
    return JSONObject("{\"type\":\"fail\"}")
}