package com.aact.sat_pda.screen.damage_export

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataTable
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.dto.TreeDTO

class CARGO_DAMAGE_EXPORT_Model: ViewModel() {
    val api = MainAPI_Impl()
    var isEditing = false
    var bSearchCon = true

    var cargoControlSid = ""
    val damageExportSid = MutableLiveData<String>("")
    var acceptType = ""
    var fltSelect = ""

    val mawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val fltNo = MutableLiveData<String>("")
    val damagePcs = MutableLiveData<String>("")
    val sumPcs = MutableLiveData<String>("")
    val remark = MutableLiveData<String>("")
    val goods = MutableLiveData<String>("")


    val groups = MutableLiveData<List<TreeDTO.Group>>(emptyList())

    fun getGroupCode(){
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val ret = api.getPWM_CARGO_DAMAGE_M010_003("0")
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { data1 ->
                            val list_tmp: List<TreeDTO.Group> = data1.mapIndexed { index, item ->
                                TreeDTO.Group(
                                    item.row["DAMAGE_GROUP_CODE"] ?: "",
                                    item.row["DAMAGE_GROUP_NAME"] ?: ""
                                )
                            }
                            groups.value = list_tmp

                            if(damageExportSid.value.length > 0){
                                getCode()
                            }
                        } ?: run {
                            groups.value = emptyList()
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }
            Common.loadingOff()

        }

    }

    fun getCode() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])



            val ret2 = api.getPWM_CARGO_DAMAGE_M010_007(damageExportSid.value, "M")

            when (ret2) {
                is Result.Error -> Common.sendError(ret2.message)
                is Result.Success<DataTable> -> {
                    val data = ret2.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { data1 ->
                            val group_tmp = groups.value
                            group_tmp?.forEach { it.itemArray.clear() }
                            data1.forEach { item ->
                                val group_obj =
                                    group_tmp.find { it.key == item.row["DAMAGE_GROUP_CODE"] }
                                if (group_obj != null) {
                                    val item_tmp = TreeDTO.Item(
                                        item.row["DAMAGE_ITEM_CODE"] ?: "",
                                        item.row["DAMAGE_ITEM_NAME"] ?: "",
                                        "N",
                                        item.row["CHECK_FLAG"] ?: ""
                                    )

                                    group_obj.itemArray.add(item_tmp)
                                }
                            }
                            groups.value = group_tmp
                        }
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun searchMawb_after(){
        val mawb_tmp = mawb.value
        if (mawb_tmp.length != 11) {
            Common.sendError("MAWB를 확인하세요")
            return
        }
        val sql = StringBuilder().apply {
            append("SELECT TCM.CARGO_CONTROL_SID , NVL(DEM.DAMAGE_EXPORT_SID,0) DAMAGE_EXPORT_SID ")
            append("FROM TWM_CARGO_CONTROL_MASTER TCM ")
            append("JOIN TWM_CARGO_INOUT_MASTER CIM ")
            append("ON CIM.CARGO_CONTROL_SID = TCM.CARGO_CONTROL_SID ")
            append("AND CIM.INOUT_FLAG = 'I' ")
            append("LEFT JOIN TWM_CARGO_DAMAGE_EXPORT_MASTER DEM ")
            append("ON DEM.CARGO_CONTROL_SID = TCM.CARGO_CONTROL_SID ")
            append("AND DEM.USABLE_FLAG = 'Y' ")
            append("AND DEM.ACCEPT_TYPE = '${acceptType}' ")
            append("WHERE TCM.CREATED_TIME > SYSDATE - 365 ")
            append("AND TCM.USABLE_FLAG = 'Y' ")
            append("AND TCM.INOUT_FLAG = 'E' ")
            append("AND TCM.USABLE_FLAG = 'Y' ")
            append("AND TCM.MASTER_AIR_WAY_BILL_NO = '${mawb_tmp}'")
        }.toString()

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getSqlDataSet(sql)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        if(!data[1].isNullOrEmpty()){

                            cargoControlSid = Util.getTableCell(0,data[1],"CARGO_CONTROL_SID")
                            damageExportSid.value = Util.getTableCell(0,data[1],"DAMAGE_EXPORT_SID")

                        }else{
                            Common.sendError("화물 입고 상태를 확인해주세요")
                        }

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }

    }

    fun searchMawb_before() {

        val mawb_tmp = mawb.value
        if (mawb_tmp.length != 11) {
            Common.sendError("MAWB를 확인하세요")
            return
        }

        val sql = StringBuilder().apply {
            append("SELECT DAMAGE_EXPORT_SID, APPROVE_FLAG ")
            append("FROM TWM_CARGO_DAMAGE_EXPORT_MASTER ")
            append("WHERE CREATED_TIME > SYSDATE - 365 ")
            append("AND USABLE_FLAG = 'Y' ")
            append("AND ACCEPT_TYPE = '${acceptType}' ")
            append("AND MASTER_AIR_WAY_BILL_NO = '${mawb.value}'")
        }.toString()

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getSqlDataSet(sql)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        if (!data[1].isNullOrEmpty()) {
                            if (Util.getTableCell(0, data[1], "APPROVE_FLAG") == "W") {
                                damageExportSid.value =
                                    Util.getTableCell(0, data[1], "DAMAGE_EXPORT_SID","0")
                                cargoControlSid = "0"
                            } else {
                                Common.sendError("해당화물은 이미 파손 처리된 화물입니다.")
                                Common.loadingOff()
                                return@launch
                            }
                        } else {
                            damageExportSid.value = "0"
                            cargoControlSid = "0"
                        }


                    } else {
                        Common.sendError("MAWB조회 에러")
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun searchInfo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret: Result<DataTable>? = when (acceptType) {
                "Y" -> {
                    if (cargoControlSid != "0") {
                        if (damageExportSid.value == "0") {
                            api.getPWM_CARGO_CONTROL_M010_001(cargoControlSid)
                        } else {
                            api.getPWM_CARGO_DAMAGE_M010_008(damageExportSid.value)
                        }
                    } else null
                }

                "N" -> {
                    if (damageExportSid.value == "0") {

                        if (mawb.value.length < 11) {
                            Common.loadingOff()
                            return@launch
                        }

                        api.getPWM_CARGO_DAMAGE_M010_006(mawb.value)
                    } else {
                        api.getPWM_CARGO_DAMAGE_M010_008(damageExportSid.value)
                    }
                }

                else -> null
            }

            ret?.let {
                when (it) {
                    is Result.Error -> Common.sendError(it.message)
                    is Result.Success<DataTable> -> {
                        val data = it.data.table
                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                            Common.suc.value = "조회가 완료됬습니다."
                            if(mawb.value.length < 11){
                                mawb.value =
                                    Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO", mawb.value)
                            }
                            fltDate.value =
                                Util.getTableCell(0, data[1], "FLIGHT_DATE", fltDate.value)
                            fltNo.value = Util.getTableCell(0, data[1], "FLIGHT_NO", fltNo.value)
                            fltSelect = Util.getTableCell(0, data[1], "SCHEDULE_SID", fltSelect)
                            damagePcs.value = Util.getTableCell(
                                0,
                                data[1],
                                "NO_OF_PACKAGE_DAMAGE",
                                damagePcs.value
                            )
                            sumPcs.value =
                                Util.getTableCell(
                                    0,
                                    data[1],
                                    if(acceptType == "Y" && damageExportSid.value == "0") "STOCK_NO_OF_PACKAGE" else "NO_OF_PACKAGE",
                                    sumPcs.value
                                )
                            remark.value = Util.getTableCell(0, data[1], "REMARK", remark.value)
                            goods.value = Util.getTableCell(0, data[1], "GOODS", goods.value)
                        } else {
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                        }
                    }
                }
            } ?: run {
                Common.sendError("조회에러")
            }



            Common.loadingOff()
        }

    }

    fun parseXml(array: List<TreeDTO.Group>) {

        val changedGroups: List<TreeDTO.Group> = array.mapNotNull { group ->
            val filteredItems = group.itemArray.filter { it.changeFlag == "Y" }
            if (filteredItems.isNotEmpty()) {
                group.copy(itemArray = filteredItems.toMutableList())
            } else {
                null // 해당 그룹은 제외됨
            }
        }

        val str = createItemXmlFromGroups(changedGroups)

        viewModelScope.launch {

            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_DAMAGE_M010_014_015(
                damageExportSid.value,
                cargoControlSid,
                fltSelect,
                acceptType,
                mawb.value,
                "",
                Common.userId.value,
                sumPcs.value,
                damagePcs.value,
                goods.value,
                remark.value,
                str
            )
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "저장이 완료됬습니다."
                        damageExportSid.value = Util.getTableCell(0, data[0], "COL2")
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }

    }

    fun createItemXmlFromGroups(groups: List<TreeDTO.Group>): String {

        data class JsonTableRow(
            val DAMAGE_GROUP_CODE: String,
            val DAMAGE_ITEM_CODE: String,
            val CHECK_FLAG: String,
            val CHANGE_FLAG: String
        )

        val tableList = groups.flatMap { group ->
            group.itemArray.map { item ->
                JsonTableRow(
                    DAMAGE_GROUP_CODE = group.key,
                    DAMAGE_ITEM_CODE = item.key,
                    CHECK_FLAG = item.checkFlag,
                    CHANGE_FLAG = item.changeFlag
                )
            }
        }
        val gson = Gson()
        val jsonString = gson.toJson(tableList)

        return jsonString


    }

    fun printPdf() {
        val dmgSid = damageExportSid.value

        if (dmgSid.length <= 1) {
            Common.sendError("MAWB를 확인해주세요")
            return
        }

        var ip = "SCALE#2"

        if (Common.terminalCode.value != "T1") {
            ip += "_" + Common.terminalCode.value
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var ret: Result<DataTable>

            ret = api.setPWM_CARGO_SCALE_PRINT_MAWB(cargoControlSid, ip)

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                    Common.loadingOff()
                    return@launch
                }

                else -> {

                }
            }

            ret = api.setPWM_CARGO_VOLUME_PRINT_MAWB(cargoControlSid, ip)

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                    Common.loadingOff()
                    return@launch
                }

                else -> {
                    Common.suc.value = "출력이 완료됐습니다."
                }
            }

            Common.loadingOff()
        }
    }

    fun setClear() {
        cargoControlSid = ""
        damageExportSid.value = ""

        fltDate.value = Util.formatDate_15(Common.dateTime.value)
        fltNo.value = ""
        damagePcs.value = ""
        sumPcs.value = ""
        remark.value = ""
        goods.value = ""
        fltSelect = ""
        val gr = groups.value
        gr?.forEach { it.itemArray.clear() }
        groups.value = gr
    }
}