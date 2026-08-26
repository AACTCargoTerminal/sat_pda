package com.aact.sat_pda.screen.location

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result

class CARGO_LOCATION_Model : ViewModel() {

    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()
    var cargoControlSid = ""

    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val positionStr = MutableLiveData<String>("")
    val rackNo = MutableLiveData<String>("")
    val locationList = MutableLiveData<List<DataRow>>(emptyList())


    val position = MutableLiveData<Pair<String, String>>()
    val locationHeader = listOf<HeaderDTO>(
        HeaderDTO("LOCATION_CODE", "id"),
        HeaderDTO("LOCATION_NAME", "LOCATION", 150),
        HeaderDTO("RACK_NO", "RACK", 130),
        HeaderDTO("NO_OF_PACKAGE", "수량", 80),
        HeaderDTO("ULD_FLAG", "ULD", 150)
    )

    val positionHeader = listOf(
        HeaderDTO("CODE_CODE", "Code", 150),
        HeaderDTO("CODE_NAME", "Name", 250)
    )

    val modalTable = SubContent.GetTable(
        headerList = positionHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to "", "CODE_NAME" to ""),
        onClick = { value ->
            position.value = Pair(value["CODE_CODE"] ?: "", value["CODE_NAME"] ?: "")
        },
        endClick = {})

    var selectLocation =
        mapOf("LOCATION_CODE" to "", "RACK_NO" to "", "NO_OF_PACKAGE" to "", "LOCATION_NAME" to "")

    val positionList = MutableLiveData<List<DataRow>>(emptyList())
    val rackList = MutableLiveData<List<String>>(emptyList())

    fun startProc() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val position = biz.getCommonCode("WHLOC", "")

            position?.let {
                positionList.postValue(it)
            }

            val rack = biz.getCommonCode("RCKNO", "")

            rack?.let {
                rackList.postValue(
                    listOf("") + it.mapNotNull { item -> item.row["CODE_CODE"] }
                )
            }

            val ret = api.getPWM_CARGO_LOCATION_M010_001_002(cargoControlSid)

            when (ret) {
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        mawb.postValue(Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO"))
                        hawb.postValue(Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO"))
                        fltDate.postValue(Util.getTableCell(0, data[1], "FLIGHT_DATE"))
                        fltNo.postValue(Util.getTableCell(0, data[1], "FLIGHT_NO"))

                        data[2]?.let {
                            locationList.postValue(it)
                        } ?: run {
                            locationList.postValue(emptyList())
                        }
                        Common.suc.value = "조회가 완료됐습니다."
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2", "이동 조회 에러"))
                    }

                }

                is Result.Error -> {
                    Common.sendError(ret.message)
                }
            }

            Common.loadingOff()
        }


    }

    fun saveProc(pcsParam:String,outParam:(suc:String)-> Unit) {

        val positionCode = position.value?.first ?: ""

        if (positionCode == "" || rackNo.value == "") {
            Common.sendError("입고위치, RackNo 지정하지 않았습니다.")
            return
        }

        val selectLocList = selectLocation["LOCATION_CODE"]

        if (selectLocList == "") {
            Common.sendError("선택한 항목이 없습니다.")
            return
        }
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val selectPosition = selectLocation["LOCATION_CODE"] ?: ""

            val time = biz.getCurDateTime()

            val ret = api.setPWM_CARGO_LOCATION_M010_011(
                cargoControlSid,
                time.substring(0, 8),
                time.substring(8, 12),
                selectPosition,
                "N",
                positionCode,
                "N",
                rackNo.value,
                pcsParam
            )

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {

                    val data = ret.data.table

                    if(Util.getTableCell(0,data[0],"COL1") == "OK"){
                        selectLocation =
                            mapOf("LOCATION_CODE" to "", "RACK_NO" to "", "NO_OF_PACKAGE" to "", "LOCATION_NAME" to "")
                        position.postValue(Pair("",""))
                        rackNo.postValue("")
                        pcs.postValue("")
                        Common.suc.value = "저장이 완료됐습니다."
                        outParam("Y")
                        return@launch
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                        outParam("N")
                    }


                }
            }

            Common.loadingOff()
        }
    }
}