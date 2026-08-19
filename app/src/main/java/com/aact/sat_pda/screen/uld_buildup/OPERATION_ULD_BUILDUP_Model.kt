package com.aact.sat_pda.screen.uld_buildup

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
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_BUILDUP_Model : ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()

    var isEdit = false
    var isEditing = false

    var operationSid = ""
    var fltSelect = ""
    var fltOrigin = ""
    var fltDest = ""
    var toLocation = ""
    var contourCode = ""
    var contourNo = ""
    var bulkFlag = ""
    var mfstWt = ""
    val uldNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val weight = MutableLiveData<String>("")
    val spcCode = MutableLiveData<String>("")
    val spcRemark = MutableLiveData<String>("")

    var spcList = emptyList<DataRow>()
    var fltList = emptyList<DataRow>()

    val workList = MutableLiveData<List<DataRow>>(emptyList())
    val workHeader = listOf<HeaderDTO>(
        HeaderDTO("CHK", "", 80, isChk = true),
        HeaderDTO("CARGO_CONTROL_SID", "id"),
        HeaderDTO("DESTINATION_CODE", "도착지", 150),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 180),
        HeaderDTO("WORKABLE_NO_OF_PACKAGE", "수량", 100),
        HeaderDTO("WORKABLE_NET_WEIGHT", "작업중량", 150),
        HeaderDTO("MFST_NET_WEIGHT", "서류중량", 150),
        HeaderDTO("SPECIAL_CARGO_CODE", "스페셜코드", 220),
    )
    val workSelect = MutableLiveData<Map<String, String>>(
        mapOf(
            "CARGO_CONTROL_SID" to "",
            "SPECIAL_CARGO_CODE" to "",
            "WORKABLE_NO_OF_PACKAGE" to "",
            "WORKABLE_NET_WEIGHT" to "",
            "MFST_NET_WEIGHT" to "",
        )
    )
    val workSelectMap: MutableMap<Int, DataRow> = mutableMapOf()

    val locationList = MutableLiveData<List<DataRow>>(emptyList())
    val locationHeader = listOf<HeaderDTO>(
        HeaderDTO("CARGO_CONTROL_SID", "id"),
        HeaderDTO("LOCATION_CODE", "위치코드", 180),
        HeaderDTO("LOCATION_NAME", "위치명", 220),
        HeaderDTO("NO_OF_PACKAGE", "수량", 100),
    )
    val locationSelect = MutableLiveData<Map<String, String>>(
        mapOf(
            "CARGO_CONTROL_SID" to "",
            "LOCATION_CODE" to ""
        )
    )

    val spcTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            spcCode.value = it["CODE_CODE"] ?: ""
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

                    setOriginDest()

                    Common.loadingOff()
                }
            }
        },
        endClick = {})

    fun setLoad() {
        if (fltDate.value.isEmpty()) {
            fltDate.value = Util.formatDate_15(Common.dateTime.value)
        }
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val spcRet = biz.getCommonSPCGO()
            if (spcRet == null) {
                spcList = emptyList()
            } else {
                spcList = spcRet
            }

            if (operationSid.isNotEmpty()) {
                setInfo()
            } else {
                setInfo2()
            }

            Common.loadingOff()
        }

    }

    suspend fun setInfo() {

        val ret = api.getPWM_ULD_BUILD_UP_M010_001_002_003(operationSid)
        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    Common.suc.value = "조회가 완료됐습니다."

                    toLocation = Util.getTableCell(0, data[1], "LOCATION_CODE")
                    fltSelect = Util.getTableCell(0, data[1], "SCHEDULE_SID")

                    workSelectMap.clear()
                    data[2]?.let { table ->
                        table.forEach { it.row["CHK"] = "N" }
                        workList.value = table
                    } ?: run {
                        workList.value = emptyList()
                    }
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    suspend fun setInfo2() {
        val tmp_fltSid = if (mawb.value.length == 11) "" else fltSelect
        val tmp_fltOrigin = if (mawb.value.length == 11) "" else fltOrigin

        val ret = api.getPWM_ULD_BUILD_UP_M010_002_003(tmp_fltSid, tmp_fltOrigin, "", mawb.value)

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    workSelectMap.clear()
                    data[1]?.let { table ->
                        table.forEach { it.row["CHK"] = "N" }
                        workList.value = table
                    } ?: run {
                        workList.value = emptyList()
                    }
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    fun allSave() {

        if (operationSid.isEmpty()) {
            Common.sendError("ULD를 먼저 저장해주세요")
            return
        }

        val array = workList.value
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            var flag = false
            workSelectMap.forEach { (key, value) ->
                if (Util.getStr(value.row["CHK"]) == "Y") {
                    val cargoControlSid = Util.getStr(array[key].row["CARGO_CONTROL_SID"])
                    val workNetWeight = Util.getStr(array[key].row["WORKABLE_NET_WEIGHT"])
                    val workMfstWeight = Util.getStr(array[key].row["MFST_NET_WEIGHT"])
                    val workPcs = Util.getStr(array[key].row["WORKABLE_NO_OF_PACKAGE"])

                    val ret1 = api.getPWM_ULD_BUILD_UP_M010_003(cargoControlSid)

                    when (ret1) {
                        is Result.Error -> {
                            Common.sendError(ret1.message)
                            flag = true
                        }

                        is Result.Success<DataTable> -> {
                            val data = ret1.data.table
                            if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                                data[1]?.let { table ->

                                    if (table.size > 0) {
                                        table.forEach { row ->
                                            val locationCode = Util.getStr(row.row["LOCATION_CODE"])
                                            val pcs = Util.getStr(row.row["NO_OF_PACKAGE"])

                                            val netWt =
                                                Util.getDouble(pcs) / Util.getDouble(workPcs) * Util.getDouble(
                                                    workNetWeight
                                                )
                                            val mfstWt =
                                                Util.getDouble(pcs) / Util.getDouble(workPcs) * Util.getDouble(
                                                    workMfstWeight
                                                )

                                            val ret2 = api.setPWM_ULD_BUILD_UP_M010_011(
                                                operationSid,
                                                cargoControlSid,
                                                locationCode,
                                                pcs,
                                                netWt.toString(),
                                                mfstWt.toString()
                                            )

                                            when (ret2) {
                                                is Result.Error -> {
                                                    Common.sendError(ret2.message)
                                                    flag = true
                                                    return@let
                                                }

                                                is Result.Success<DataTable> -> {
                                                    val data1 = ret2.data.table
                                                    if (Util.getTableCell(
                                                            0,
                                                            data1[0],
                                                            "COL1"
                                                        ) == "OK"
                                                    ) {

                                                    } else {
                                                        Common.sendError(
                                                            Util.getTableCell(
                                                                0,
                                                                data1[0],
                                                                "COL2"
                                                            )
                                                        )
                                                        flag = true
                                                        return@let
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        flag = true
                                    }


                                } ?: run {
                                    flag = true
                                }

                                if (flag) {
                                    setInfo()
                                    Common.loadingOff()
                                    return@launch
                                }

                            } else {
                                Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                                flag = true
                            }
                        }
                    }
                }
            }

            if (!flag && workSelectMap.size > 0) {
                Common.suc.value = "저장완료"

            }
            setInfo()
            Common.loadingOff()
        }

    }

    fun workSelectIndex(sid: String) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BUILD_UP_M010_003(sid)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        data[1]?.let { table ->
                            locationList.value = table

                        } ?: run {
                            locationSelect.value = mapOf(
                                "CARGO_CONTROL_SID" to "",
                                "LOCATION_CODE" to ""
                            )
                            locationList.value = emptyList()
                        }

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun locationDist() {

        val tmpMap = workSelect.value

        if (tmpMap?.values.isNullOrEmpty()) {
            return
        }

        val workPcs = Util.getDouble(Util.getStr(tmpMap["WORKABLE_NO_OF_PACKAGE"]))
        val wt = Util.getDouble(Util.getStr(tmpMap["WORKABLE_NET_WEIGHT"]))
        val mfstWeight = Util.getDouble(Util.getStr(tmpMap["MFST_NET_WEIGHT"]))
        val inQty = Util.getDouble(pcs.value)

        if (workPcs == inQty || workPcs == 0.0) {
            weight.value = wt.toString()
            mfstWt = mfstWeight.toString()
        } else if (inQty > workPcs) {
            pcs.value = workPcs.toString()
        } else {
            weight.value = Math.floor(inQty / workPcs * wt).toString()
            mfstWt = Math.floor(inQty / workPcs * mfstWeight).toString()
        }


    }

    suspend fun setFlight() {
        val validDate = Util.formatDate_8(fltDate.value)

        val fltRet = biz.getFltight(validDate)

        if (fltRet == null) {
            fltList = emptyList()
            fltNo.value = ""
            fltSelect = ""

        } else {
            fltList = fltRet
        }
    }

    suspend fun setOriginDest() {
        if (fltSelect.isEmpty()) {
            fltDest = ""
            fltOrigin = ""
            return
        }

        val ret = biz.getOriginDest(fltSelect)

        if (ret == null) {
            fltDest = ""
            fltOrigin = ""
        } else {
            fltDest = Util.getStr(ret.row["DESTINATION_CODE"])
            fltOrigin = Util.getStr(ret.row["ORIGIN_CODE"])

        }


    }

    fun setSave() {
        if (fltDate.value.isEmpty() || fltSelect.isEmpty()) {
            Common.sendError("일자, 편번을 확인하십시요.")
            return
        }

        val tmp_workSelect = workSelect.value?.all { it.value.isEmpty() } ?: false
        val tmp_locationSelect = locationSelect.value?.all { it.value.isEmpty() } ?: false

        if (tmp_workSelect || tmp_locationSelect) {
            Common.sendError("미적재화물, 화물보관 항목이 선택되지 않았습니다.")
            return
        }
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            if (operationSid.isEmpty()) {
                val ret = api.setPWM_OPERATION_ULD_M010_011(
                    operationSid,
                    uldNo.value,
                    contourCode,
                    contourNo,
                    fltSelect,
                    fltOrigin,
                    fltDest,
                    toLocation,
                    "N",
                    bulkFlag,
                    "N",
                    ""
                )
                when (ret) {
                    is Result.Error -> {
                        Common.sendError(ret.message)
                        Common.loadingOff()
                        return@launch
                    }

                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                            operationSid = Util.getTableCell(0, data[0], "COL2")
                        } else {
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                            Common.loadingOff()
                            return@launch
                        }
                    }
                }
            }

            val cargoSid = locationSelect.value["CARGO_CONTROL_SID"] ?: ""
            val locationCode = locationSelect.value["LOCATION_CODE"] ?: ""

            val ret = api.setPWM_ULD_BUILD_UP_M010_014(
                operationSid,
                cargoSid,
                locationCode,
                pcs.value,
                weight.value,
                mfstWt,
                spcCode.value,
                spcRemark.value
            )

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        Common.suc.value = "저장완료"
                        setInfo()
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }
}