package com.aact.sat_pda.screen.import_irr

import androidx.compose.ui.graphics.Path
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.collections.find

class IMPORT_IRR_Model : ViewModel() {

    val biz = BizAPI()
    val api = MainAPI_Impl()

    var cargoSid = ""
    var holdTypeCode = ""

    val cargoNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")
    val assign = MutableLiveData<String>("")
    val mfcsPcs = MutableLiveData<String>("")
    val mfcsWt = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val wt = MutableLiveData<String>("")
    val holdType = MutableLiveData<String>("")
    val remark = MutableLiveData<String>("")
    val irrPcs = MutableLiveData<String>("")
    val holdDate = MutableLiveData<String>("")
    val holdTime = MutableLiveData<String>("")

    var typeList: List<DataRow> = emptyList()
    val modalTable = SubContent.GetTable(
        headerList = Header().commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_SID" to "", "CODE_CODE" to "", "CODE_NAME" to ""),
        onClick = { value ->
            holdType.value = value["CODE_NAME"] ?: ""
            holdTypeCode = value["CODE_CODE"] ?: ""
        },
        endClick = {})

    val ynList = listOf<String>("N", "Y")
    val spFlag = MutableLiveData<String>("N")

    fun getLoad() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getCommonCode("IRTYP", "")

            if (ret == null) {
                typeList = emptyList()
            } else {
                typeList = ret
            }

            if (cargoNo.value.isNotEmpty()) {
                setInfo()
            }

            Common.loadingOff()
        }
    }

    suspend fun setInfo() {
        val ret = api.getPWM_CARGO_HOLD_M010_001(cargoNo.value, holdTypeCode)

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                setCls()
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    Common.suc.value = "조회완료"

                    mawb.value = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO")
                    hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")
                    assign.value = Util.getTableCell(0, data[1], "ASSIGN_CODE")
                    mfcsPcs.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
                    mfcsWt.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")
                    pcs.value = Util.getTableCell(0, data[1], "NO_OF_PACKAGE")
                    wt.value = Util.getTableCell(0, data[1], "NET_WEIGHT")
                    holdTypeCode = Util.getTableCell(0, data[1], "HOLD_TYPE_CODE")
                    holdDate.value = Util.getTableCell(0, data[1], "HOLD_DATE")
                    holdTime.value = Util.getTableCell(0, data[1], "HOLD_TIME")

                    if (holdTypeCode.isNotEmpty()) {
                        remark.value = Util.getTableCell(0, data[1], "HOLD_REMARKS")
                    }

                    val findObj = typeList.find { rows -> rows.row["CODE_CODE"] == holdTypeCode }
                    if (findObj != null) {
                        holdType.value = findObj.row["CODE_NAME"] ?: ""
                    }

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }
    }

    fun setCls() {
        mawb.value = ""
        hawb.value = ""
        assign.value = ""
        mfcsPcs.value = ""
        mfcsWt.value = ""
        pcs.value = ""
        wt.value = ""
        holdType.value = ""
        remark.value = ""

    }

    fun saveClick() {

        if (pcs.value.isEmpty() || wt.value.isEmpty()) {
            Common.sendError("PCS, WT를 확인하십시요.")
            return
        }

        if (holdTypeCode == "6" && irrPcs.value.isEmpty()) {
            Common.sendError("DMGD / 이례수량 필수입력")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val time = (holdTime.value + "000000").substring(0, 6)

            val ret = api.setPWM_CARGO_HOLD_M010_011(
                cargoSid,
                holdTypeCode,
                Common.userId.value,
                holdDate.value,
                time,
                remark.value,
                pcs.value,
                wt.value,
                spFlag.value
            )

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                        if(holdTypeCode == "6"){
                            val ret2 = api.setPWM_CARGO_HOLD_M010_011A(cargoSid,irrPcs.value)
                            when(ret2){
                                is Result.Error -> Common.sendError(ret2.message)
                                else -> null
                            }
                        }
                        Common.suc.value = "저장완료"
                        Common.getStack()

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }
}