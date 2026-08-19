package com.aact.sat_pda.screen.import_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_INFO_Model : ViewModel() {

    val api  = MainAPI_Impl()

    var cargoSid = ""

    val cargoNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")

    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val assign = MutableLiveData<String>("")
    val mfcsPcs = MutableLiveData<String>("")
    val mfcsWt = MutableLiveData<String>("")
    val inPcs = MutableLiveData<String>("")
    val inWt = MutableLiveData<String>("")
    val outPcs = MutableLiveData<String>("")
    val outWt = MutableLiveData<String>("")
    val desc = MutableLiveData<String>("")
    val status = MutableLiveData<String>("")
    var skipCargoNoObserver = false

    val mawbHeader = listOf(
        HeaderDTO("CARGO_CONTROL_SID", "id"),
        HeaderDTO("HOUSE_AIR_WAY_BILL_NO", "HAWB", 220),
        HeaderDTO("CARGO_CONTROL_NO", "화물번호", 300)
    )

    val modalTable = SubContent.GetTable(
        headerList = mawbHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CARGO_CONTROL_SID" to "", "HOUSE_AIR_WAY_BILL_NO" to "","CARGO_CONTROL_NO" to ""),
        onClick = { value ->
            skipCargoNoObserver = true
            cargoNo.value = value["CARGO_CONTROL_NO"] ?: ""

            setInfo()
        },
        endClick = {})

    fun setInfo(){
        val cargoNoParam = cargoNo.value ?: ""
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_CONTROL_M020_002_BY_NO(cargoNoParam)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    setCls()
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        Common.suc.value = "조회 완료"
                        cargoSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                        skipCargoNoObserver = true
                        cargoNo.value = cargoNoParam
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")
                        fltDate.value = Util.getTableCell(0,data[1],"FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0,data[1],"FLIGHT_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                        mfcsPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        mfcsWt.value = Util.getTableCell(0,data[1],"MFST_NET_WEIGHT")
                        inPcs.value = Util.getTableCell(0,data[1],"ACCEPTED_NO_OF_PACKAGE")
                        inWt.value = Util.getTableCell(0,data[1],"ACCEPTED_NET_WEIGHT")
                        outPcs.value = Util.getTableCell(0,data[1],"RELEASED_NO_OF_PACKAGE")
                        outWt.value = Util.getTableCell(0,data[1],"RELEASED_NET_WEIGHT")
                        desc.value = Util.getTableCell(0,data[1],"CARGO_DESCRIPTION")
                        status.value = Util.getTableCell(0,data[1],"CARGO_STATUS_NAME")
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setHAWBList(outParam:(list:List<DataRow>)-> Unit){
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_ULD_BREAK_DOWN_M020_004(mawb.value)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> ->{
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                        outParam(data[1] ?: emptyList())

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setInfoMawb(){
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_CONTROL_M020_002_BY_MAWB(mawb.value)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    setCls()
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                        Common.suc.value = "조회완료"

                        cargoSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                        skipCargoNoObserver = true
                        cargoNo.value = Util.getTableCell(0,data[1],"CARGO_CONTROL_NO")
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0,data[1],"HOUSE_AIR_WAY_BILL_NO")
                        fltDate.value = Util.getTableCell(0,data[1],"FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0,data[1],"FLIGHT_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                        mfcsPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        mfcsWt.value = Util.getTableCell(0,data[1],"MFST_NET_WEIGHT")
                        inPcs.value = Util.getTableCell(0,data[1],"ACCEPTED_NO_OF_PACKAGE")
                        inWt.value = Util.getTableCell(0,data[1],"ACCEPTED_NET_WEIGHT")
                        outPcs.value = Util.getTableCell(0,data[1],"RELEASED_NO_OF_PACKAGE")
                        outWt.value = Util.getTableCell(0,data[1],"RELEASED_NET_WEIGHT")
                        desc.value = Util.getTableCell(0,data[1],"CARGO_DESCRIPTION")
                        status.value = Util.getTableCell(0,data[1],"CARGO_STATUS_NAME")

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setInfoHawb(){
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_CONTROL_M020_002_BY_HAWB(hawb.value)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    setCls()
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        Common.suc.value = "조회완료"
                        cargoSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                        skipCargoNoObserver = true
                        cargoNo.value = Util.getTableCell(0,data[1],"CARGO_CONTROL_NO")
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        fltDate.value = Util.getTableCell(0,data[1],"FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0,data[1],"FLIGHT_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                        mfcsPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        mfcsWt.value = Util.getTableCell(0,data[1],"MFST_NET_WEIGHT")
                        inPcs.value = Util.getTableCell(0,data[1],"ACCEPTED_NO_OF_PACKAGE")
                        inWt.value = Util.getTableCell(0,data[1],"ACCEPTED_NET_WEIGHT")
                        outPcs.value = Util.getTableCell(0,data[1],"RELEASED_NO_OF_PACKAGE")
                        outWt.value = Util.getTableCell(0,data[1],"RELEASED_NET_WEIGHT")
                        desc.value = Util.getTableCell(0,data[1],"CARGO_DESCRIPTION")
                        status.value = Util.getTableCell(0,data[1],"CARGO_STATUS_NAME")
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setCls(){
        cargoNo.value = ""
        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        fltDate.value = ""
        fltNo.value = ""
        assign.value = ""
        mfcsPcs.value = ""
        mfcsWt.value = ""
        inPcs.value = ""
        inWt.value = ""
        outPcs.value = ""
        outWt.value = ""
        desc.value = ""
        status.value =""
    }
}