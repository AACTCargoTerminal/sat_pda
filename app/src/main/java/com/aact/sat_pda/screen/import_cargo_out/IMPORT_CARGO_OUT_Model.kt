package com.aact.sat_pda.screen.import_cargo_out

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_CARGO_OUT_Model : ViewModel() {
    val api = MainAPI_Impl()
    val biz = BizAPI()

    data class LocationItem(
        val locationCode: String,
        val locationName: String,
        val rackNo: String,
        val stockPcs: String,
        val outPcs: MutableLiveData<String> = MutableLiveData("0")
    )

    var cargoSid = ""
    val cargoNo = MutableLiveData("")
    val mawb = MutableLiveData("")
    val hawb = MutableLiveData("")
    val fltDate = MutableLiveData("")
    val fltNo = MutableLiveData("")
    val status = MutableLiveData("")
    val stockWt = MutableLiveData("")
    val releaseDate = MutableLiveData("")
    val releaseTime = MutableLiveData("")
    val customsId = MutableLiveData("")
    val approveDate = MutableLiveData("")
    val outWt = MutableLiveData("")
    val outPcs = MutableLiveData("0")

    // InOutKind 콤보박스
    var inOutKindList: List<DataRow> = emptyList()
    val inOutKindCode = MutableLiveData("")
    val inOutKindIndex = MutableLiveData(-1)
    val inOutkindNameList = MutableLiveData<List<String>>(emptyList())

    // 위치별 출고 수량 리스트
    val locationList = MutableLiveData<MutableList<LocationItem>>(mutableListOf())

    // 공통코드 BW015 (반출유형) 로딩
    fun getInOutKindList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getCommonCode("BW015", "")
            if (ret != null) {
                inOutKindList = ret
                inOutkindNameList.value = ret.map { it.row["CODE_NAME"] ?: "" }
            } else {
                inOutkindNameList.value = emptyList()
            }

            Common.loadingOff()
        }
    }

    // 화물번호로 정보 조회 (setInfo)
    fun setInfoByNo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_RELEASE_M020_001_BY_NO(cargoNo.value)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "조회 완료"

                        // 화물 정보 설정
                        cargoSid = Util.getTableCell(0, data[1], "CARGO_CONTROL_SID")
                        mawb.value = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")
                        fltDate.value = Util.getTableCell(0, data[1], "FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0, data[1], "FLIGHT_NO")
                        status.value = Util.getTableCell(0, data[1], "CARGO_STATUS_NAME")
                        stockWt.value = Util.getTableCell(0, data[1], "STOCK_NET_WEIGHT")

                        // 현재 일시 설정
                        val curDateTime = biz.getCurDateTime()
                        releaseDate.value = curDateTime.substring(0, 8)

                        // 시분초를 HH:MM:SS 형식으로 변환
                        val timeStr = curDateTime.substring(8, 14)
                        releaseTime.value = "${timeStr.substring(0, 2)}:${timeStr.substring(2, 4)}:${timeStr.substring(4, 6)}"

                        approveDate.value = curDateTime.substring(0, 8)

                        // 위치 리스트 로딩
                        loadLocationList()

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 위치별 재고 리스트 로딩
    private suspend fun loadLocationList() {
        val ret = api.getPWM_CARGO_RELEASE_M020_002_BY_NO(cargoNo.value)

        when(ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table

                // 결과 상태 확인
                val resultStatus = Util.getTableCell(0, data[0], "COL1")
                if (resultStatus != "OK") {
                    val errorMsg = Util.getTableCell(0, data[0], "COL2")
                    if (errorMsg.isNotEmpty()) {
                        Common.sendError(errorMsg)
                    }
                    return
                }

                if (data[1] != null && data[1]!!.isNotEmpty()) {
                    val tempList = mutableListOf<LocationItem>()

                    data[1]?.forEach{ row ->
                        val locationCode = row.row["LOCATION_CODE"] ?: ""
                        val locationName = row.row["LOCATION_NAME"] ?: ""
                        val rackNo = row.row["RACK_NO"] ?: ""
                        val stockNoOfPackage = row.row["STOCK_NO_OF_PACKAGE"] ?: "0"
                        val noOfPackage = row.row["NO_OF_PACKAGE"] ?: "0"

                        val item = LocationItem(
                            locationCode = locationCode,
                            locationName = locationName,
                            rackNo = rackNo,
                            stockPcs = stockNoOfPackage,
                            outPcs = MutableLiveData(noOfPackage)
                        )

                        tempList.add(item)
                    }

                    locationList.value = tempList

                    // 초기 합계 계산
                    calculateTotalOutPcs()
                } else {
                    // 위치 리스트가 비어있어도 에러가 아님 - 빈 리스트로 처리
                    locationList.value = mutableListOf()
                }
            }
        }
    }


    // 출고 수량 합계 자동 계산
    fun calculateTotalOutPcs() {
        var total = 0
        locationList.value?.forEach{ item ->
            val pcs = item.outPcs.value?.toIntOrNull() ?: 0
            total += pcs
        }
        outPcs.value = total.toString()
    }

    // 저장
    fun saveClick() {
        //유효성 검사
        if (inOutKindIndex.value == -1) {
            Common.sendError("반출유형을 선택하십시오.")
            return
        }

        if (cargoSid.isEmpty()) {
            Common.sendError("화물 정보를 조회하십시오.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            // 그리드 데이터 공백 구분 문자열로 변환
            val gridLocationCode = locationList.value?.joinToString(" ") { it.locationCode } ?: ""
            val gridNoOfPackage = locationList.value?.joinToString(" ") { it.outPcs.value ?: "0" } ?: ""

            // releaseTime 6자리로 변환 (HH:MM:SS → HHMMSS)
            val releaseTimeFormatted = ((releaseTime.value?.replace(":", "") ?: "") + "000000").substring(0, 6)

            val ret = api.setPWM_CARGO_RELEASE_M020_011(
                cargoSid,
                inOutKindCode.value ?: "",
                releaseDate.value ?: "",
                releaseTimeFormatted,
                customsId.value ?: "",
                approveDate.value?.trim() ?: "",
                outPcs.value?.trim() ?: "0",
                outWt.value ?: "",
                gridLocationCode,
                gridNoOfPackage
            )

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장완료"

                        // 화면 초기화
                        setClear()
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
        cargoSid = ""
        cargoNo.value = ""
        mawb.value = ""
        hawb.value = ""
        fltDate.value = ""
        fltNo.value = ""
        status.value = ""
        stockWt.value = ""

        releaseDate.value = ""
        releaseTime.value = ""
        customsId.value = ""
        approveDate.value = ""
        outPcs.value = "0"
        outWt.value = ""

        inOutKindCode.value = ""
        inOutKindIndex.value = -1

        locationList.value?.clear()
        locationList.value = mutableListOf()
    }



}