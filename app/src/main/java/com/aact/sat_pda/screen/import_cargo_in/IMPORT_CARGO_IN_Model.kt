package com.aact.sat_pda.screen.import_cargo_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_CARGO_IN_Model: ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()

    var cargoSid = ""
    var cargoNo = MutableLiveData<String>("")

    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")

    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val paperPcs = MutableLiveData<String>("")
    val paperWt = MutableLiveData<String>("")
    val workPcs = MutableLiveData<String>("")
    val workWt = MutableLiveData<String>("")

    val status = MutableLiveData<String>("")

    var scheduleSid = ""

    val assign = MutableLiveData<String>("")
    val unLoadCode = MutableLiveData<String>("")
    val cargoDes = MutableLiveData<String>("")

    val location = MutableLiveData<String>("")
    val rackNo = MutableLiveData<String>("")

    val ediFlag = MutableLiveData<String>("")
    val spFlag = MutableLiveData<String>("")

    val locationList = MutableLiveData<List<String>>()
    val rackNoList = MutableLiveData<List<String>>()

    val ynList = listOf<String>("N","Y")


    private var savedPcs = ""
    private var savedWt = ""


    fun setInfo() {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_STOCK_M020_001(cargoSid)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0,data[0],"COL1") =="OK") {

                        setInfoFromDataSet(data)

                    }else {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    fun setInfoByNo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_STOCK_M020_001_BY_NO(cargoNo.value)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0,data[0],"COL1")=="OK") {

                        setInfoFromDataSet(data)

                    } else {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun saveClick() {

        if (cargoSid.isEmpty()){
            Common.sendError("입고처리 화물이 조회되지 않았습니다.")
            Common.loadingOff()
            return
        }

        if (location.value.isNullOrEmpty() || rackNo.value.isNullOrEmpty()) {
            Common.sendError("입고위치, RackNo를 확인하십시오")
            Common.loadingOff()
            return
        }
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            if (workPcs.value != savedPcs || workWt.value != savedWt) {
                val ret = api.setPWM_ULD_BREAK_DOWN_M020_011("",
                                                            scheduleSid,
                                                            "",
                                                            cargoSid,
                                                            workPcs.value,
                                                            workWt.value)

                when(ret) {
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if (Util.getTableCell(0,data[0],"COL1")=="OK") {
                        } else {
                            Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                            Common.loadingOff()
                            return@launch
                        }
                    }
                }
            }

            val curDateTime = biz.getCurDateTime()
            val ret2 = api.setPWM_CARGO_STOCK_M020_011(cargoSid,
                                                        curDateTime.substring(0,8),
                                                        curDateTime.substring(8,12),
                                                        ediFlag.value,
                                                        location.value,
                                                        rackNo.value,
                                                        spFlag.value)

            when(ret2) {
                is Result.Error -> Common.sendError(ret2.message)
                is Result.Success<DataTable> -> {
                    val data1 = ret2.data.table
                    if (Util.getTableCell(0,data1[0],"COL1") == "OK") {
                        Common.suc.value = "저장완료"
                    } else {
                        Common.sendError(Util.getTableCell(0,data1[0],"COL2"))
                    }
                }
            }
            Common.loadingOff()
        }

    }

    fun getLocationList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val locations = biz.getPositionListIMPC()

            locations?.let { dataRows ->
                val locationCodes = dataRows.map {row ->
                    row.row["CODE_CODE"]?.toString() ?: ""
                }.filter { it.isNotEmpty() }

                locationList.value = locationCodes
            } ?: run {
                locationList.value = emptyList()
            }

            Common.loadingOff()
        }
    }

    fun getRackNoList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val racks = biz.getCommonCode("RCKNO","")

            racks?.let {dataRows ->
                val rackCodes = dataRows.map { row ->
                    row.row["CODE_CODE"]?.toString() ?: ""
                }.filter { it.isNotEmpty() }

                rackNoList.value = rackCodes
            } ?: run {
                rackNoList.value = emptyList()
            }

            Common.loadingOff()
        }
    }

    fun setInfoFromDataSet(data: LinkedHashMap<Int, MutableList<DataRow>>) {
        Common.suc.value = "조회 완료"

        cargoSid = Util.getTableCell(0, data[1], "CARGO_CONTROL_SID")

        mawb.value = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO")
        hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")
        fltDate.value = Util.getTableCell(0, data[1], "FLIGHT_DATE")
        fltNo.value = Util.getTableCell(0, data[1], "FLIGHT_NO")
        paperPcs.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
        paperWt.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")

        workPcs.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
        workWt.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")

        savedPcs = Util.getTableCell(0, data[1], "ACCEPTED_NO_OF_PACKAGE")
        savedWt = Util.getTableCell(0,data[1],"ACCEPTED_NET_WEIGHT")

        status.value = Util.getTableCell(0, data[1], "CARGO_STATUS_NAME")
        location.value = Util.getTableCell(0, data[1], "DEFAULT_LOCATION_CODE")

        scheduleSid = Util.getTableCell(0, data[1], "SCHEDULE_SID")
        assign.value = Util.getTableCell(0, data[1], "ASSIGN_CODE")
        unLoadCode.value = Util.getTableCell(0, data[1], "UNLOAD_TYPE_CODE")
        cargoDes.value = Util.getTableCell(0, data[1], "CARGO_DESCRIPTION")

        ediFlag.value = Util.getTableCell(0,data[1],"EDI_FLAG")
        spFlag.value = Util.getTableCell(0,data[1],"SP_FLAG")
    }

    fun setClearAll(skipCargoNo: Boolean = false) {
        if (!skipCargoNo) cargoNo.value =""

        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        fltDate.value = ""
        fltNo.value = ""
        paperPcs.value = ""
        paperWt.value = ""
        workPcs.value = ""
        workWt.value = ""
        status.value = ""
        location.value = ""
        rackNo.value = ""
        scheduleSid = ""
        assign.value = ""
        unLoadCode.value = ""
        cargoDes.value = ""
        ediFlag.value = ""
        spFlag.value = ""
        savedPcs = ""
        savedWt = ""
    }



}