package com.aact.sat_pda.screen.import_bd_list

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
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_BD_LIST_Model : ViewModel() {
    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()

    val fltNo = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val fltList = MutableLiveData<List<DataRow>>(emptyList())

    val cargoList = MutableLiveData<List<DataRow>>(emptyList())
    var cargoSelect = mutableMapOf("CARGO_CONTROL_NO" to "")

    var scheduleSid = ""
    var terminalCode = ""


    var cargoNo = MutableLiveData<String>("")
    val statusName = MutableLiveData<String>("")
    val mfstPcs = MutableLiveData<String>("")
    val acceptPcs = MutableLiveData<String>("")
    val stockPcs = MutableLiveData<String>("")
    val releasePcs = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val assign = MutableLiveData<String>("")


    val cargoListHeader = listOf(
        HeaderDTO("CARGO_CONTROL_NO","화물번호", 150),
        HeaderDTO("STATUS_NAME","상태", 100),
        HeaderDTO("MFST_NO_OF_PACKAGE","예약수량", 100),
        HeaderDTO("ACCEPTED_NO_OF_PACKAGE","접수수량", 100),
        HeaderDTO("STOCK_NO_OF_PACKAGE","재고수량", 100),
        HeaderDTO("RELEASED_NO_OF_PACKAGE","출고수량", 100),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO","MAWB", 200),
        HeaderDTO("HOUSE_AIR_WAY_BILL_NO","HAWB", 200),
        HeaderDTO("ASSIGN_NAME","할당", 100)
    )



    fun setFlight() {
        val flt_date = fltDate.value ?: ""

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
                            fltDate.value = Util.getTableCell(0,data1,"FLIGHT_DATE")
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

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_OPERATION_L020_061(scheduleSid,"Y")

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0,data[0],"COL1")=="OK") {

                        Common.suc.value = "조회 완료."

                        cargoNo.value = Util.getTableCell(0,data[1],"CARGO_CONTROL_NO")
                        statusName.value = Util.getTableCell(0,data[1],"STATUS_NAME")
                        mfstPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        acceptPcs.value = Util.getTableCell(0,data[1],"ACCEPTED_NO_OF_PACKAGE")
                        stockPcs.value= Util.getTableCell(0,data[1],"STOCK_NO_OF_PACKAGE")
                        releasePcs.value = Util.getTableCell(0,data[1],"RELEASED_NO_OF_PACKAGE")
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")

                        data[1]?.let {table ->
                            cargoList.value = table
                        }?:run {
                            cargoList.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }
}