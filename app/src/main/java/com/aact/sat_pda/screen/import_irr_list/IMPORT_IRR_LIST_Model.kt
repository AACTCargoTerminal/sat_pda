package com.aact.sat_pda.screen.import_irr_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_IRR_LIST_Model : ViewModel() {

    val api = MainAPI_Impl()

    var cargoSid = ""

    val cargoNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val assign = MutableLiveData<String>("")
    val status = MutableLiveData<String>("")
    val mfcsPcs =MutableLiveData<String>("")
    val mfcsWt = MutableLiveData<String>("")

    val irrList = MutableLiveData<List<DataRow>>(emptyList())
    val irrHeader = listOf<HeaderDTO>(
        HeaderDTO("HOLD_TYPE_CODE", "id"),
        HeaderDTO("HOLD_FLAG", "보류", 100),
        HeaderDTO("HOLD_TIME", "보류 일시", 250),
        HeaderDTO("HOLD_REMARKS", "내용", 180)
    )
    var irrSelect = mutableMapOf("HOLD_TYPE_CODE" to "")

    fun setInfo(){
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_CONTROL_M020_002_011_BY_NO(cargoNo.value)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                        Common.suc.value = "조회 완료"
                        setClear()

                        cargoSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0,data[1],"HOUSE_AIR_WAY_BILL_NO")
                        fltDate.value = Util.getTableCell(0,data[1],"FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0,data[1],"FLIGHT_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                        status.value = Util.getTableCell(0,data[1],"CARGO_STATUS_NAME")
                        mfcsPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        mfcsWt.value = Util.getTableCell(0,data[1],"MFST_NET_WEIGHT")

                        data[2]?.let { table->
                            irrList.value = table
                        }?:run {
                            irrList.value = emptyList()
                        }


                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setClear(){
        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        fltDate.value = ""
        fltNo.value = ""
        assign.value = ""
        status.value = ""
        mfcsPcs.value = ""
        mfcsWt.value = ""
    }
}