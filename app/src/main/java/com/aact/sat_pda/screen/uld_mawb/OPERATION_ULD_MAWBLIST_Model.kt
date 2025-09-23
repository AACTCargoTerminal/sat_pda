package com.aact.sat_pda.screen.uld_mawb

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_MAWBLIST_Model : ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()

    var operationSid = ""
    val uldNo = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")

    val headerList = listOf(
        HeaderDTO("CARGO_CONTROL_SID","id"),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO","MAWB",180),
        HeaderDTO("NO_OF_PACKAGE","수량",100),
        HeaderDTO("NET_WEIGHT","중량",150),
    )

    var workSelect = mapOf("CARGO_CONTROL_SID" to "")

    suspend fun setList() : List<DataRow>{
        val ret = api.getPWM_ULD_BUILD_UP_M010_004(operationSid)

        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                    Common.suc.value = "조회 완료"
                    data[1]?.let { table->
                        return table
                    }

                }else{
                    Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                }
            }
        }

        return emptyList()

    }

    fun setDelete(outParam:(flag: Boolean)-> Unit){
        val sid = workSelect["CARGO_CONTROL_SID"] ?: ""

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_ULD_BUILD_UP_M010_021(operationSid,sid,"Y")

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        Common.suc.value = "삭제완료"
                        outParam(true)
                        Common.loadingOff()
                        return@launch
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            outParam(false)
            Common.loadingOff()
        }

    }
}