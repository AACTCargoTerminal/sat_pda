package com.aact.sat_pda.screen.import_cargo_damage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.dto.TreeDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_CARGO_DAMAGE_Model :ViewModel() {

    val api: MainAPI = MainAPI_Impl()
    val biz = BizAPI()

    var cargoSid = ""
    var lastSearchedCargoNo = ""
    val cargoNo = MutableLiveData<String>("")
    val mawb = MutableLiveData<String>("")
    val hawb = MutableLiveData<String>("")

    val dmgPcs = MutableLiveData<String>("")
    val chkBUC = MutableLiveData<Boolean>(false)
    val remark = MutableLiveData<String>("")

    val dmgItemList = MutableLiveData<List<DataRow>>(emptyList())
    var dmgItemSelect = mutableMapOf("DAMAGE_GROUP_CODE" to "")

    val groups = MutableLiveData<List<TreeDTO.Group>>(emptyList())


    val dmgList = MutableLiveData<List<DataRow>>(emptyList())
    var dmgSelect = mutableMapOf("DAMAGE_ITEM_CODE" to "")

    val dmgListHeader = listOf(
        HeaderDTO("DAMAGE_ITEM_NAME", "항목명", 150),
        HeaderDTO("CHECK_FLAG", "선택", 60, editFlag = false, isChk = true),
        HeaderDTO("NO_OF_DAMAGE", "수량", 80, editFlag = true, isChk = false)
    )

    private var originalData = mutableListOf<DataRow>()
    private var currentData = mutableListOf<DataRow>()

    fun setInfo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val searchCargoNo = cargoNo.value.orEmpty()

            if (searchCargoNo.isBlank()) {
                Common.sendError("화물번호를 입력해주세요.")
                Common.loadingOff()
                return@launch
            }

            lastSearchedCargoNo = searchCargoNo
            clearDetail()

            val ret = api.getPWM_CARGO_DAMAGE_M010_002_003_004_BY_NO(searchCargoNo)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {


                        Common.suc.value = "조회 완료"

                        cargoSid = Util.getTableCell(0, data[1], "CARGO_CONTROL_SID")
                        mawb.value = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")
                        dmgPcs.value = Util.getTableCell(0, data[1], "DAMAGED_NO_OF_PACKAGE")
                        chkBUC.value = Util.getTableCell(0, data[1], "DAMAGED_HOLD_FLAG") == "Y"
                        remark.value = Util.getTableCell(0, data[1], "DAMAGED_DESCRIPTION")

                        // 아이템 데이터 먼저 설정
                        data[3]?.let { itemData ->
                            originalData = itemData.toMutableList()
                            currentData = itemData.map { it.copy() }.toMutableList()
                        } ?: run {
                            originalData.clear()
                            currentData.clear()
                        }

                        // 그룹 데이터 설정
                        data[2]?.let { groupData ->
                            val treeGroups = groupData.map { groupRow ->
                                val groupCode = Util.getStr(groupRow.row["DAMAGE_GROUP_CODE"])
                                val groupName = Util.getStr(groupRow.row["DAMAGE_GROUP_NAME"])

                                // 해당 그룹의 아이템들 필터링
                                val items = currentData.filter {
                                    it.row["DAMAGE_GROUP_CODE"] == groupCode
                                }.map { itemRow ->
                                    TreeDTO.Item(
                                        key = Util.getStr(itemRow.row["DAMAGE_ITEM_CODE"]),
                                        value = Util.getStr(itemRow.row["DAMAGE_ITEM_NAME"]),
                                        checkFlag = Util.getStr(itemRow.row["CHECK_FLAG"]),
                                        changeFlag = "N"
                                    )
                                }.toMutableList()

                                TreeDTO.Group(
                                    key = groupCode,
                                    value = groupName,
                                    itemArray = items,
                                    isExpanded = false
                                )
                            }

                            groups.value = treeGroups

                            if (groupData.isNotEmpty()) {
                                dmgItemSelect["DAMAGE_GROUP_CODE"] = Util.getStr(groupData[0].row["DAMAGE_GROUP_CODE"])
                                loadItems(dmgItemSelect["DAMAGE_GROUP_CODE"] ?: "")
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




    fun loadItems(groupCode: String) {
        val filtered = currentData.filter {
            it.row["DAMAGE_GROUP_CODE"] == groupCode
        }
        dmgList.value = filtered

        if (filtered.isNotEmpty()) {
            dmgSelect["DAMAGE_ITEM_CODE"] = Util.getStr(filtered[0].row["DAMAGE_ITEM_CODE"])
        }
    }

    fun updateItem(itemCode: String, checkFlag: String) {
        currentData.find {
            it.row["DAMAGE_ITEM_CODE"] == itemCode
        }?.let { item ->
            item.row["CHECK_FLAG"] = checkFlag
            if (checkFlag != "Y") {
                item.row["NO_OF_DAMAGE"] = "0"
            }
        }

        // RowAdapter에서 변경사항 반영을 위해 dmgList 업데이트
        val groupCode = dmgItemSelect["DAMAGE_GROUP_CODE"] ?: ""
        if (groupCode.isNotEmpty()) {
            loadItems(groupCode)
        }
    }

    fun saveClick() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            if (cargoSid.isEmpty()) {
                Common.sendError("수입화물이 조회되지 않았습니다.")
                Common.loadingOff()
                return@launch
            }

            if (Util.getDouble(dmgPcs.value) == 0.0) {
                Common.sendError("파손수량이 없습니다.")
                Common.loadingOff()
                return@launch
            }

            var saveFlag = ""

            for (i in currentData.indices) {
                val current = currentData[i]
                val original = originalData[i]

                val currentCheck = current.row["CHECK_FLAG"] ?: "N"
                val originalCheck = original.row["CHECK_FLAG"] ?: "N"

                if (currentCheck != originalCheck || currentCheck == "Y") {
                    val ret = api.setPWM_CARGO_DAMAGE_M010_013(
                        cargoSid,
                        current.row["DAMAGE_GROUP_CODE"] ?: "",
                        current.row["DAMAGE_ITEM_CODE"] ?: "",
                        currentCheck,
                        current.row["NO_OF_DAMAGE"] ?: "0"
                    )

                    when(ret) {
                        is Result.Error -> {
                            Common.sendError(ret.message)
                            Common.loadingOff()
                            return@launch
                        }
                        is Result.Success<DataTable> -> {
                            val data = ret.data.table
                            if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                                saveFlag += "OK"
                            } else {
                                Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                                Common.loadingOff()
                                return@launch
                            }
                        }
                    }
                }
            }

            if (saveFlag.contains("OK")) {
                val currDT = biz.getCurDateTime()
                val holdFlag = if (chkBUC.value == true) "Y" else "N"

                val ret2 = api.setPWM_CARGO_DAMAGE_M010_012(
                    cargoSid,
                    holdFlag,
                    dmgPcs.value ?: "0",
                    remark.value ?: "",
                    Common.userId.value ?: "",
                    currDT.substring(0, 8),
                    currDT.substring(8, 14)
                )

                when(ret2) {
                    is Result.Error -> Common.sendError(ret2.message)
                    is Result.Success<DataTable> -> {
                        val data = ret2.data.table
                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                            Common.suc.value = "저장완료"

                            Common.loadingOff()
                            setInfo()
                            return@launch
                        } else {
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                        }
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setClear() {
        cargoNo.value = ""
        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        dmgPcs.value = ""
        chkBUC.value = false
        remark.value = ""
        dmgItemList.value = emptyList()
        dmgList.value = emptyList()
        dmgItemSelect = mutableMapOf("DAMAGE_GROUP_CODE" to "")
        dmgSelect = mutableMapOf("DAMAGE_ITEM_CODE" to "")
        originalData.clear()
        currentData.clear()
    }

    private fun clearDetail() {
        cargoSid = ""
        mawb.value = ""
        hawb.value = ""
        dmgPcs.value = ""
        chkBUC.value = false
        remark.value = ""
        dmgItemList.value = emptyList()
        groups.value = emptyList()
        dmgList.value = emptyList()
        dmgItemSelect = mutableMapOf("DAMAGE_GROUP_CODE" to "")
        dmgSelect = mutableMapOf("DAMAGE_ITEM_CODE" to "")
        originalData.clear()
        currentData.clear()
    }

}