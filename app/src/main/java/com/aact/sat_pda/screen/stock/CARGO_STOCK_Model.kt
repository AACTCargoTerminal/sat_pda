package com.aact.sat_pda.screen.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result

class CARGO_STOCK_Model : ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()
    var acceptSid = ""
    var acceptSeq = ""
    var autoFlag = ""

    val positionList = MutableLiveData<List<DataRow>>(emptyList())

    val rackList = MutableLiveData<List<String>>(emptyList())
    val spcFlagList = listOf("N", "Y")

    val acceptDate = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val netWt = MutableLiveData<String>("")
    val spc = MutableLiveData<String>("")
    val position = MutableLiveData<Pair<String, String>>(Pair("", ""))
    val positionStr = MutableLiveData<String>("")
    val rackNo = MutableLiveData<String>("")
    val spcFlag = MutableLiveData<String>("N")

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

    fun getInfo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val positionRes = async { biz.getPositionListEXP() }

            val postionRet = positionRes.await()

            postionRet?.let {
                positionList.value = it
            }

            val rackRes = async { biz.getCommonCode("RCKNO", "") }

            val rackRet = rackRes.await()
            rackRet?.let {
                rackList.value = listOf("") + it.mapNotNull { item -> item.row["CODE_CODE"] }
            }


            val res = api.getPGetPWM_CARGO_STOCK_M010_001(acceptSid)

            when (res) {
                is Result.Error -> Common.sendError(res.message)
                is Result.Success<DataTable> -> {
                    val data = res.data.table

                    data[0]?.let { status ->

                        if (Util.getTableCell(0, status, "COL1") == "OK") {

                            data[1]?.let { rows ->
                                acceptDate.postValue(Util.getTableCell(0, rows, "ACCEPTED_TIME"))
                                mawb.postValue(Util.getTableCell(0, rows, "MASTER_AIR_WAY_BILL_NO"))
                                hawb.postValue(Util.getTableCell(0, rows, "HOUSE_AIR_WAY_BILL_NO"))
                                fltDate.postValue(Util.getTableCell(0, rows, "FLIGHT_DATE"))
                                fltNo.postValue(Util.getTableCell(0, rows, "FLIGHT_NO"))
                                pcs.postValue(Util.getTableCell(0, rows, "NO_OF_PACKAGE"))
                                netWt.postValue(Util.getTableCell(0, rows, "NET_WEIGHT"))
                                spc.value = Util.getTableCell(0, rows, "SPECIAL_CARGO_CODE")

                                val findPosition = positionList.value?.find {
                                    it.row["CODE_CODE"] == Util.getTableCell(
                                        0,
                                        rows,
                                        "DEFAULT_LOCATION_CODE"
                                    )
                                }

                                findPosition?.row?.let {
                                    position.value = Pair(
                                        it["CODE_CODE"] ?: "",
                                        it["CODE_NAME"] ?: ""
                                    )
                                } ?: run {
                                    position.value = Pair("", "")
                                }

                                val rack = rackList.value?.find { rackRow->rackRow == "WH" }

                                if(rack == null){
                                    rackNo.value = ""
                                }else{
                                    rackNo.value = rack
                                }

                                if(autoFlag == "Y" && spc.value.length == 0){
                                    setCargoIn()
                                }

                            }

                        } else {
                            Common.sendError(Util.getTableCell(0, status, "COL2", "수정에러"))
                        }

                    } ?: run {
                        Common.sendError("서버 에러")
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setCargoIn() {
        val positionObj = position.value
        positionObj?.let {
            if (it.first.length == 0) {
                Common.sendError("입고위치를 지정하지 않았습니다.")
                return
            }
        } ?: run {
            Common.sendError("입고위치를 지정하지 않았습니다.")
            return
        }
        val rackObj = rackNo.value
        rackObj?.let {
            if (it.isEmpty()) {
                Common.sendError("RackNo를 지정하지 않았습니다.")
                return
            }
        } ?: run {
            Common.sendError("RackNo를 지정하지 않았습니다.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val currDT = biz.getCurDateTime()

            val ret = api.setPWM_CARGO_STOCK_M010_011(
                acceptSid,
                currDT.substring(0, 8),
                currDT.substring(8, 12),
                positionObj.first,
                rackObj,
                spcFlag.value
            )

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    data[0]?.let { status ->
                        if (Util.getTableCell(0, status, "COL1") == "OK") {
                            Common.suc.value = "입고되었습니다."
                            Common.getStack()
                        } else {
                            Common.sendError(Util.getTableCell(0, status, "COL2"))
                        }
                    } ?: run {
                        Common.sendError("서버 에러")
                    }
                }
            }

            Common.loadingOff()
        }

    }
}