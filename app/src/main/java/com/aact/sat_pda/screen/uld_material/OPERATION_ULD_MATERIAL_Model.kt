package com.aact.sat_pda.screen.uld_material

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

class OPERATION_ULD_MATERIAL_Model : ViewModel() {

    val api = MainAPI_Impl()

    var operationSid = ""
    val uldNo = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")

    val materialList = MutableLiveData<List<DataRow>>(emptyList())
    var selectList = mapOf("MATERIAL_CODE" to "")
    val selectMap : MutableMap<Int, DataRow> = mutableMapOf()
    val headerList = listOf<HeaderDTO>(
        HeaderDTO("OPERATION_ULD_SID", "id"),
        HeaderDTO("MATERIAL_CODE", "CODE", 180),
        HeaderDTO("MATERIAL_NAME", "NAME", 220),
        HeaderDTO("MATERIAL_QTY", "PCS", 110, true),
    )

    fun getList() {

        viewModelScope.launch {

            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_OPERATION_ULD_M010_003(operationSid)

            when(ret){
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if(Util.getTableCell(0,data[0],"COL1")=="OK"){

                        data[1]?.let { table->
                            materialList.value = table
                        }?:run {
                            materialList.value = emptyList()
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
        val materialTmp = materialList.value
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var flagOk = true

            selectMap.forEach { (key,value)->
                val sltTmp = materialTmp.getOrNull(key)
                if(sltTmp!= null){

                    val cd = sltTmp.row["MATERIAL_CODE"] ?: ""
                    val originQty = sltTmp.row["MATERIAL_QTY"] ?: "0"
                    val qty = value.row["MATERIAL_QTY"] ?: "0"

                    if(qty.isNotEmpty() && qty != "0"){
                        if(qty != originQty){
                            val flag = setMaterialSave(operationSid,cd,qty)
                            if(flag == false){
                                flagOk = false
                                //이구간에서 false가 나오면 정지하고싶음 순환을
                                return@forEach
                            }
                        }
                    }


                }
            }

            if(flagOk){
                Common.getStack()
            }
            Common.loadingOff()
        }

    }

    suspend fun setMaterialSave(sid:String,cd:String,value:String) : Boolean{

        val ret = api.setPWM_OPERATION_ULD_M010_012(sid,cd,value)
        when(ret){
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                    return true

                }else{
                    Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                }
            }
        }

        return false
    }

}