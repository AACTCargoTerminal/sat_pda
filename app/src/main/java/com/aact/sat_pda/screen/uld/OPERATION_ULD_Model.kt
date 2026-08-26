package com.aact.sat_pda.screen.uld

import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OPERATION_ULD_Model : ViewModel() {

    val biz = BizAPI()
    val api = MainAPI_Impl()

    var isEditing = false
    var isUldSetting = false

    var uldNo = ""
    var operationSid = ""
    var fltSelect = ""
    var uldValid = ""
    var fltOrigin = ""
    var fltDestName = ""
    var originScaleWt = ""
    var agentName = ""

    val uldType = MutableLiveData<String>("")
    val uldSerialNo = MutableLiveData<String>("")
    val uldCarrier = MutableLiveData<String>("")

    val contourCode = MutableLiveData<String>("")
    val contourNo = MutableLiveData<String>("")
    val position = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>(Util.formatDate_16(Common.dateTime.value))
    val fltNo = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")
    val status = MutableLiveData<String>("")
    val remark = MutableLiveData<String>("")
    val bdPcs = MutableLiveData<String>("")
    val bdWeight = MutableLiveData<String>("")
    val transAt = MutableLiveData<String>("")
    val transFlt = MutableLiveData<String>("")
    val uldWeight = MutableLiveData<String>("")
    val agent = MutableLiveData<String>("")
    val bupFlag = MutableLiveData<Boolean>(false)
    val bulkFlag = MutableLiveData<Boolean>(false)
    val thruFlag = MutableLiveData<Boolean>(false)
    val raFlag = MutableLiveData<Boolean>(false)

    var typeList: List<DataRow> = emptyList()
    var carrierList: List<DataRow> = emptyList()
    var positionList: List<DataRow> = emptyList()
    var contourList: List<DataRow> = emptyList()
    var fltList: List<DataRow> = emptyList()

    val countourTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            contourCode.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )

    val positionTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            position.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )
    val carrierTable = SubContent.GetTable(
        headerList = Header().crrcdHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            uldCarrier.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )
    val typeTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            uldType.value = it["CODE_CODE"] ?: ""
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
            fltDestName = it["CODE_NAME"] ?: ""
        },
        endClick = {}
    )

    fun setDefault(outParam: (suc: Boolean) -> Unit) {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var ret: List<DataRow>?

            ret = biz.getCommonULCTR()

            if (ret == null) {
                contourList = emptyList()
            } else {
                contourList = ret
            }

            ret = biz.getCommonCode("WHLOC", "")

            if (ret == null) {
                positionList = emptyList()
            } else {
                positionList = ret
            }

            ret = biz.getCommonCRRCDULD()

            if (ret == null) {
                carrierList = emptyList()
            } else {
                carrierList = ret
            }

            ret = biz.getCommonULDTP()

            if (ret == null) {
                typeList = emptyList()
            } else {
                typeList = ret
            }

            if (uldNo.isEmpty()) {
                uldType.value = "PMC"
            } else {
                setUldNoToControl(uldNo)
            }

            if (Common.departCode.value == "33") {
                bupFlag.value = true
            }

            if (operationSid.isNotEmpty()) {
                //조회
                getInfo()
            } else {

                if (fltNo.value.isNotEmpty()) {
                    setFlight(false)
                    val findData =
                        fltList.find { row -> Util.getStr(row.row["FLIGHT_NO"]) == fltNo.value }
                    if (findData != null) {
                        fltSelect = Util.getStr(findData.row["SCHEDULE_SID"])
                    } else {
                        fltNo.value = ""
                        fltSelect = ""
                    }
                } else {
                    fltNo.value = ""
                    fltSelect = ""
                }

                if (fltSelect.isNotEmpty()) {
                    setOriginDest(false)
                }


            }

            Common.loadingOff()
            outParam(true)
        }


    }

    fun setUldNoToControl(arg1: String) {
        if (arg1.length <= 5) {
            return
        }
        isUldSetting = true

        uldType.value = arg1.substring(0, 3)
        uldCarrier.value = arg1.substring(arg1.length - 2)
        uldSerialNo.value = arg1.substring(3, arg1.length - 2)

        isUldSetting = false
    }

    fun getInfo() {

        uldValid = ""

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_OPERATION_ULD_M010_001(operationSid)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        Common.suc.value = "조회가 완료됐습니다."

                        uldValid = "Y"
                        contourCode.value = Util.getTableCell(0, data[1], "CONTOUR_CODE")
                        contourNo.value = Util.getTableCell(0, data[1], "CONTOUR_NO")

                        position.value = Util.getTableCell(0, data[1], "LOCATION_CODE")
                        if (Util.getTableCell(0, data[1], "BUP_FLAG") == "Y") {
                            bupFlag.value = true
                        } else {
                            bupFlag.value = false
                        }

                        if (Util.getTableCell(0, data[1], "BULK_FLAG") == "Y") {
                            bulkFlag.value = true
                        } else {
                            bulkFlag.value = false
                        }

                        if (Util.getTableCell(0, data[1], "THRU_FLAG") == "Y") {
                            thruFlag.value = true
                        } else {
                            thruFlag.value = false
                        }

                        uldWeight.value = Util.getTableCell(0, data[1], "SCALED_WEIGHT")
                        originScaleWt = Util.getTableCell(0, data[1], "SCALED_WEIGHT")
                        remark.value = Util.getTableCell(0, data[1], "REMARKS")
                        status.value = Util.getTableCell(0, data[1], "ULD_STATUS_NAME")

                        fltDate.value =
                            Util.changeDate(Util.getTableCell(0, data[1], "FLIGHT_DATE"))

                        fltNo.value = Util.getTableCell(0, data[1], "FLIGHT_NO")

                        if (fltNo.value.isNotEmpty()) {
                            setFlight(false)
                            val findData =
                                fltList.find { row ->
                                    Util.getStr(row.row["FLIGHT_NO"]) == fltNo.value
                                }
                            if (findData != null) {
                                fltSelect = Util.getStr(findData.row["SCHEDULE_SID"])
                            } else {
                                fltNo.value = ""
                                fltSelect = ""
                            }
                        } else {
                            fltNo.value = ""
                            fltSelect = ""
                        }

                        fltOrigin = Util.getTableCell(0, data[1], "ORIGIN_CODE")
                        fltDest.value = Util.getTableCell(0, data[1], "DESTINATION_CODE")
                        fltDestName = Util.getTableCell(0, data[1], "DESTINATION_CODE")

                        bdWeight.value = Util.getTableCell(0, data[1], "NET_WEIGHT")
                        bdPcs.value = Util.getTableCell(0, data[1], "NO_OF_PACKAGE")

                        agent.value = Util.getTableCell(0, data[1], "AGENT_AGENCY_CODE")
                        agentName = Util.getTableCell(0, data[1], "AGENT_CUSTOMER_NAME")

                        transAt.value = Util.getTableCell(0, data[1], "TRANSFERAT")
                        transFlt.value = Util.getTableCell(0, data[1], "TRANSFERFLT")

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }


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
            fltDestName = ""
            if (errFlag) {
                Common.sendError("해당날짜에는 편번이 없습니다.")
            }
            return
        }

        val ret = biz.getOriginDest(fltSelect)

        if (ret == null) {
            fltOrigin = ""
            fltDest.value = ""
            fltDestName = ""
        } else {
            fltOrigin = Util.getStr(ret.row["ORIGIN_CODE"])
            fltDest.value = Util.getStr(ret.row["DESTINATION_CODE"])
            fltDestName = Util.getStr(ret.row["DESTINATION_NAME"])
        }


    }

    suspend fun setULDValid(): Boolean {
        if (uldValid == "Y") {
            return true
        }

        val ret = api.getPWM_ULD_INVENTORY_P020_001(
            "",
            "",
            uldType.value + uldSerialNo.value + uldCarrier.value,
            "Y"
        )

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    val uldNo = Util.getTableCell(0, data[1], "ULD_NO")
                    if (uldNo.isNotEmpty()) {
                        setUldNoToControl(uldNo)
                        return true
                    }
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return false
    }

    suspend fun setSave() {
        val valid = setULDValid()
        if (!valid) {
            val b = setULDInput()
            if (!b) {
                return
            }
        }

        if (contourCode.value.isEmpty()) {
            Common.sendError("Contour 코드를 입력 하여 주십시오.")
            return
        }

        if (fltDate.value.isEmpty() || fltSelect.isEmpty()) {
            Common.sendError("일자, 편번을 확인하십시요")
            return
        }

        val uldNo = uldType.value + uldSerialNo.value + uldCarrier.value

        val bup = if (bupFlag.value) "Y" else "N"
        val bulk = if (bulkFlag.value) "Y" else "N"
        val thru = if (thruFlag.value) "Y" else "N"

        val ret = api.setPWM_OPERATION_ULD_M010_011_T2A(
            operationSid,
            uldNo,
            contourCode.value,
            contourNo.value,
            fltSelect,
            fltOrigin,
            fltDest.value,
            position.value,
            bup,
            bulk,
            thru,
            remark.value,
            uldWeight.value,
            agent.value,
            agentName,
            transAt.value,
            transFlt.value
        )

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    operationSid = Util.getTableCell(0, data[0], "COL2")
                    Common.suc.value = "저장완료"
                    return
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    suspend fun setULDInput(): Boolean {
        if (operationSid.isNotEmpty()) {
            uldValid = "Y"
            return true
        }

        val uldNo = uldType.value + uldSerialNo.value + uldCarrier.value

        val ret = api.setPWM_ULD_STOCK_M010_011(
            "",
            uldNo,
            uldType.value,
            uldSerialNo.value,
            uldCarrier.value,
            "S",
            contourNo.value,
            uldCarrier.value,
            "GIE",
            "N",
            "N",
            "",
            ""
        )

        when (ret) {
            is Result.Error -> {
                Common.sendError(ret.message)
            }

            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    uldValid = ""
                    return true

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return false

    }

    fun clearAll() {
        uldCarrier.value = ""

        uldType.value = "PMC"

        uldSerialNo.value = ""
        operationSid = ""
        position.value = ""
        if (Common.departCode.value == "33") {
            bupFlag.value = true
        } else {
            bupFlag.value = false
        }

        bulkFlag.value = false
        thruFlag.value = false
        raFlag.value = false

        uldValid = ""
        status.value = ""
        uldWeight.value = ""
        bdWeight.value = ""
        bdPcs.value = ""
        originScaleWt = ""
        agent.value = ""
        agentName = ""
        remark.value = ""
        uldNo = ""
        transAt.value = ""
        transFlt.value = ""

    }

    fun clearUldInfo() {
        operationSid = ""
        uldValid = ""

        position.value = ""

        bupFlag.value = true
        bulkFlag.value = false
        thruFlag.value = false
        raFlag.value = false

        status.value = ""
        uldWeight.value = ""
        bdWeight.value = ""
        bdPcs.value = ""
        originScaleWt = ""

        agent.value = ""
        agentName = ""
        remark.value = ""
    }

    fun setDelete() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_OPERATION_ULD_M010_021(operationSid, "Y")

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        clearAll()
                        Common.suc.value = "삭제를 완료했습니다."

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setCompleteValid(outParam: (Int?) -> Unit) {
        var saveCnt = 0
        if (operationSid.isEmpty()) {
            Common.sendError("저장되지 않은 작업 ULD입니다.")
            return
        }

        if (contourCode.value.isEmpty()) {
            Common.sendError("Contour를 입렵하십시요.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_OPERATION_ULD_M010_003(operationSid)
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { table ->
                            table.forEach { row ->
                                if (Util.getDouble(row.row["MATERIAL_QTY"] ?: "0") > 0) {
                                    saveCnt++
                                }
                            }

                        }
                        outParam(saveCnt)
                        Common.loadingOff()
                        return@launch
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            outParam(null)

            Common.loadingOff()
        }
    }

    fun setComplete() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_OPERATION_ULD_M010_031(operationSid)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "B/D 완료됐습니다."
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setCancel() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_OPERATION_ULD_M010_032(operationSid)
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "취소가 완료됐습니다."
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setOffload() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_OPERATION_ULD_M010_034(operationSid)
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "오프로드가 완료됐습니다."
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun printClick() {
        var ip = "SCALE#2"

        if (Common.terminalCode.value != "T1") {
            ip += "_" + Common.terminalCode.value
        }

        val agent_tmp = agent.value
        var agentStr = ""
        if (agent_tmp.isNotEmpty()) {
            agentStr = "   (" + agent_tmp.trim() + ")"
        }

        val uldNo = uldType.value + uldSerialNo.value + uldCarrier.value

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_ULD_SCALE_SLIP_ONLY_PRINT(
                uldNo + agentStr,
                contourCode.value,
                Util.formatDate_8(fltDate.value),
                fltNo.value,
                fltDest.value,
                uldWeight.value,
                Common.userName.value,
                ip
            )

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        Common.suc.value = "출력이 완료됐습니다."
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            if(originScaleWt != uldWeight.value){
                val ret = api.getPWM_ULD_BUILD_UP_M010_004(operationSid)
                when(ret){
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        data[1]?.let { table ->
                            table.forEach { row->
                                val cargoSid = row.row["CARGO_CONTROL_SID"]?:""
                                if(cargoSid.isNotEmpty()){
                                    val sql = StringBuilder().apply {
                                        append("MERGE INTO TWM_CARGO_SLIP_PRINT A ")
                                        append("USING DUAL ")
                                        append("ON (A.CARGO_CONTROL_SID = '${cargoSid}') ")
                                        append("WHEN MATCHED THEN ")
                                        append("UPDATE SET A.UPDATED_TIME = SYSDATE ")
                                        append(", UPDATED_USER = '${Common.userId.value}' ")
                                        append("WHEN NOT MATCHED THEN ")
                                        append("INSERT (A.CARGO_CONTROL_SID, A.WEIGHT_PRINT_CNT, A.VOLUME_PRINT_CNT, CREATED_TIME, UPDATED_TIME, CREATED_USER, UPDATED_USER) ")
                                        append("VALUES ('${cargoSid}',  1,  0, SYSDATE, SYSDATE,'${Common.userId.value}','${Common.userId.value}') ")
                                    }.toString()

                                    api.getSqlDataSet(sql)
                                }
                            }
                        }
                    }
                }
            }

            Common.loadingOff()
        }
    }
}