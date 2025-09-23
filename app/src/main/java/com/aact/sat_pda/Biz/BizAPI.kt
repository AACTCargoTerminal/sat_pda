package com.aact.sat_pda.Biz

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import kotlin.collections.get
import kotlin.text.get

class BizAPI {

    var fltPrefix: String = ""
    var fltPrefixDate: String = ""
    var fltPrefixList: List<DataRow>? = null

    suspend fun getAgencyInfo(str: String): DataRow? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getPCM_CUSTOMER_P010_001(str, "", "", "", "", "Y", "", "")

        when (ret) {
            is Result.Success<DataTable> -> {
                val data = ret.data.table

                data[0]?.let {
                    if (it.isNotEmpty()) {
                        if (it[0].row["COL1"] == "OK") {
                            data[1]?.let { res ->
                                return res[0]
                            } ?: run {
                                return DataRow(linkedMapOf())
                            }
                        } else {
                            Common.sendError((it[0].row["COL3"] ?: "공통코드 에러"))
                        }
                    }
                } ?: run {
                    Common.sendError("서버 에러")
                }
                return null
            }

            is Result.Error -> {
                Common.sendError(ret.message)
                return null
            }
        }
    }

    suspend fun getCommonCode(
        classCode: String,
        searchKey: String
    ): List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getCommonCode(classCode, searchKey)

        when (ret) {
            is Result.Success<DataTable> -> {

                val data = ret.data.table

                data[0]?.let {
                    if (it.isNotEmpty()) {
                        if (it[0].row["COL1"] == "OK") {
                            return ret.data.table[1] ?: emptyList()
                        } else {
                            Common.sendError((it[0].row["COL3"] ?: "공통코드 에러"))
                        }
                    }
                } ?: run {
                    Common.sendError("서버 에러")
                }

                return null
            }

            is Result.Error -> {
                Common.sendError(ret.message)
                return null
            }
        }

    }

    suspend fun getOriginDest(fltSid: String): DataRow? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getPWM_SCHEDULE_M010_001(fltSid)

        when (ret) {
            is Result.Success<DataTable> -> {

                val data = ret.data.table

                data[0]?.let { status ->
                    if (status[0].row["COL1"] == "OK") {
                        data[1]?.let { res ->
                            return res[0]
                        }
                    } else {
                        Common.sendError((status[0].row["COL3"] ?: "편번 조회 에러"))
                    }
                }
            }

            is Result.Error -> {
                Common.sendError(ret.message)
            }
        }

        return null

    }

    suspend fun getPWMFlightNoWithPreFix(date1: String, prefix: String): List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        if (prefix.trim().length == 0 || prefix.trim().length == 3) {

        } else {
            Common.sendError("MAWB를 확인해주세요")
            return null
        }

        if (date1 == fltPrefixDate && prefix == fltPrefix && fltPrefixList != null) {
            return fltPrefixList
        }

        val ret = api.getPWM_SCHEDULE_L020_002("O", date1, prefix, "Y")

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {


                val data = ret.data.table
                data[0]?.let { status ->
                    if (Util.getTableCell(0, status, "COL1") == "OK") {

                        data[1]?.let { res ->
                            val filterData: List<DataRow> = res.mapNotNull { rowObj ->
                                val terminalCode = rowObj.row["TERMINAL_CODE"]
                                when {
                                    terminalCode == Common.terminalCode.value -> rowObj

                                    terminalCode == null -> {
                                        val mutableItem = rowObj.row.toMutableMap()
                                        mutableItem["TERMINAL_CODE"] = "미정"
                                        DataRow(LinkedHashMap(mutableItem))
                                    }

                                    else -> null // 제외
                                }
                            }

                            fltPrefix = prefix
                            fltPrefixDate = date1
                            fltPrefixList = filterData
                            return filterData
                        } ?: run {
                            return emptyList()
                        }


                    } else {
                        Common.sendError(Util.getTableCell(0, status, "COL2"))
                    }

                }
            }
        }

        fltPrefixDate = ""
        fltPrefix = ""
        fltPrefixList = null

        return null
    }

    suspend fun getAgencyCode(
        flag: String,
        searchKey: String
    ): List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        var customerFlag = ""
        var vendorFlag = ""
        var carrierFlag = ""
        var agencyFlag = ""
        var agencyOnlyFlag = ""
        var customsFlag = ""
        var iataFlag = ""

        if (flag == "AGENCY") {
            agencyOnlyFlag = "Y"
        }
        if (flag.contains("CUSTOMER")) {
            customerFlag = "Y"
        }
        if (flag.contains("VENDOR")) {
            vendorFlag = "Y"
        }
        if (flag.contains("CARRIER")) {
            carrierFlag = "Y"
        }
        if (flag.contains("AGENCY")) {
            agencyFlag = "Y"
        }
        if (flag.contains("CUSTOMS")) {
            customsFlag = "Y"
        }

        val ret: Result<DataTable>

        if (agencyOnlyFlag == "Y") {
            ret = api.getPCM_CUSTOMER_P020_001(searchKey)
        } else {
            ret = api.getPCM_CUSTOMER_P010_001(
                searchKey,
                "",
                customerFlag,
                vendorFlag,
                carrierFlag,
                agencyFlag,
                customsFlag,
                iataFlag
            )
        }

        when (ret) {
            is Result.Success<DataTable> -> {
                val data = ret.data.table

                data[0]?.let { status ->

                    if (status[0].row["COL1"] == "OK") {

                        data[1]?.let { res ->

                            return res

                        } ?: run {
                            return emptyList()
                        }

                    } else {

                        Common.sendError((status[0].row["COL3"] ?: "대리점 조회 에러"))
                    }

                } ?: run {
                    Common.sendError("서버 에러")
                }
            }

            is Result.Error -> {
                Common.sendError(ret.message)
            }
        }
        return null
    }

    suspend fun setAuth(): Boolean {
        val api = MainAPI_Impl()

        Route.setDefault()

        val ret = api.getAuthority()
        when (ret) {
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                data[0]?.let { status ->
                    if (Util.getTableCell(0, status, "COL1") == "OK") {
                        val tmp = mutableListOf<Route>()
                        data[1]?.forEach { row ->
                            val findObj =
                                Common.menuList.find { it.route.uppercase() == row.row["ROUTE"] }
                            if (findObj == null) {
                                return@forEach
                            }
                            val def = "N"
                            findObj.name = row.row["NAME"] ?: ""
                            findObj.group = row.row["GRP"] ?: ""
                            findObj.execute = row.row["EXECUTE_FLAG"] ?: def
                            findObj.create = row.row["CREATE_FLAG"] ?: def
                            findObj.update = row.row["UPDATE_FLAG"] ?: def
                            findObj.delete = row.row["DELETE_FLAG"] ?: def
                            findObj.read = row.row["READ_FLAG"] ?: def
                            findObj.print = row.row["PRINT_FLAG"] ?: def
                            findObj.excel = row.row["EXCEL_FLAG"] ?: def
                            tmp.add(findObj)
                        }
                        if(tmp.isNotEmpty()){
                            Common.menuList = tmp
                        }
                        return true
                    } else {
                        Common.sendError(Util.getTableCell(0, status, "COL3", "권한 조회 에러"))
                    }
                }
            }

            is Result.Error -> Common.sendError(ret.message)
        }
        return true

    }

    suspend fun getFltight(date1: String): List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getPWMFlightNo(date1 = date1)

        when (ret) {
            is Result.Success<DataTable> -> {

                val data = ret.data.table

                data[0]?.let { status ->
                    if (status[0].row["COL1"] == "OK") {
                        data[1]?.let { res ->
                            val filterData: List<DataRow> = res.mapNotNull { rowObj ->
                                val terminalCode = rowObj.row["TERMINAL_CODE"]
                                when {
                                    terminalCode == Common.terminalCode.value -> rowObj

                                    terminalCode == null -> {
                                        val mutableItem = rowObj.row.toMutableMap()
                                        mutableItem["TERMINAL_CODE"] = "미정"
                                        DataRow(LinkedHashMap(mutableItem))
                                    }

                                    Common.terminalCode.value == "ALL" -> rowObj

                                    else -> null // 제외
                                }
                            }

                            return filterData
                        } ?: run {
                            return emptyList()
                        }
                    } else {
                        Common.sendError((status[0].row["COL3"] ?: "편번 조회 에러"))
                    }
                }
            }

            is Result.Error -> Common.sendError(ret.message)
        }

        return null

    }

    suspend fun getCurDateTime(): String {

        val api: MainAPI = MainAPI_Impl()


        val ret = api.getCurDateTime()

        when (ret) {
            is Result.Error -> {
                Common.sendError(ret.message)
                return ""
            }

            is Result.Success<DataTable> -> {
                var res = ""
                ret.data.table[1]?.let {
                    res = it[0].row["COL1"] ?: ""
                }
                return res
            }
        }

    }

    suspend fun getPositionListEXP(): List<DataRow>? {

        val api: MainAPI = MainAPI_Impl()


        val ret = api.getCommonCodeWHLOC("WHLOC", "EXP", "Y")

        when (ret) {
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                data[0]?.let { status ->
                    if (Util.getTableCell(0, status, "COL1") == "OK") {
                        data[1]?.let {
                            return it
                        }
                        return emptyList()
                    } else {
                        Common.sendError(Util.getTableCell(0, status, "COL2"))
                    }
                }
            }

            is Result.Error -> Common.sendError(ret.message)
        }

        return null
    }

    suspend fun getCommonULCTR(): List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()
        val ret = api.getCommonCode("ULCTR", "")
        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    val retData = data[1]?.filter { row ->
                        Util.getStr(row.row["VALUE6_CHAR"]) == Common.terminalCode.value || Util.getStr(
                            row.row["VALUE6_CHAR"]
                        ) == ""
                    }
                    return retData
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }

    suspend fun getCommonCRRCDULD() : List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getCommonCodeWHLOC("CRRCD","ULD","Y")

        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    val retData = data[1]?.filter { row ->
                        Util.getStr(row.row["VALUE6_CHAR"]) == Common.terminalCode.value || Util.getStr(
                            row.row["VALUE6_CHAR"]
                        ) == ""
                    }
                    return retData
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }

    suspend fun getCommonULDTP() : List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getCommonCode("ULDTP","")

        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    val retData = data[1]?.filter { row ->
                        Util.getStr(row.row["VALUE6_CHAR"]) == Common.terminalCode.value || Util.getStr(
                            row.row["VALUE6_CHAR"]
                        ) == ""
                    }
                    return retData
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }

    suspend fun getCommonSPCGO() : List<DataRow>? {
        val api: MainAPI = MainAPI_Impl()

        val ret = api.getCommonCode("SPCGO","")

        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    data[1]?.let { table->
                        return table
                    }
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }
}