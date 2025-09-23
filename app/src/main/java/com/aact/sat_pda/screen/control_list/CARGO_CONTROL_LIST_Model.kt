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

    fun selectClick(fltDate:String,fltNo:String, mawb: String) {
        val api: MainAPI = MainAPI_Impl()

        viewModelScope.launch {

            Common.loadingOn(coroutineContext[Job])

            var date = ""
            var sid = ""

            if(mawb.length != 11){
                if(fltNo.length < 4 || fltSelect == ""){
                    Common.sendError("편번을 확인해주세요")
                    Common.loadingOff()
                    return@launch
                }
                date = fltDate
                sid = fltSelect
            }

            val ret = api.getPWM_CARGO_OPERATION_L010_001(I_FLIGHT_DATE = date, I_MASTER_AIR_WAY_BILL_NO = mawb, I_SCHEDULE_SID = sid)
            when (ret) {
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    data[0]?.let { status ->

                        if(Util.getTableCell(0,status,"COL1")  == "OK"){
                            Common.suc.value = "조회가 완료됬습니다."
                            bodyList.value = data[1]?:emptyList()
                            Common.loadingOff()
                            return@launch
                        }else{
                            Common.sendError(Util.getTableCell(0,status,"COL2","조회에러"))
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
}