package com.aact.sat_pda.screen.import_irr_release

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_CARGO_IRR_RELEASE_Model: ViewModel() {
    val api  = MainAPI_Impl()
    val biz = BizAPI()

    var cargoSid = ""
    var holdTypeCode = ""
    var cargoNo = MutableLiveData("")
    val mawb = MutableLiveData("")
    val hawb = MutableLiveData("")
    val assign = MutableLiveData("")
    val mfcsPcs = MutableLiveData("")
    val mfcsWt = MutableLiveData("")

    val pcs = MutableLiveData("")
    val wt = MutableLiveData("")
    val holdType = MutableLiveData("")
    val holdDate = MutableLiveData("")
    val holdTime = MutableLiveData("")
    val holdRemark = MutableLiveData("")

    val releaseRemark = MutableLiveData("")
    val releaseDate = MutableLiveData("")
    val releaseTime = MutableLiveData("")
    val releaseType = MutableLiveData("")
    var holdTypeList: List<DataRow> = emptyList()
    var releaseTypeList: List<DataRow> = emptyList()
    val releaseTypeCode = MutableLiveData("")

    val releaseTypeIndex = MutableLiveData(-1)

    suspend fun getHoldTypeList() {
        val ret = biz.getCommonCode("IRTYP", "")
        if (ret != null) {
            holdTypeList = ret
        }
    }

    suspend fun getReleaseTypeList() {
        val ret = biz.getCommonCode("IRFRE", "")
        if (ret != null) {
            releaseTypeList = ret
        }
    }


    fun setInfo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_HOLD_M020_001(cargoSid, holdTypeCode)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    setClear()
                    if (Util.getTableCell(0,data[0],"COL1") == "OK") {
                        Common.suc.value = "조회 완료"
                        mawb.value = Util.getTableCell(0,data[1],"MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0,data[1],"HOUSE_AIR_WAY_BILL_NO")
                        assign.value = Util.getTableCell(0,data[1],"ASSIGN_CODE")
                        mfcsPcs.value = Util.getTableCell(0,data[1],"MFST_NO_OF_PACKAGE")
                        mfcsWt.value = Util.getTableCell(0,data[1],"MFST_NET_WEIGHT")
                        pcs.value = Util.getTableCell(0, data[1],"NO_OF_PACKAGE")
                        wt.value = Util.getTableCell(0,data[1],"NET_WEIGHT")
                        holdDate.value = Util.getTableCell(0, data[1],"HOLD_DATE")
                        holdTime.value = Util.getTableCell(0, data[1],"HOLD_TIME")
                        holdRemark.value = Util.getTableCell(0,data[1],"HOLD_REMARKS")
                        releaseRemark.value = Util.getTableCell(0, data[1], "RELEASE_REMARKS")
                        releaseDate.value = Util.getTableCell(0, data[1], "RELEASE_DATE")
                        releaseTime.value = Util.getTableCell(0, data[1],"RELEASE_TIME")
                        releaseType.value = Util.getTableCell(0,data[1],"RELEASE_TYPE_CODE")

                        val holdCode =  Util.getTableCell(0, data[1], "HOLD_TYPE_CODE")
                        holdTypeCode = holdCode

                        val name = holdTypeList.firstOrNull {
                                it.row["CODE_CODE"] == holdCode
                        }?.row?.get("CODE_NAME")?: ""

                        holdType.value = name

                        val releaseCode = Util.getTableCell(0,data[1],"RELEASE_TYPE_CODE")
                        releaseTypeCode.value = releaseCode

                        if (releaseCode.isNotEmpty()) {
                            val index = releaseTypeList.indexOfFirst {
                                it.row["CODE_CODE"] == releaseCode
                            }
                            releaseTypeIndex.postValue(if (index >= 0) index else -1)
                        }
                    }
                    else
                    {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun saveClick()
    {
        if (releaseTypeIndex.value == -1) {
            Common.sendError("해제 유형을 선택해 주세요.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_HOLD_M020_011(cargoSid
                                                    ,holdTypeCode
                                                    ,releaseTypeCode.value ?: ""
                                                    ,releaseDate.value ?: ""
                                                    , (releaseTime.value + "000000").substring(0,6)
                                                    , releaseRemark.value ?: "")

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0,data[0],"COL1") =="OK") {
                        Common.suc.value = "저장 완료"
                        Common.getStack()
                    }
                    else
                    {
                        Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setClear() {
        mawb.value = ""
        hawb.value = ""
        assign.value = ""
        mfcsPcs.value = ""
        mfcsWt.value = ""
        pcs.value = ""
        wt.value = ""
        holdType.value = ""
        holdDate.value = ""
        holdTime.value = ""
        holdRemark.value = ""
        releaseRemark.value = ""
        releaseDate.value = ""
        releaseTime.value = ""
        releaseType.value = ""
    }


}