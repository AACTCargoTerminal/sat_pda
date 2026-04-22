package com.aact.sat_pda.screen.import_bd_with_no

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_BD_WITH_NO_Model: ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()


    var cargoSid = ""

    var cargoNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val assign = MutableLiveData<String>("")
    val status = MutableLiveData<String>("")
    val unloadCode = MutableLiveData<String>("")
    val cargoDes = MutableLiveData<String>("")
    val sumPcs = MutableLiveData<String>("")
    val sumWt = MutableLiveData<String>("")
    val sumMfcsPcs = MutableLiveData<String>("")
    val sumMfcsWt = MutableLiveData<String>("")
    val scheduleSid = MutableLiveData<String>("")

    val pcs = MutableLiveData<String>("")
    val wt = MutableLiveData<String>("")
    val location = MutableLiveData<String>("")
    val cargoBreakdownSid = MutableLiveData<String>("")
    val wtSum = MutableLiveData<String>("")
    val wtSumMfcs = MutableLiveData<String>("")
    val locationList = MutableLiveData<List<String>>()


    fun setInfo() {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_CONTROL_M020_002_BY_NO(cargoNo.value ?: "")

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0,data[0], "COL1") == "OK") {

                        Common.suc.value = "조회 완료"

                        cargoSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        if (Util.getTableCell(0,data[1],"HOUSE_AIR_WAY_BILL_NO") != ""){
                            hawb.value = Util.getTableCell(0,data[1],"HOUSE_AIR_WAY_BILL_NO")
                        }
                        else
                        {
                            hawb.value = "< No HAWB >"
                        }
                        fltDate.value = Util.getTableCell(0,data[1],"FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0,data[1],"FLIGHT_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                    }else {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }

                    val ret2 = api.getPWM_ULD_BREAK_DOWN_M020_005(cargoSid)

                    when(ret2)
                    {
                        is Result.Error -> Common.sendError(ret2.message)
                        is Result.Success<DataTable> -> {
                            val data1 = ret2.data.table
                            if (Util.getTableCell(0,data1[0], "COL1") == "OK")
                            {
                                if (Util.getTableCell(0,data1[0], "COL1") == "OK") {
                                    assign.value = Util.getTableCell(0,data1[1],"ASSIGN_CODE")
                                    unloadCode.value = Util.getTableCell(0,data1[1],"UNLOAD_TYPE_CODE")
                                    cargoDes.value = Util.getTableCell(0,data1[1],"CARGO_DESCRIPTION")
                                    sumPcs.value = Util.getTableCell(0,data1[1],"ACCEPTED_NO_OF_PACKAGE")
                                    sumMfcsPcs.value = Util.getTableCell(0,data1[1],"MFST_NO_OF_PACKAGE")
                                    sumWt.value = Util.getTableCell(0,data1[1],"ACCEPTED_NET_WEIGHT")
                                    sumMfcsWt.value = Util.getTableCell(0,data1[1],"MFST_NET_WEIGHT")
                                    scheduleSid.value = Util.getTableCell(0,data1[1],"SCHEDULE_SID")
                                }else {
                                    Common.sendError(Util.getTableCell(0,data1[0],"COL2"))
                                }
                            }
                        }
                    }

                }
            }
            Common.loadingOff()
        }
    }

    fun getLocationList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val locations = biz.getPositionListIMPC()

            locations?.let { dataRows ->
                val locationCodes = dataRows.map { row ->
                    row.row["CODE_CODE"]?.toString() ?: ""
                }.filter { it.isNotEmpty() }

                locationList.value = locationCodes
            }?: run {
                locationList.value = emptyList()
            }

            Common.loadingOff()
        }
    }


    fun setBreakDown() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_ULD_BREAK_DOWN_M020_013(
                cargoBreakdownSid.value?:"",
                scheduleSid.value?: "",
                "",
                cargoSid,
                pcs.value ?:"",
                wt.value?:"",
                location.value?:""
            )

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장 완료"
                        //재조회
                        setInfo()
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()
        }
    }

    fun calculateWeight() {
        val pcsValue = pcs.value?.toDoubleOrNull()?: 0.0
        val sumPcsValue = sumPcs.value?.toDoubleOrNull()?:0.0
        val sumMfcsPcsValue = sumMfcsPcs.value?.toDoubleOrNull() ?: 0.0
        val sumMfcsWtValue = sumMfcsWt.value?.toDoubleOrNull()?: 0.0
        val sumWtValue = sumWt.value?.toDoubleOrNull() ?: 0.0
        if (pcsValue + sumPcsValue == sumMfcsPcsValue) {
            //수량이 같은 경우 - 남은 중량
            val calculatedWt = sumMfcsWtValue - sumWtValue
            wt.value = String.format("%.1f", calculatedWt)
        } else {
            //비율로 계산
            val calculatedWt = if (sumMfcsPcsValue > 0) {
                sumMfcsWtValue * (pcsValue / sumMfcsPcsValue)
            } else {
                0.0
            }
            wt.value = String.format("%.1f", calculatedWt)
        }
    }


    fun setClear() {
        cargoNo.value = ""
        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        fltDate.value = ""
        fltNo.value = ""
        assign.value = ""
        status.value = ""
        unloadCode.value = ""
        cargoDes.value = ""
        sumPcs.value = ""
        sumWt.value = ""
        sumMfcsPcs.value = ""
        sumMfcsWt.value = ""
        scheduleSid.value = ""
        pcs.value = ""
        wt.value = ""
        location.value = ""
        cargoBreakdownSid.value = ""
        wtSum.value = ""
        wtSumMfcs.value = ""
    }


}