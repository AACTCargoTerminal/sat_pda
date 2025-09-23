package com.aact.sat_pda.screen.uld_scale

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_SCALE_Model : ViewModel() {

    val biz = BizAPI()
    val api = MainAPI_Impl()

    var operationSid = ""
    var isEditing = false
    var fltSelect = ""

    val uldNo = MutableLiveData<String>("")
    val contourCode = MutableLiveData<String>("")
    val contourNo = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")
    val scaleNoSelect = MutableLiveData<String>("1:10FT")
    val weight = MutableLiveData<String>("")
    val dollyWeight = MutableLiveData<String>("")
    val scaleUser = MutableLiveData<String>("")


    val scaleNoList = listOf<String>("1:10FT", "2:20FT")

    var fltList: List<DataRow> = emptyList()

    var contourList: List<DataRow> = emptyList()
    var typeList: List<DataRow> = emptyList()
    val scaleList = arrayOf("", "")
    var uldSelect = mapOf(
        "ULD_NO" to "",
        "FLIGHT_NO" to "",
        "DEPARTURE_TIME" to "",
        "CONTOUR_CODE" to "",
        "DESTINATION_CODE" to "",
        "SCALED_WEIGHT" to "",
        "TARE_WEIGHT" to ""
    )

    val uldModal = SubContent.GetTable(
        headerList = Header().uldScaleHeader,
        bodylist = emptyList(),
        selectValue = uldSelect,
        onClick = {
            uldSelect = Util.validSelectTable(uldSelect, it)
            uldNo.value = uldSelect["ULD_NO"]
            fltDate.value =
                if (!uldSelect["DEPARTURE_TIME"].isNullOrEmpty()) uldSelect["DEPARTURE_TIME"]?.substring(
                    0,
                    10
                ) else Util.formatDate_15(Common.dateTime.value)
            weight.value = uldSelect["SCALED_WEIGHT"]
            dollyWeight.value = uldSelect["TARE_WEIGHT"]
            fltNo.value = uldSelect["FLIGHT_NO"]
            fltDest.value = uldSelect["DESTINATION_CODE"]
            contourCode.value = uldSelect["CONTOUR_CODE"]
        },

        endClick = {}
    )

    val countourTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            contourCode.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )

    val fltTable = SubContent.GetTable(
        headerList = Header().fltHeader,
        bodylist = emptyList(),
        selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
        onClick = { value ->
            fltSelect = value["SCHEDULE_SID"] ?: ""
            fltNo.value = value["FLIGHT_NO"] ?: ""
            if (fltSelect.isNotEmpty()) {
                viewModelScope.launch {
                    Common.loadingOn(coroutineContext[Job])

                    setOriginDest(true)

                    Common.loadingOff()
                }
            }
        },
        endClick = {})

    val destTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            fltDest.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )

    fun setDefault() {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val contourRet = biz.getCommonULCTR()

            if (contourRet == null) {
                contourList = emptyList()
            } else {
                contourList = contourRet
            }

            val typeRet = api.getCommonCode("ULDTP")

            when (typeRet) {
                is Result.Error -> Common.sendError(typeRet.message)
                is Result.Success<DataTable> -> {
                    val data = typeRet.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { table ->

                            typeList = table

                        } ?: run {
                            typeList = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            val scaleRet = api.getCommonCode("SCALE")

            when (scaleRet) {
                is Result.Error -> Common.sendError(scaleRet.message)
                is Result.Success<DataTable> -> {
                    val data = scaleRet.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { table ->

                            if (table.size >= 5) {
                                scaleList[1] = table[4].row["CODE_CODE"] ?: ""
                            }
                            if (table.size >= 4) {
                                scaleList[0] = table[3].row["CODE_CODE"] ?: ""
                            }

                        } ?: run {
                            scaleList[0] = ""
                            scaleList[1] = ""
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            if (operationSid.isNotEmpty()) {
                getInfo()
            } else {
                scaleUser.value = Common.userName.value
                fltDate.value = Util.formatDate_15(Common.dateTime.value)
            }

            Common.loadingOff()
        }

    }

    suspend fun getInfo() {

        val ret = api.getPWM_ULD_SCALE_MASTER_INFO(operationSid)
        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    uldNo.value = Util.getTableCell(0, data[1], "ULD_NO")
                    contourCode.value = Util.getTableCell(0, data[1], "CONTOUR_CODE")
                    fltDate.value = Util.getTableCell(0, data[1], "DEPARTURE_TIME").substring(0, 10)
                    fltNo.value = Util.getTableCell(0, data[1], "FLIGHT_NO")
                    fltDest.value = Util.getTableCell(0, data[1], "DESTINATION_CODE")
                    weight.value = Util.getTableCell(0, data[1], "SCALED_WEIGHT")
                    dollyWeight.value = Util.getTableCell(0, data[1], "TARE_WEIGHT")
                    scaleUser.value = Util.getTableCell(0, data[1], "SCALED_USER_NAME")

                    if (contourCode.value == "BLK" && uldNo.value.isNotEmpty()) {

                        val findObj = typeList.find { row ->
                            row.row["CODE_CODE"] == uldNo.value.substring(
                                0,
                                3
                            )
                        }

                        if (findObj != null) {
                            weight.value =
                                Util.getDouble(findObj.row["VALUE1_NUMBER"] ?: "0").toString()
                        }

                    }


                    Common.suc.value = "조회가 완료됐습니다."

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    fun uldClick(outParam: (suc: List<DataRow>?) -> Unit) {
        val date = fltDate.value
        val flag = "N"

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_SCALE_M010_001(Util.formatDate_8(date), flag)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        data[1]?.let { table ->
                            outParam(table)
                            Common.loadingOff()
                            return@launch
                        }

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            outParam(null)


            Common.loadingOff()
        }
    }

    suspend fun setFlight(errFlag: Boolean) {
        val validDate = Util.formatDate_8(fltDate.value)

        val fltRet = biz.getFltight(validDate)

        if (fltRet == null) {
            fltList = emptyList()
            fltNo.value = ""
            fltSelect = ""
            if (errFlag) {
                Common.sendError("해당날짜에는 편번이 없습니다.")
            }

        } else {
            fltList = fltRet
        }
    }

    suspend fun setOriginDest(errFlag: Boolean) {
        if (fltSelect.isEmpty()) {
            fltDest.value = ""
            if (errFlag) {
                Common.sendError("해당날짜에는 편번이 없습니다.")
            }
            return
        }

        val ret = biz.getOriginDest(fltSelect)

        if (ret == null) {
            fltDest.value = ""
        } else {
            fltDest.value = Util.getStr(ret.row["DESTINATION_CODE"])
        }


    }

    fun scaleRec() {
        val scaleRb = scaleNoSelect.value

        val index = scaleNoList.indexOf(scaleRb)
        var ip = ""
        val terminalCode = Common.terminalCode.value

        if (index >= 0) {
            if (index == 0 && terminalCode == "T1") {
                ip = "RAMP1"
            } else if (index == 0 && terminalCode == "T2") {
                ip = "RAMP1_T2"
            } else if (index == 1 && terminalCode == "T1") {
                ip = "RAMP2"
            } else if (index == 1 && terminalCode == "T2") {
                ip = "RAMP2_T2"
            } else {
                Common.sendError("알 수 없는 저울입니다.")
                return
            }

            viewModelScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = api.getScaleWeight(ip)
                when (ret) {
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                            weight.value = Util.getTableCell(0, data[0], "COL3")
                            Common.suc.value = "저울 측정 완료"
                        } else {
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                        }
                    }
                }

                Common.loadingOff()
            }
        } else {
            Common.sendError("저울을 선택해주세요")
        }

    }


    suspend fun setSave(): Boolean {

        var selScale = ""
        val scaleRb = scaleNoSelect.value

        val index = scaleNoList.indexOf(scaleRb)
        if (index >= 0) {
            selScale = scaleList[index]

            val ret = api.setPWM_ULD_SCALE_M010_011(
                operationSid,
                selScale,
                weight.value,
                dollyWeight.value,
                Common.userId.value
            )

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장이 완료됐습니다."
                        return true
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
        } else {
            Common.sendError("저울을 선택해주세요")
        }

        return false
    }

    suspend fun setExport(): Boolean {

        val ret = api.setPWM_OPERATION_ULD_M010_033(operationSid)

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    Common.suc.value = "출고됐습니다."
                    return true
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return false
    }

    suspend fun printClick() {
        var ip = "RAMP"
        val terminalCode = Common.terminalCode.value
        val wtTotal = Util.getDouble(weight.value) + Util.getDouble(dollyWeight.value)
        if (terminalCode == "T2") {
            ip = "RAMP_T2"
        }

        val ret = api.setPWM_ULD_SCALE_PRINT(
            uldNo.value,
            contourCode.value,
            Util.formatDate_8(fltDate.value),
            fltNo.value,
            fltDest.value,
            weight.value,
            scaleUser.value,
            weight.value,
            dollyWeight.value,
            wtTotal.toString(),
            ip
        )

        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                }else{
                    Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                }
            }
        }
    }

}