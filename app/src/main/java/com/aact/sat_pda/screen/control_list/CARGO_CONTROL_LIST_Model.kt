package com.aact.sat_pda.screen.control_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result

class CARGO_CONTROL_LIST_Model : ViewModel() {
    val headerList = listOf<HeaderDTO>(
        HeaderDTO("CARGO_CONTROL_SID", "id"),
        HeaderDTO("FLIGHT_NO", "편번", 140),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 250),
        HeaderDTO("STATUS_NAME", "상태", 140),
        HeaderDTO(
            "PROCESS_RAT" +
                    "IO", "%", 80
        ),
        HeaderDTO("MFST_NO_OF_PACKAGE", "예약", 80),
        HeaderDTO("ACCEPTED_NO_OF_PACKAGE", "접수", 80),
        HeaderDTO("STOCK_NO_OF_PACKAGE", "입고", 80),
        HeaderDTO("WORK_NO_OF_PACKAGE", "작업", 80),
        HeaderDTO("RELEASED_NO_OF_PACKAGE", "출고", 80)
    )
    val bodyList = MutableLiveData<List<DataRow>>(emptyList())
    var selectList = mapOf("CARGO_CONTROL_SID" to "","MASTER_AIR_WAY_BILL_NO" to "")

    val fltDate = MutableLiveData<String>("")
    val fltBodyList = MutableLiveData<List<DataRow>>(emptyList())
    var fltSelect = ""
    val fltNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")

    var saveFltDate = ""
    var saveFltNo = ""
    var saveScheduleSid = ""
    var settingRouteParams = false
    var returnFromControl = false
    var isEditing = false

    fun selectClick(fltDate: String, mawb: String,) {
        val api: MainAPI = MainAPI_Impl()

        viewModelScope.launch {

            Common.loadingOn(coroutineContext[Job])

            var date = fltDate
            var sid = fltSelect

            if(mawb.length == 11) {
                date = ""
                sid = ""
            }

            val ret = api.getPWM_CARGO_OPERATION_L010_001(I_FLIGHT_DATE = date, I_MASTER_AIR_WAY_BILL_NO = mawb, I_SCHEDULE_SID = sid)
            when (ret) {
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    data[0]?.let { status ->

                        if(Util.getTableCell(0, status, "COL1") == "OK") {
                            Common.suc.value = "조회가 완료됐습니다."

                            val list = data[1] ?: emptyList()

                            if(list.isNotEmpty()){
                                selectList = mapOf(
                                    "CARGO_CONTROL_SID" to (list[0].row["CARGO_CONTROL_SID"] ?: ""),
                                    "MASTER_AIR_WAY_BILL_NO" to (list[0].row["MASTER_AIR_WAY_BILL_NO"] ?: "")
                                )
                            }else{
                                selectList = mapOf(
                                    "CARGO_CONTROL_SID" to "",
                                    "MASTER_AIR_WAY_BILL_NO" to ""
                                )
                            }

                            bodyList.value = list
                            Common.loadingOff()
                            return@launch
                        }else{
                            Common.sendError(Util.getTableCell(0, status, "COL2", "조회에러"))
                        }

                    } ?: run {
                        Common.sendError("서버 에러")
                    }
                    bodyList.value = emptyList()
                }

                is Result.Error -> throw Exception(ret.message)
            }

            Common.loadingOff()
        }
    }

    fun damageClick(
        onSuccess: (cargoControlSid: String, damageExportSid: String) -> Unit
    ) {
        val sid = selectList["CARGO_CONTROL_SID"] ?: ""

        if(sid.isBlank()) {
            Common.sendError("선택한 화물이 없습니다.")
            return
        }

        val sql = StringBuilder().apply {
            append("SELECT TCM.CARGO_CONTROL_SID, NVL(DEM.DAMAGE_EXPORT_SID,0) DAMAGE_EXPORT_SID ")
            append("FROM TWM_CARGO_CONTROL_MASTER TCM ")
            append("JOIN TWM_CARGO_INOUT_MASTER CIM ")
            append("ON CIM.CARGO_CONTROL_SID = TCM.CARGO_CONTROL_SID ")
            append("AND CIM.INOUT_FLAG = 'I' ")
            append("LEFT JOIN TWM_CARGO_DAMAGE_EXPORT_MASTER DEM ")
            append("ON DEM.CARGO_CONTROL_SID = TCM.CARGO_CONTROL_SID ")
            append("AND DEM.USABLE_FLAG = 'Y' ")
            append("WHERE TCM.CARGO_CONTROL_SID = ${sid} ")
            append("AND TCM.USABLE_FLAG = 'Y' ")
            append("AND TCM.INOUT_FLAG = 'E'")
        }.toString()

        val api: MainAPI = MainAPI_Impl()

        viewModelScope.launch {

            Common.loadingOn(coroutineContext[Job])

            val ret = api.getSqlDataSet(sql)

            when(ret) {
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    data[0]?.let { status ->

                        if(Util.getTableCell(0, status, "COL1") == "OK"){

                            val list = data[1] ?: emptyList()

                            if(list.isNotEmpty()){

                                val cargoControlSid = list[0].row["CARGO_CONTROL_SID"] ?: ""

                                val damageExportSid = list[0].row["DAMAGE_EXPORT_SID"] ?: "0"

                                Common.loadingOff()

                                onSuccess(cargoControlSid, damageExportSid)

                                return@launch

                            }else{
                                Common.sendError("화물 입고 상태를 확인해주세요")
                            }

                        }else{
                            Common.sendError(Util.getTableCell(0, status, "COL2", "조회에러"))
                        }
                    } ?: run{
                        Common.sendError("서버 에러")
                    }
                }
                is Result.Error -> throw Exception(ret.message)
            }

            Common.loadingOff()
        }
    }
}