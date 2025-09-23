package com.aact.sat_pda.screen.control

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result

class CARGO_CONTROL_Model : ViewModel() {

    var cargoControlSid = ""
    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val company = MutableLiveData<String>("")


    val headerList = listOf<HeaderDTO>(
        HeaderDTO("CARGO_ACCEPT_SID", "id"),
        HeaderDTO("CARGO_ACCEPT_SEQ", "NO", 80),
        HeaderDTO("ACCEPTED_TIME", "접수시간", 160),
        HeaderDTO("CARGO_STATUS_NAME", "상태", 140),
        HeaderDTO("NO_OF_PACKAGE", "PCS", 120),
        HeaderDTO("BUP_FLAG", "BUP 여부", 80)
    )

    val bodyList = MutableLiveData<List<DataRow>>(emptyList())
    var selectList = mapOf("CARGO_ACCEPT_SID" to "", "CARGO_ACCEPT_SEQ" to "")

    val fltSelect = MutableLiveData<String>("")
    val fltList = MutableLiveData<List<DataRow>>(emptyList())

    var isEditing = false


    fun startProc() {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            isEditing = true
            val ret = api.getPWM_CARGO_CONTROL_M010_001_002(I_CARGO_CONTROL_SID = cargoControlSid)
            when (ret) {
                is Result.Success<DataTable> -> {

                    val data = ret.data.table

                    data[0]?.let { status ->
                        if (status[0].row["COL1"] == "OK") {
                            mawb.value = (Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO"))
                            hawb.value = (Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO"))

                            val temp_fltDate = Util.changeDate(
                                Util.getTableCell(
                                    0,
                                    data[1],
                                    "FLIGHT_DATE"
                                )
                            )
                            fltDate.value = (
                                    temp_fltDate
                                    )

                            val temp_fltList = biz.getFltight(temp_fltDate.replace("-", ""))
                            val temp_fltSid = Util.getTableCell(
                                0,
                                data[1],
                                "SCHEDULE_SID"
                            )

                            temp_fltList?.let { fltData ->
                                val temp_row =
                                    fltData.find { rows -> rows.row["SCHEDULE_SID"] == temp_fltSid }
                                fltNo.value = (temp_row?.row["FLIGHT_NO"] ?: "")
                            }

                            fltSelect.postValue(temp_fltSid)
                            fltDest.value = (Util.getTableCell(0, data[1], "DESTINATION_CODE"))

                            data[2]?.let { table ->
                                bodyList.postValue(table)
                            } ?: run {
                                bodyList.postValue(emptyList())
                            }

                            data[3]?.let { table ->
                                company.value = (Util.getTableCell(0, table, "CUSTOMER_CODE"))
                            } ?: run {
                                company.value = ("")
                            }
                            Common.suc.value = "조회가 완료됬습니다."
                        } else {
                            Common.sendError((status[0].row["COL3"] ?: "MAWB 정보 에러"))
                        }

                    } ?: run {
                        Common.sendError("서버 에러")
                    }

                }

                is Result.Error -> {
                    Common.sendError(ret.message)
                }
            }
            isEditing = false
            Common.loadingOff()
        }

    }

    fun updateProc(company: String, fltDest: String) {

        viewModelScope.launch {
            var customerCode = ""
            var agencyName = ""


            Common.loadingOn(coroutineContext[Job])

            if (company.isNotEmpty()) {
                val result = biz.getAgencyInfo(company)

                if (result == null) {
                    Common.loadingOff()
                    return@launch
                }

                customerCode = result.row["CUSTOMER_CODE"] ?: ""
                agencyName = result.row["CUSTOMER_NAME"] ?: ""

                if (customerCode.isEmpty() || agencyName.isEmpty()) {
                    Common.sendError("대리점 코드를 확인하세요")
                    Common.loadingOff()
                    return@launch
                }
            }

            if (fltDest.isNotEmpty()) {
                val dest = biz.getCommonCode("APORT", fltDest)

                if (dest == null) {
                    Common.loadingOff()
                    return@launch
                }

                val filterData = dest.filter { it.row["CODE_CODE"] == fltDest }
                if (filterData.size == 0) {
                    Common.sendError("도착지 코드를 확인하십시요")
                    Common.loadingOff()
                    return@launch
                }
            }

            val ret: Result<DataTable>

            ret = api.setPWM_CARGO_CONTROL_M010_032_DEST_AGENCY(
                cargoControlSid,
                fltSelect.value,
                fltDest,
                customerCode,
                agencyName
            )

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                }

                is Result.Success<DataTable> -> {

                    val data = ret.data.table

                    data[0]?.let { status ->

                        if (Util.getTableCell(0, status, "COL1") == "OK") {
                            Common.suc.value = "수정이 완료됬습니다."
                            startProc()
                        } else {
                            Common.sendError(Util.getTableCell(0, status, "COL2", "수정에러"))
                        }

                    } ?: run {
                        Common.sendError("서버 에러")
                    }
                }
            }

            Common.loadingOff()
        }

    }

    fun printProc() {
        var ip = "CARGOACCEPT"


        if (Common.terminalCode.value == "T2") {
            ip = "CARGOACCEPT_T2"
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var ret: Result<DataTable>

            ret = api.setPWM_CARGO_SCALE_PRINT_MAWB(cargoControlSid, ip)

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                    Common.loadingOff()
                    return@launch
                }

                else -> {

                }
            }

            ret = api.setPWM_CARGO_VOLUME_PRINT_MAWB(cargoControlSid, ip)

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                    Common.loadingOff()
                    return@launch
                }

                else -> {
                    Common.suc.value = "출력이 완료됬습니다."
                }
            }

            Common.loadingOff()
        }

    }

    fun inClick() {
        //BUP_FLAG
        val cargoList = bodyList.value
        var acceptSid = ""
        var acceptSeq = ""
        var bupFlag = "N"
        if (cargoList.isNullOrEmpty()) {
            Common.sendError("접수 목록이 없습니다.")
            return
        } else {
            val cargoTemp =
                cargoList.find { it.row["CARGO_ACCEPT_SID"] == selectList["CARGO_ACCEPT_SID"] && it.row["CARGO_ACCEPT_SEQ"] == selectList["CARGO_ACCEPT_SEQ"] }
            cargoTemp?.let {
                it.row["CARGO_ACCEPT_SID"]?.let { value ->
                    acceptSid = value
                }
                it.row["CARGO_ACCEPT_SEQ"]?.let { value ->
                    acceptSeq = value
                }
                it.row["BUP_FLAG"]?.let { value ->
                    bupFlag = value
                }
            }
        }

        if (acceptSid == "" || acceptSeq == "") {
            Common.sendError("선택한 항목이 없습니다.")
            return
        }

        val params =
            listOf(Pair("CARGO_ACCEPT_SID", acceptSid), Pair("CARGO_ACCEPT_SEQ", acceptSeq))

        if (bupFlag == "Y") {

            viewModelScope.launch {

                Common.loadingOn(coroutineContext[Job])

                val currDT = biz.getCurDateTime()

                val ret = api.setPWM_CARGO_STOCK_M015_011(
                    acceptSid,
                    currDT.substring(0, 8),
                    currDT.substring(8, 12)
                )

                when (ret) {
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        data[0]?.let { status ->
                            if (Util.getTableCell(0, status, "COL1") == "OK") {

                                startProc()

                            } else {
                                Common.sendError(Util.getTableCell(0, status, "COL2", "수정에러"))
                            }
                        } ?: run {
                            Common.sendError("서버 에러")
                        }
                    }
                }

                Common.loadingOff()
            }

        } else {

            //param = parms 입고 가기

            val cargoIn = Route.Stock
            cargoIn.params = params
            Common.addNavigate(cargoIn)
        }


    }

    fun cancelClick() {
        val cargoList = bodyList.value
        var acceptSid = ""
        var acceptSeq = ""
        var bupFlag = "N"
        if (cargoList.isNullOrEmpty()) {
            Common.sendError("접수 목록이 없습니다.")
            return
        } else {
            val cargoTemp =
                cargoList.find { it.row["CARGO_ACCEPT_SID"] == selectList["CARGO_ACCEPT_SID"] && it.row["CARGO_ACCEPT_SEQ"] == selectList["CARGO_ACCEPT_SEQ"] }
            cargoTemp?.let {
                it.row["CARGO_ACCEPT_SID"]?.let { value ->
                    acceptSid = value
                }
                it.row["CARGO_ACCEPT_SEQ"]?.let { value ->
                    acceptSeq = value
                }
                it.row["BUP_FLAG"]?.let { value ->
                    bupFlag = value
                }
            }
        }

        if (acceptSid == "" || acceptSeq == "") {
            Common.sendError("선택한 항목이 없습니다.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var ret: Result<DataTable>

            if (bupFlag == "N") {
                ret = api.setPWM_CARGO_STOCK_M010_021(acceptSid)
            } else {
                ret = api.setPWM_CARGO_STOCK_M015_021(acceptSid)
            }

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    data[0]?.let { status ->
                        if (Util.getTableCell(0, status, "COL1") == "OK") {
                            Common.suc.value = "취소가 완료됬습니다."
                            startProc()
                        } else {
                            Common.sendError(Util.getTableCell(0, status, "COL2", "수정에러"))
                        }
                    } ?: run {
                        Common.sendError("서버에러")
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun getDest(select: String) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getOriginDest(select)

            ret?.row?.let { data ->
                fltDest.value = (data["DESTINATION_CODE"] ?: "")
            }

            Common.loadingOff()
        }
    }
}