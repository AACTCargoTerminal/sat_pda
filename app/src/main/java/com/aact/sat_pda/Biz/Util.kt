package com.aact.sat_pda.Biz

import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.UUID
import kotlin.collections.set
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.Inet4Address
import java.net.NetworkInterface
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.collections.iterator
import kotlin.math.roundToInt
import kotlin.text.get

object Util {

    fun getOneColumn(array: List<DataRow>, colName: String): List<String> {

        var ret: List<String> = emptyList()

        try {
            val temp = array.mapNotNull { it.row[colName] }
            ret = temp
        } catch (e: Exception) {
            ret = emptyList()
        }

        return ret
    }
    fun getTableIndex(array:List<DataRow>,filterColumn:String,findItem:String) : Int{
        var ret = -1
        try{

            ret = array.indexOfFirst { it.row[filterColumn] == findItem }
        }catch (e: Exception){
            ret = -1
        }
        return ret
    }
    fun getInt(str:String) : Int{
        var ret = 0
        try{
            ret = str.toInt()
        }catch (e: Exception){
            ret = 0
        }
        return ret
    }

    fun getDouble(str: String): Double {
        var ret = 0.0

        try {

            ret = (str.toDouble() * 100).roundToInt() / 100.0

        } catch (e: Exception) {
            ret = 0.0
        }
        return ret
    }

    //YYYYMMDD -> YYYY-MM-DD
    fun changeDate(str: String): String {
        var ret = ""
        try {
            val builder = StringBuilder()
            var selection = str.length

            for (i in str.indices) {
                builder.append(str[i])
                if ((i == 3 || i == 5) && i != str.lastIndex) {
                    builder.append("-")
                    selection++
                }
            }
            ret = builder.toString()

        } catch (e: Exception) {
            ret = getNowDate()
        }

        return ret
    }

    fun getGUID(): String {
        var ret: String = "";
        try {
            ret = UUID.randomUUID().toString()
        } catch (e: Exception) {
            ret = "";
        }
        return ret
    }

