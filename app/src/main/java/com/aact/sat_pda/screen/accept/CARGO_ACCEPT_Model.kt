package com.aact.sat_pda.screen.accept

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
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

class CARGO_ACCEPT_Model : ViewModel() {

    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()
    var isEditing = false

    var cargoControlSid = ""
    var acceptSid = ""
    var origin = ""
    var customerCode = ""
    var fltDestSid = ""
    val mawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val ec = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")

    val agentCode = MutableLiveData<String>("")
    val agentName = MutableLiveData<String>("")
    val mfstPcs = MutableLiveData<String>("")
    val mfstWt = MutableLiveData<String>("")
    val goshowSelect = MutableLiveData<String>("N")
    val transSelect = MutableLiveData<String>("N")
    val bupSelect = MutableLiveData<String>("N")

    val ynList = listOf<String>("N","Y")
    val fltList = MutableLiveData<List<DataRow>>(emptyList())
    val fltSelect = MutableLiveData<String>("")
    val fltHeader = listOf<HeaderDTO>(
        HeaderDTO("SCHEDULE_SID", "id"),
        HeaderDTO("FLIGHT_NO", "편번", 140),
        HeaderDTO("TERMINAL_CODE", "터미널", 100)
    )
    val fltModal = SubContent.GetTable(
        headerList = fltHeader,
        bodylist = emptyList(),
        selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
        onClick = { value ->
            fltSelect.value = value["SCHEDULE_SID"] ?: ""
            fltNo.value = value["FLIGHT_NO"] ?: ""
        },
        endClick = {})

    val destHeader = listOf(
        HeaderDTO("CODE_SID", "id"),
        HeaderDTO("CODE_CODE", "Code", 150),
        HeaderDTO("CODE_NAME", "Name", 300)
    )
    val destModal = SubContent.GetTable(
        headerList = destHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to "","CODE_SID" to ""),
        onClick = {
            fltDest.value = it["CODE_CODE"]?:""
            fltDestSid = it["CODE_SID"]?:""
        },
        endClick = {}
    )


    val agencyHeader = listOf(
        HeaderDTO("CUSTOMER_SID", "id"),
        HeaderDTO("CUSTOMER_CODE", "Code", 150),
        HeaderDTO("AGENCY_CODE", "Agency", 130),
        HeaderDTO("CUSTOMER_NAME", "Name", 300)
    )

    val agencyModal = SubContent.GetTable(
        headerList = agencyHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CUSTOMER_CODE" to "","AGENCY_CODE" to "","CUSTOMER_NAME" to ""),
        onClick = {
            customerCode = it["CUSTOMER_CODE"]?:""
            agentCode.value = it["AGENCY_CODE"]?:""
            agentName.value = it["CUSTOMER_NAME"]?:""
        },
        endClick = {}
    )

    fun getMawbInfo(){
        val mawb_temp = mawb.value
        if(mawb_temp.length < 11){
            Common.sendError("MAWB를 확인해주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_ACCEPT_M010_001(acceptSid,mawb_temp)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        val data1 = data[1]
                        if(!data1.isNullOrEmpty()){
                            Common.suc.value = "조회 됬습니다."
                            acceptSid = Util.getTableCell(0,data1,"CARGO_ACCEPT_SID")
                            fltDate.value = Util.getTableCell(0,data1,"FLIGHT_DATE")
                            fltNo.value = Util.getTableCell(0,data1,"FLIGHT_NO")
                            origin = Util.getTableCell(0,data1,"ORIGIN_CODE")
                            fltDest.value = Util.getTableCell(0,data1,"DESTINATION_CODE")
                            mfstPcs.value = Util.getTableCell(0,data1,"MFST_NO_OF_PACKAGE")
                            mfstWt.value = Util.getTableCell(0,data1,"MFST_NET_WEIGHT")
                            goshowSelect.value = Util.getTableCell(0,data1,"GOSHOW_FLAG")
                            transSelect.value = Util.getTableCell(0,data1,"FROM_WAREHOUSE_FLAG")
                            bupSelect.value = Util.getTableCell(0,data1,"BUP_FLAG")
                            ec.value = Util.getTableCell(0,data1,"EC_STATUS_NAME")
                        }

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setSave(){
        val mawb_tmp = mawb.value
        var agentCode_tmp = agentCode.value
        var agentName_tmp = agentName.value
        var fltDest_tmp = fltDest.value

        if(mawb_tmp.length < 11){
            Common.sendError("MAWB를 확인해주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            if(agentCode_tmp.length > 0 && agentName_tmp.length == 0){
                val agentStr = biz.getAgencyInfo(agentCode_tmp)
                if(!agentStr?.row.isNullOrEmpty()){
                    customerCode = agentStr.row["CUSTOMER_CODE"] ?: ""
                    agentName_tmp = agentStr.row["CUSTOMER_NAME"] ?: ""
                }else{
                    Common.sendError("대리점 코드를 확인하십시요")
                    Common.loadingOff()
                    return@launch
                }
            }

            if(fltDest_tmp.length > 0 && fltDestSid.length == 0){

                val destStr = biz.getCommonCode("APORT",fltDest_tmp)
                
                if(destStr == null){
                    return@launch
                }else{
                    if(destStr.isEmpty()){
                        Common.sendError("도착지 코드를 확인하십시요")
                        Common.loadingOff()
                        return@launch
                    }else{
                        fltDest_tmp = Util.getTableCell(0,destStr,"CODE_CODE",fltDest_tmp)
                        fltDestSid = Util.getTableCell(0,destStr,"CODE_NAME",fltDest_tmp)
                    }
                }

            }

            val ret = api.setPWM_CARGO_ACCEPT_M010_011(acceptSid,
                mawb_tmp,
                "",
                fltSelect.value,
                origin,
                fltDest_tmp,
                mfstPcs.value,
                mfstWt.value,
                customerCode,
                agentName_tmp,
                goshowSelect.value,
                transSelect.value,
                bupSelect.value)
            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        cargoControlSid = Util.getTableCell(0,data[0],"COL2")
                        acceptSid = Util.getTableCell(0,data[0],"COL3")

                        val params = listOf<Pair<String,String>>(
                            Pair("CARGO_CONTROL_SID",cargoControlSid),
                            Pair("CARGO_ACCEPT_SID",acceptSid),
                            Pair("MAWB",mawb_tmp)
                        )

                        //스케일로 라우팅

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }




    }

    fun setClear(){
        cargoControlSid = ""
        acceptSid = ""
        origin = ""
        customerCode = ""
        fltDestSid = ""
        fltDate.value = Util.formatDate_15(Common.dateTime.value)
        ec.value = ""
        fltNo.value = ""
        agentCode.value = ""
        mfstPcs.value = ""
        mfstWt.value = ""
        goshowSelect.value = "N"
        transSelect.value = "N"
        bupSelect.value = "N"
    }

}