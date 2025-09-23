package com.aact.sat_pda.screen.scale_list

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
import kotlinx.coroutines.launch
import kotlin.text.isNullOrEmpty
import com.aact.sat_pda.dto.Result

class CARGO_SCALE_LIST_Model : ViewModel() {
    var isEditing = false

    var cargoControlSid = ""
    var acceptSid = ""
    val mawb = MutableLiveData<String>("")
    val acceptSeq = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val pcsSum = MutableLiveData<String>("")
    val wtSum = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val wt = MutableLiveData<String>("")
    val palletSelect = MutableLiveData<Int>(0)
    val palletPcs = MutableLiveData<String>("1")
    val palletWt = MutableLiveData<String>("")
    val netWt = MutableLiveData<String>("")
    val spcStr = MutableLiveData<String>("")
    val spcSelect = MutableLiveData<String>("")
    val scaleSelect = MutableLiveData<Map<String, String>>(mapOf("SCALE_SEQ" to ""))

    val palletList = MutableLiveData<List<DataRow>>(emptyList())
    val spcList = MutableLiveData<List<DataRow>>(emptyList())
    val scaleList = MutableLiveData<List<DataRow>>(emptyList())
    val spcHeader = listOf(
        HeaderDTO("CODE_CODE", "Code", 120),
        HeaderDTO("CODE_NAME", "Name", 300)
    )
    val spcModal = SubContent.GetTable(
        headerList = spcHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to "", "CODE_NAME" to ""),
        onClick = { value ->
            spcSelect.value = value["CODE_CODE"] ?: ""
            spcStr.value = value["CODE_NAME"] ?: ""
        },
        endClick = {})

    val api = MainAPI_Impl()
    val biz = BizAPI()

    val scaleHeader = listOf<HeaderDTO>(
        HeaderDTO("SCALE_SEQ", "id"),
        HeaderDTO("NO_OF_PACKAGE", "수량", 80),
        HeaderDTO("NET_WEIGHT", "Net Wt", 120),
        HeaderDTO("SCALE_TIME", "계량 시간", 250)

    )

    suspend fun setList() {
        val ret = api.getPWM_CARGO_CONTROL_M010_003(acceptSid)

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    data[1]?.let {
                        scaleList.value = it
                    } ?: run {
                        scaleList.value = emptyList()
                    }

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    fun getScaleSelect(seq: String) {
        isEditing = true

        acceptSeq.value = seq

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_SCALE_M010_001(cargoControlSid, acceptSid, seq)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        pcs.value = Util.getTableCell(0, data[1], "NO_OF_PACKAGE")
                        wt.value = Util.getTableCell(0, data[1], "GROSS_WEIGHT")
                        palletSelect.value = Util.getTableIndex(
                            palletList.value,
                            "VALUE1_NUMBER",
                            Util.getTableCell(0, data[1], "PALLET_CODE")
                        )
                        palletPcs.value = Util.getTableCell(0, data[1], "NO_OF_PALLET")
                        palletWt.value = Util.getTableCell(0, data[1], "TARE_WEIGHT")
                        netWt.value = Util.getTableCell(0, data[1], "NET_WEIGHT")

                        val targetCode = Util.getTableCell(0, data[1], "SPECIAL_CARGO_CODE")

                        val spc_temp = spcList.value?.find {
                            val code = it.row["CODE_CODE"]
                            code != null && code == targetCode
                        }
                        spcSelect.value = targetCode
                        spcStr.value = spc_temp?.row["CODE_NAME"] ?: ""
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            isEditing = false
            Common.loadingOff()
        }

    }

    fun setUpdate() {
        val idx = scaleSelect.value["SCALE_SEQ"]
        if (idx.isNullOrEmpty()) {
            Common.sendError("선택한 항목이 없습니다.")
            return
        }
        val palletCode = palletList.value[palletSelect.value].row["VALUE1_NUMBER"]
        if(palletCode.isNullOrEmpty()){
            Common.sendError("파렛트를 선택하여주세요.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])





            val ret = api.setPWM_CARGO_SCALE_M010_011_UPDATE(
                cargoControlSid,
                acceptSid,
                idx,
                pcs.value,
                wt.value,
                spcSelect.value,
                palletCode,
                palletPcs.value,
                palletWt.value,
                netWt.value,
            )

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        inputClear()
                        setList()
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }

    }

    fun setDelete(seq:String){

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_SCALE_M010_021(acceptSid,seq,"Y")

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        inputClear()
                        setList()

                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }


    }

    fun printClick(){
        var printIp = ""
        when(Common.terminalCode.value){
            "T1" -> printIp = "CARGOACCEPT"
            "T2" -> printIp = "CARGOACCEPT_T2"
            else -> printIp = "CARGOACCEPT"
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_SCALE_PRINT_MAWB(cargoControlSid,printIp)
            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                        //진행
                        Common.suc.value = "출력이 완료됫습니다."
                    }else{
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun inputClear() {
        acceptSeq.value = ""
        scaleSelect.value = mapOf("SCALE_SEQ" to "")
        pcs.value = ""
        wt.value = ""
        palletSelect.value = 0
        palletPcs.value = "1"
        palletWt.value = ""
        netWt.value = ""
    }

    fun setPalletCumpute() {
        if (isEditing) {
            return
        }
        val temp_list = palletList.value
        if (temp_list.isNotEmpty()) {
            val selectList = temp_list[palletSelect.value]
            val temp_wt = selectList.row["VALUE1_NUMBER"] ?: ""
            palletWt.value = (Util.getDouble(temp_wt) * Util.getDouble(palletPcs.value)).toString()
        }

    }

    fun setNetWTCompute() {
        if (isEditing) {
            return
        }
        netWt.value = (Util.getDouble(wt.value) - Util.getDouble(palletWt.value)).toString()
    }
}