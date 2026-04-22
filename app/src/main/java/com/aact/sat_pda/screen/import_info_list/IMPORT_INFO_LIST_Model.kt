package com.aact.sat_pda.screen.import_info_list

import androidx.constraintlayout.motion.widget.Debug
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_INFO_LIST_Model : ViewModel() {
    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()

    val cargoNo = MutableLiveData<String>("")
    val cargoSid = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val status = MutableLiveData<String>("")
    val mfstPcs =MutableLiveData<String>("")
    val mfstWt = MutableLiveData<String>("")
    val acceptPcs =MutableLiveData<String>("")
    val acceptWt = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>(Util.validDate(Util.formatDate_15(Common.dateTime.value)))
    val fltNo = MutableLiveData<String>("")
    val fltList = MutableLiveData<List<DataRow>>(emptyList())

    val i00Count = MutableLiveData<String>("")
    val i02Count = MutableLiveData<String>("")
    val i03Count = MutableLiveData<String>("")

    var cargoSelect = mutableMapOf(
        "CARGO_CONTROL_SID" to "",
        "CARGO_CONTROL_NO" to "",
        "STATUS_NAME" to "",
        "STATUS_CODE" to ""
    )
    val cargoList = MutableLiveData<List<DataRow>>(emptyList())

    var scheduleSid = ""
    var terminalCode = ""

    val infoListHeader = listOf(
        HeaderDTO("STATUS_NAME", "상태", 100),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 200),
        HeaderDTO("HOUSE_AIR_WAY_BILL_NO", "HAWB", 200),
        HeaderDTO("MFST_NO_OF_PACKAGE", "예약수량", 80),
        HeaderDTO("MFST_NET_WEIGHT", "예약중량", 80),
        HeaderDTO("ACCEPTED_NO_OF_PACKAGE", "접수수량", 80),
        HeaderDTO("ACCEPTED_NET_WEIGHT", "접수중량", 80),
        HeaderDTO("CARGO_CONTROL_NO", "화물번호", 80),
        HeaderDTO("CARGO_CONTROL_SID", "id", 80)
    )



    fun getFlight() {
        val flt_date = (fltDate.value ?: "").replace("-", "")

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_001(flt_date)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        val data1 = data[1]
                        if(!data1.isNullOrEmpty()){
                            Common.suc.value = "조회 됐습니다."
                            scheduleSid = Util.getTableCell(0,data1,"SCHEDULE_SID")
                            fltNo.value = Util.getTableCell(0,data1,"FLIGHT_NO")
                            // observe 무한 호출 방지로 수정
                            //fltDate.value = Util.getTableCell(0,data1,"FLIGHT_DATE")
                            terminalCode = Util.getTableCell(0,data1,"TERMINAL_CODE")
                            fltList.value = data1 ?: emptyList()
                        }
                    }else {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }


    fun setList() {

        if (scheduleSid.isEmpty()) {
            Common.sendError("항공편을 먼저 선택해주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_OPERATION_L020_063(scheduleSid, "Y")

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        val rows = data[1] ?: emptyList()

                        Common.suc.value = "조회 완료"

                        //요약 카운트 메시지
                        val Count00 = rows.count {
                            val code = it.row["STATUS_CODE"] ?: ""
                            code == "I00" || code == "I01"
                        }
                        val Count02 = rows.count {it.row["STATUS_CODE"] == "I02"}
                        val Count03 = rows.count {it.row["STATUS_CODE"] == "I03"}

                        //요약값 대입
                        i00Count.value = Count00.toString()
                        i02Count.value = Count02.toString()
                        i03Count.value = Count03.toString()


                        data[1]?.let { table->
                            println("cargoList에 ${table.size}개 항목 설정")
                            cargoList.value = table
                        }?:run {
                            println("data[1]이 null - cargoList를 빈 리스트로 설정")
                            cargoList.value = emptyList()
                        }
                    }else {
                        val col2 = Util.getTableCell(0, data[0], "COL2")
                        println("COL1이 OK가 아님. COL2: $col2")
                        Common.sendError(col2)
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setClear(){
        fltNo.value = ""
    }
}