    fun parseSoapTableList(xmlString: String): Result<DataTable> {
        try {
            val result = DataTable(linkedMapOf())
            var currentItem: DataRow? = null
            var currentTag: String? = null
            var currentIndex = -1
            var beforeTag = ""
            var inNewDataSet = false // ✅ NewDataSet 태그 진입 여부

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(xmlString.reader())

            val tablePattern = Regex("^_?(?:table|tabl)\\d*_?$", RegexOption.IGNORE_CASE)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name

                        // ✅ NewDataSet 태그 시작
                        if (tagName.lowercase() == "newdataset") {
                            inNewDataSet = true
                        }

                        if (inNewDataSet) {
                            if (tagName == "_TABLE0_" || tablePattern.matches(tagName.lowercase())) {
                                if (tagName == beforeTag) {
                                    currentItem = DataRow(linkedMapOf())
                                } else {
                                    beforeTag = tagName

                                    currentItem = DataRow(linkedMapOf())
                                    currentIndex += 1
                                    if (result.table.size <= currentIndex) {
                                        result.table[currentIndex] = mutableListOf()
                                    }
                                }
                            } else if (currentItem != null) {
                                currentTag = tagName
                                currentItem.row[currentTag] = ""
                            }
                        }
                    }

                    XmlPullParser.TEXT -> {
                        if (inNewDataSet && currentItem != null && currentTag != null) {
                            currentItem.row[currentTag] = parser.text
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        val tagName = parser.name

                        // ✅ NewDataSet 태그 종료
                        if (tagName.lowercase() == "newdataset") {
                            inNewDataSet = false
                        }

                        if (inNewDataSet &&
                            (tagName == "_TABLE0_" || tablePattern.matches(tagName.lowercase())) &&
                            currentItem != null && currentIndex >= 0
                        ) {
                            result.table[currentIndex]?.add(currentItem)
                            currentItem = null
                        }
                        currentTag = null
                    }
                }
                eventType = parser.next()
            }
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "ERROR")
        }
    }

    fun buildSoapXml(
        methodName: String,
        key1: String,
        reqData: Map<String, String>
    ): Result<String> {
        return try {
            val bodyContent = reqData.entries.joinToString("") { (key, value) ->
                "<$key>$value</$key>"
            }
            Result.Success(
                """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                       xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <$methodName xmlns="http://aact.sharp.co.kr/">
              <key1>$key1</key1>
              $bodyContent
            </$methodName>
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "ERROR")
        }

    }

    suspend fun sendAPI(
        reqData: Map<String, String>,
        methodName: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(Common.timeout, TimeUnit.SECONDS)
                    .readTimeout(Common.timeout, TimeUnit.SECONDS)
                    .writeTimeout(Common.timeout, TimeUnit.SECONDS)
                    .build()

                val mediaType = "text/xml; charset=utf-8".toMediaTypeOrNull()
                var xmlData: String = "";
                when (val result =
                    buildSoapXml(methodName, Common.serverKey, reqData)) {
                    is Result.Success -> {
                        xmlData = result.data
                    }

                    is Result.Error -> {
                        return@withContext Result.Error(result.message)
                    }
                }

                val request = Request.Builder()
                    .url("http://${Common.selectServer.value}/pda/pda1.asmx")
                    .addHeader("Content-Type", "text/xml; charset=utf-8")
                    .addHeader("User-Agent", "Mozilla/5.0 (PDA)")
                    .addHeader("SOAPAction", "http://aact.sharp.co.kr/${methodName}")
                    .post(xmlData.toRequestBody(mediaType))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    val result = parseSoapTableList(body)
                    when (result) {
                        is Result.Success -> {
                            return@withContext Result.Success(result.data)
                        }

                        is Result.Error -> {
                            return@withContext Result.Error(result.message)
                        }
                    }
                } else {
                    val errorBody = response.body?.string().orEmpty()
                    val errorMessage = "서버 오류: HTTP ${response.code}\n내용: $errorBody"
                    return@withContext Result.Error(errorMessage)
                }

            } catch (e: Exception) {
                return@withContext Result.Error(e.message ?: "Util.sendAPI")
            }
        }
    }

    //yyyyMMddHHmmss -> yyyy-MM-dd HH:mm:ss
    fun formatDate_14(date: String): Result<String> {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val dateTime = LocalDateTime.parse(date, inputFormatter)

            Result.Success(dateTime.format(outputFormatter))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Parsing ERR")
        }

    }

    // yyyy-MM-dd HH:mm:ss -> yyyyMMdd
    fun formatDate_15(date: String): String {
        var ret = ""
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

            val dateTime = LocalDateTime.parse(date, inputFormatter)

            ret = dateTime.format(outputFormatter)
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    // yyyy-MM-dd HH:mm:ss -> yyyy-MM-dd
    fun formatDate_16(date: String): String {
        var ret = ""
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            val dateTime = LocalDateTime.parse(date, inputFormatter)

            ret = dateTime.format(outputFormatter)
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    // yyyy-MM-dd HH:mm:ss -> yy-MM-dd HH:mm
    fun formatDate_17(date: String): String {
        var ret = ""
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")

            val dateTime = LocalDateTime.parse(date, inputFormatter)

            ret = dateTime.format(outputFormatter)
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    fun getStr(str: String?): String {
        return str?.replace(" ", "") ?: ""
    }

    fun getTableCell(
        index: Int,
        array: List<DataRow>?,
        key: String,
        defalutStr: String = ""
    ): String {
        var ret = defalutStr
        try {
            array?.let {
                val temp = it[index].row[key]
                if(temp!= null){
                    ret = temp
                }
            }

        } catch (e: Exception) {
            ret = defalutStr
        }
        return ret

    }

    //yyyy-MM-dd -> yyyyMMdd
    fun formatDate_8(date: String): String {
        var ret = ""
        try {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

            val dateTime = LocalDate.parse(date, inputFormatter)

            ret = dateTime.format(outputFormatter)
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    fun validDate(str: String): String {
        val raw = str.replace("-", "")
        val trimmed = if (raw.length >= 8) {
            raw.substring(0, 8)
        } else raw

        val builder = StringBuilder()
        var selection = trimmed.length

        for (i in trimmed.indices) {
            builder.append(trimmed[i])
            if ((i == 3 || i == 5) && i != trimmed.lastIndex) {
                builder.append("-")
                selection++
            }
        }
        val result = builder.toString()
        return result
    }

    fun getLocalIp(): Result<String> {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                val addrs = intf.inetAddresses
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return Result.Success(addr.hostAddress ?: "0.0.0.0")
                    }
                }
            }
            Result.Error("IP 조회에러")
        } catch (e: Exception) {
            Result.Error(e.message ?: "ERROR")
        }
    }

    fun validSelectTable(
        originMap: Map<String, String>,
        changeMap: Map<String, String>
    ): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for ((key, _) in originMap) {
            if (changeMap.containsKey(key)) {
                result[key] = changeMap[key] ?: ""
            }
        }

        return result
    }

    //YYYY-MM-DD
    fun getNowDate(): String {
        var ret = ""
        try {
            ret = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            ret = ""
        }
        return ret
    }

    //YYYYMMDD True False
    fun validYYYYMMDD(str:String): Boolean{
        try{
            if (str.length != 8 || !str.all { it.isDigit() }) return false
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            LocalDate.parse(str, formatter)
            return true
        }catch (e: Exception){
            return false
        }
    }

    //ISO8601 -> HH:mm:ss
    fun offSetParse(str:String):String{
        var ret = str
        try{
            val hhmmss = OffsetDateTime.parse(str)
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            ret = hhmmss
        }catch (e: Exception){
            ret = str
        }
        return ret
    }
}