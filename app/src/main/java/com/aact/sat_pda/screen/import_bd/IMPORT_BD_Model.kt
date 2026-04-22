package com.aact.sat_pda.screen.import_bd

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

class IMPORT_BD_Model: ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()

    // 조회 조건
    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))

    // 콤보박스 목록 (DataRow 리스트)
    val flightList = MutableLiveData<List<DataRow>>(emptyList())
    val uldList = MutableLiveData<List<DataRow>>(emptyList())
    val mawbList = MutableLiveData<List<DataRow>>(emptyList())
    val hawbList = MutableLiveData<List<DataRow>>(emptyList())

    // 선택된 값
    var selectedFlightSid = ""
    var selectedUldSid = ""
    var selectedMawbNo = ""
    var cargoControlSid = ""

    // 상세 정보
    val cargoNo = MutableLiveData<String>("")
    val assignCode = MutableLiveData<String>("")
    val unloadTypeCode = MutableLiveData<String>("")
    val cargoDescription = MutableLiveData<String>("")
    val sumPcs = MutableLiveData<String>("")
    val sumPcsMfst = MutableLiveData<String>("")
    val sumWt = MutableLiveData<String>("")
    val sumWtMfst = MutableLiveData<String>("")

    // 입력 값
    val pcs = MutableLiveData<String>("")
    val wt = MutableLiveData<String>("")
    val cargoBreakdownSid = MutableLiveData<String>("")


    // 항공편 목록 조회
    fun getFlightList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_001(fltDate.value ?: "")

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let {
                            flightList.value = it
                            selectedFlightSid = it[0].row["SCHEDULE_SID"]?.toString() ?: ""
                            Common.suc.value = "항공편 조회 완료"
                        } ?: run {
                            flightList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    // ULD 목록 조회
    fun getULDList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_002(selectedFlightSid)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let {
                            uldList.value = it
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

    // MAWB 목록 조회
    fun getMAWBList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_003(selectedFlightSid)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let {
                            mawbList.value = it
                            selectedMawbNo = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL")
                        } ?: run {
                            mawbList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    // HAWB 목록 조회
    fun getHAWBList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_004(selectedMawbNo)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let {
                            hawbList.value = it
                        } ?: run {
                            hawbList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    // 상세정보 조회
    fun getInfo() {
        if (cargoControlSid.isEmpty()) return

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            setClear()

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_005(cargoControlSid)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        cargoNo.value = Util.getTableCell(0, data[1], "CARGO_CONTROL_NO")
                        assignCode.value = Util.getTableCell(0, data[1], "ASSIGN_CODE")
                        unloadTypeCode.value = Util.getTableCell(0, data[1], "UNLOAD_TYPE_CODE")
                        cargoDescription.value = Util.getTableCell(0, data[1], "CARGO_DESCRIPTION")
                        sumPcs.value = Util.getTableCell(0, data[1], "ACCEPTED_NO_OF_PACKAGE")
                        sumPcsMfst.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
                        sumWt.value = Util.getTableCell(0, data[1], "ACCEPTED_NET_WEIGHT")
                        sumWtMfst.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")

                        // 기본값 설정
                        pcs.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
                        wt.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")

                        Common.suc.value = "조회 완료"
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    // 저장
    fun save() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_ULD_BREAK_DOWN_M020_011(
                cargoBreakdownSid.value ?: "",
                selectedFlightSid,
                selectedUldSid,
                cargoControlSid,
                pcs.value ?: "",
                wt.value ?: ""
            )

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장 완료"
                        // 재조회
                        getInfo()
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    private fun setClear() {
        cargoNo.value = ""
        assignCode.value = ""
        unloadTypeCode.value = ""
        cargoDescription.value = ""
        sumPcs.value = ""
        sumPcsMfst.value = ""
        sumWt.value = ""
        sumWtMfst.value = ""
        pcs.value = ""
        wt.value = ""
    }
}
