package com.aact.sat_pda.screen.import_cargo_ovcd

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

class IMPORT_CARGO_OVCD_Model : ViewModel() {
    val api = MainAPI_Impl()
    val biz = BizAPI()

    // 일자 및 편번
    val fltDate = MutableLiveData("")
    var scheduleSid = ""

    // 편번 리스트
    var flightList: List<DataRow> = emptyList()
    val flightNameList = MutableLiveData<List<String>>(emptyList())
    val flightIndex = MutableLiveData(-1)

    // 화물 정보
    val mawb = MutableLiveData("")
    val hawb = MutableLiveData("")
    val mrn = MutableLiveData("")
    val msn = MutableLiveData("OVCD") // Fixed value
    val hsn = MutableLiveData("")

    val origin = MutableLiveData("")
    val dest = MutableLiveData("")

    val pcs = MutableLiveData("")
    val wt = MutableLiveData("")

    var holdTypeList: List<DataRow> = emptyList()
    val holdTypeNameList = MutableLiveData<List<String>>(emptyList())
    val holdTypeCode = MutableLiveData("")
    val holdTypeIndex = MutableLiveData(-1)

    val holdDate = MutableLiveData("")
    val holdTime = MutableLiveData("")
    val remarks = MutableLiveData("")

    // 현재 일시로 초기화
    fun initDateTime() {
        viewModelScope.launch {
            val curDt = biz.getCurDateTime()
            fltDate.value = curDt.substring(0, 8)
            holdDate.value = curDt.substring(0, 8)
            holdTime.value = curDt.substring(8, 14)
        }
    }

    // 보류 유형 리스트 로드
    fun getHoldTypeList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // IRR TYPE 공통코드 (IRTYP)
            val ret = biz.getCommonCode("IRTYP", "")
            if (ret != null) {
                holdTypeList = ret
                holdTypeNameList.value = ret.map { it.row["CODE_NAME"] ?: "" }
                // Default to index 2 (C# code: cmbHoldType.SelectedIndex = 2)
                if (ret.size > 2) {
                    holdTypeIndex.value = 2
                    holdTypeCode.value = ret[2].row["CODE_CODE"] ?: ""
                }
            } else {
                holdTypeNameList.value = emptyList()
            }

            Common.loadingOff()
        }
    }

    // 편번 리스트 로드
    fun getFlightList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            if (fltDate.value.isNullOrEmpty() || fltDate.value!!.length != 8) {
                flightNameList.value = emptyList()
                Common.loadingOff()
                return@launch
            }

            val ret = api.getPWM_SCHEDULE_L020_002(
                "I", // Import
                fltDate.value ?: "",
                "",
                "Y"
            )

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { table ->
                            flightList = table
                            flightNameList.value = table.map {
                                it.row["FLIGHT_NO"] ?: ""
                            }
                        } ?: run {
                            flightList = emptyList()
                            flightNameList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 스케줄 정보 로드
    fun setFlightInfo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            setClear()

            if (scheduleSid.isEmpty()) {
                Common.loadingOff()
                return@launch
            }

            val ret = api.getPWM_CARGO_CONTROL_M040_001(scheduleSid)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        if (data[1] != null && data[1]!!.isNotEmpty()) {
                            origin.value = Util.getTableCell(0, data[1], "ORIGIN_CODE")
                            dest.value = Util.getTableCell(0, data[1], "DESTINATION_CODE")
                            mrn.value = Util.getTableCell(0, data[1], "MANIFEST_REFERENCE_NO")
                            msn.value = Util.getTableCell(0, data[1], "MASTER_BL_SEQUENCE_NO")
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 저장
    fun saveClick() {
        // 유효성 검사
        if (pcs.value.isNullOrEmpty() || pcs.value == "0") {
            Common.sendError("수량을 확인하십시오.")
            return
        }

        if (scheduleSid.isEmpty()) {
            Common.sendError("편번을 선택하십시오.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_CONTROL_M040_011(
                "0",
                scheduleSid,
                mrn.value ?: "",
                msn.value ?: "OVCD",
                hsn.value ?: "",
                mawb.value ?: "",
                hawb.value ?: "",
                origin.value ?: "",
                dest.value ?: "",
                "",
                "0",
                "0",
                pcs.value ?: "0",
                wt.value ?: "0",
                holdTypeCode.value ?: "",
                Common.userId.value,
                holdDate.value ?: "",
                holdTime.value ?: "",
                remarks.value ?: ""
            )

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장완료"
                        setClear()
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 화면 초기화 (편번 선택 정보 제외)
    fun setClear() {
        mawb.value = ""
        hawb.value = ""
        origin.value = ""
        dest.value = ""
        remarks.value = ""
        pcs.value = ""
        wt.value = ""
    }
}
