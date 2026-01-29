package com.aact.sat_pda.screen.import_uld

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

class IMPORT_ULD_Model : ViewModel() {
    val api = MainAPI_Impl()
    val biz = BizAPI()

    // ULD No
    var uldTypeList: List<DataRow> = emptyList()
    val uldTypeNameList = MutableLiveData<List<String>>(emptyList())
    val uldTypeIndex = MutableLiveData(-1)
    val uldTypeCode = MutableLiveData("")

    val uldSerialNo = MutableLiveData("")

    var carrierList: List<DataRow> = emptyList()
    val carrierNameList = MutableLiveData<List<String>>(emptyList())
    val carrierIndex = MutableLiveData(-1)
    val carrierCode = MutableLiveData("")

    // 일자 및 편번
    val fltDate = MutableLiveData("")
    var scheduleSid = ""

    var flightList: List<DataRow> = emptyList()
    val flightNameList = MutableLiveData<List<String>>(emptyList())
    val flightIndex = MutableLiveData(-1)

    // Origin
    val origin = MutableLiveData("")

    // ULD 목록
    val uldList = MutableLiveData<List<DataRow>>(emptyList())

    // 현재 일시로 초기화
    fun initDateTime() {
        viewModelScope.launch {
            val curDt = biz.getCurDateTime()
            fltDate.value = curDt.substring(0, 8)
        }
    }

    // ULD 타입 리스트 로드
    fun getULDTypeList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // ULD TYPE 공통코드
            val ret = biz.getCommonULDTP()
            if (ret != null) {
                uldTypeList = ret
                uldTypeNameList.value = ret.map { it.row["CODE_NAME"] ?: "" }
            } else {
                uldTypeNameList.value = emptyList()
            }

            Common.loadingOff()
        }
    }

    // 항공사 리스트 로드
    fun getCarrierList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // Carrier 공통코드 (ULD용)
            val ret = biz.getCommonCRRCDULD()
            if (ret != null) {
                carrierList = ret
                carrierNameList.value = ret.map { it.row["CODE_NAME"] ?: "" }
            } else {
                carrierNameList.value = emptyList()
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
                I_INOUT_FLAG = "I", // Import
                I_FLIGHT_DATE = fltDate.value ?: "",
                I_AIRLINE_PREFIX = "",
                I_USABLE_FLAG = "Y"
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

    // ULD 목록 로드
    fun getULDList() {
        val date = fltDate.value?.replace("-", "") ?: ""

        if (!Util.validYYYYMMDD(date)) {
            uldList.value = emptyList()
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // 편번 선택된 경우 해당 편번의 ULD 조회
            val flightNo = if (flightIndex.value!! >= 0 && flightIndex.value!! < flightList.size) {
                flightList[flightIndex.value!!].row["FLIGHT_NO"] ?: ""
            } else {
                ""
            }

            val ret = api.getPWM_OPERATION_ULD_L010_001(
                date,
                flightNo,
                origin.value ?: "",
                ""
            )

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { list ->
                            uldList.value = list
                        } ?: run {
                            uldList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // ULD 등록
    fun saveClick() {
        // 유효성 검사
        if (uldTypeCode.value.isNullOrEmpty()) {
            Common.sendError("ULD 타입을 선택하십시오.")
            return
        }

        if (uldSerialNo.value.isNullOrEmpty()) {
            Common.sendError("시리얼 번호를 입력하십시오.")
            return
        }

        if (carrierCode.value.isNullOrEmpty()) {
            Common.sendError("항공사를 선택하십시오.")
            return
        }

        if (scheduleSid.isEmpty()) {
            Common.sendError("편번을 선택하십시오.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // ULD No 구성
            val uldNo = "${uldTypeCode.value}${uldSerialNo.value}${carrierCode.value}"

            val ret = api.setPWM_OPERATION_ULD_M010_011(
                I_OPERATION_ULD_SID = "0",
                I_ULD_NO = uldNo,
                I_CONTOUR_CODE = "",
                I_CONTOUR_NO = "",
                I_SCHEDULE_SID = scheduleSid,
                I_ORIGIN_CODE = origin.value ?: "",
                I_DESTINATION_CODE = "",
                I_LOCATION_CODE = "",
                I_BUP_FLAG = "N",
                I_BULK_FLAG = "N",
                I_THRU_FLAG = "N",
                I_REMARKS = ""
            )

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "등록완료"
                        setClear()
                        getULDList()
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 화면 초기화
    fun setClear() {
        uldSerialNo.value = ""
        origin.value = ""
        uldTypeIndex.value = -1
        carrierIndex.value = -1
    }
}
