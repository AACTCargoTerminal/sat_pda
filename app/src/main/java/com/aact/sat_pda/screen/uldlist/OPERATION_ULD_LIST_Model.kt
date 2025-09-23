package com.aact.sat_pda.screen.uldlist

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
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_LIST_Model : ViewModel() {

    val biz = BizAPI()
    val api = MainAPI_Impl()
    val header = Header()

    var isEditing = false

    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val fltNo = MutableLiveData<String>("")
    val fltSelect = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")
    val uldList = MutableLiveData<List<DataRow>>(emptyList())
    val chkEOF = MutableLiveData<Boolean>(true)
    val chkBUC = MutableLiveData<Boolean>(false)

    var selectList = mapOf("OPERATION_ULD_SID" to "", "ULD_NO" to "")

    var fltOrigin = ""
    var fltDestNm = ""

    val fltTable = SubContent.GetTable(
        headerList = header.fltHeader,
        bodylist = emptyList(),
        selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
        onClick = { value ->
            val select = value["SCHEDULE_SID"] ?: ""
            fltSelect.value = select
            fltNo.value = value["FLIGHT_NO"] ?: ""
            if (select.length > 0) {
                getDest(select)
            }

        },
        endClick = {})

    val destTable = SubContent.GetTable(
        headerList = header.commonCodeHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to ""),
        onClick = {
            fltDest.value = it["CODE_CODE"] ?: ""
        },
        endClick = {}
    )

    val headerList = listOf<HeaderDTO>(
        HeaderDTO("OPERATION_ULD_SID", "id"),
        HeaderDTO("ULD_NO", "ULD_NO", 200),
        HeaderDTO("CONTOUR_CODE", "CONTOUR", 140),
        HeaderDTO("FLIGHT_NO", "FLIGHT_NO", 160),
        HeaderDTO("ULD_STATUS_NAME", "ULD_STATUS", 250),
        HeaderDTO("DESTINATION_NAME", "DEST", 120)
    )

    fun setFlight(outParam: (suc: List<DataRow>) -> Unit) {
        val date = fltDate.value?.replace("-", "") ?: ""

        if (!Util.validYYYYMMDD(date)) {
            Common.sendError("일자를 확인해주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])


            val ret = biz.getFltight(date)

            ret?.let {
                if (it.size == 0) {
                    Common.sendError("해당날짜의 편번은 없습니다.")
                } else {
                    outParam(it)
                }

            }


            Common.loadingOff()
        }
    }

    fun getDest(select: String) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getOriginDest(select)

            ret?.row?.let { data ->
                fltDest.value = data["DESTINATION_CODE"] ?: ""
                fltDestNm = data["DESTINATION_NAME"] ?: ""
                fltOrigin = data["ORIGIN_CODE"] ?: ""
            } ?: run {
                fltDest.value = ""
                fltDestNm = ""
                fltOrigin = ""
            }

            Common.loadingOff()
        }
    }

    fun getList() {

        val date = fltDate.value?.replace("-", "") ?: ""

        if (!Util.validYYYYMMDD(date)) {
            Common.sendError("일자를 확인해주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret =
                api.getPWM_OPERATION_ULD_L010_001(date, fltNo.value, fltOrigin, fltDest.value)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        data[1]?.let { list ->
                            val tmp_array = list.filter {
                                if (Util.getStr(it.row["ULD_STATUS_CODE"]) == "EOF" && chkEOF.value == true) {
                                    false
                                } else if (
                                    Util.getStr(it.row["ULD_STATUS_CODE"]) == "BUC" && chkBUC.value == true
                                ) {
                                    false
                                } else {
                                    true
                                }
                            }

                            if (tmp_array.size > 0) {
                                selectList = mapOf(
                                    "OPERATION_ULD_SID" to Util.getStr(tmp_array[0].row["OPERATION_ULD_SID"]),
                                    "ULD_NO" to Util.getStr(tmp_array[0].row["ULD_NO"])
                                )
                            }else{
                                selectList = mapOf(
                                    "OPERATION_ULD_SID" to "",
                                    "ULD_NO" to ""
                                )
                            }

                            uldList.value = tmp_array
                        } ?: run {
                            selectList = mapOf(
                                "OPERATION_ULD_SID" to "",
                                "ULD_NO" to ""
                            )
                            uldList.value = emptyList()
                        }

                        Common.suc.value = "조회가 완료됐습니다."

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }


}