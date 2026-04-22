package com.aact.sat_pda.screen.doubt_list

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
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_DOUBT_LIST_Model : ViewModel() {

    val api = MainAPI_Impl()
    val biz = BizAPI()

    var custSid = "0"
    var fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val mawb = MutableLiveData<String>("")
    val custCode = MutableLiveData<String>("")
    var custName = ""
    var custNum1 = ""
    var custNum2 = ""

    var custList: List<DataRow> = emptyList()
    val custHeader = listOf<HeaderDTO>(
        HeaderDTO("CODE_SID", "id"),
        HeaderDTO("VALUE1_CHAR", "대리점코드", 150),
        HeaderDTO("CODE_NAME", "대리점명", 220),
        HeaderDTO("VALUE2_CHAR", "연락처1", 180),
        HeaderDTO("VALUE3_CHAR", "연락처2", 220),
    )

    val custTable = SubContent.GetTable(
        headerList = custHeader,
        bodylist = emptyList(),
        selectValue = mapOf(
            "CODE_SID" to "",
            "VALUE1_CHAR" to "",
            "CODE_NAME" to "",
            "VALUE2_CHAR" to "",
            "VALUE3_CHAR" to "",
        ),
        onClick = { value ->
            val select = value["CODE_SID"]
            if (select != null) {
                custSid = select
                custCode.value = Util.getStr(value["VALUE1_CHAR"])
                custName = Util.getStr(value["CODE_NAME"])
                custNum1 = Util.getStr(value["VALUE2_CHAR"])
                custNum2 = Util.getStr(value["VALUE3_CHAR"])

            } else {
                custSid = "0"
                custCode.value = ""
                custName = ""
                custNum1 = ""
                custNum2 = ""
            }

        },
        endClick = {})

    val doubtList = MutableLiveData<List<DataRow>>(emptyList())
    val doubtHeader = listOf<HeaderDTO>(
        HeaderDTO("CARGO_CONTROL_SID", "id"),
        HeaderDTO("CARGO_ACCEPT_SEQ", "id"),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 180),
        HeaderDTO("STATUS", "상태", 180),
        HeaderDTO("DATE", "시간", 220),
    )
    val doubtSelect = MutableLiveData<Map<String, String>>(
        mapOf(
            "CARGO_CONTROL_SID" to "",
            "CARGO_ACCEPT_SEQ" to ""
        )
    )


    fun setLoad() {
        //fltDate = Util.formatDate_15(Common.dateTime.value)

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getCommonCode("XRCOD", "")

            if (ret == null) {
                custList = emptyList()
            } else {
                custList = ret
            }

            getInfo()

            Common.loadingOff()
        }
    }

    suspend fun getInfo() {
        val ret =
            api.getPWM_XRAY_L010_007(fltDate.value, fltDate.value, mawb.value, Common.terminalCode.value, "")

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    data[1]?.let { table ->
                        table.forEach { rows ->
                            if (Util.getStr(rows.row["RELEASE_FLAG"]) == "N" && Util.getStr(rows.row["NOTICE_TIME"]) == "") {
                                rows.row["STATUS"] = "의심화물접수"
                                rows.row["DATE"] = ""
                            } else if (Util.getStr(rows.row["RELEASE_FLAG"]) == "N" && Util.getStr(
                                    rows.row["NOTICE_TIME"]
                                ).length > 0
                            ) {
                                rows.row["STATUS"] = "통보완료"
                                rows.row["DATE"] = Util.offSetParse(rows.row["NOTICE_TIME"] ?: "")
                            } else if (Util.getStr(rows.row["RELEASE_FLAG"]) == "Y") {
                                rows.row["STATUS"] = "해제완료"
                                rows.row["DATE"] = Util.offSetParse(rows.row["RELEASE_TIME"] ?: "")
                            }
                        }

                        doubtList.value = table
                    } ?: run {
                        doubtList.value = emptyList()
                    }

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

    }

    fun doubtClick(obj: Map<String, String>) {
        val sid = obj["CARGO_CONTROL_SID"]
        val seq = obj["CARGO_ACCEPT_SEQ"]
        val tmp_list = doubtList.value
        if (sid != null || seq != null) {
            val findData =
                tmp_list.find { rows -> rows.row["CARGO_CONTROL_SID"] == sid && rows.row["CARGO_ACCEPT_SEQ"] == seq }
            if (findData != null) {
                val companySid = Util.getStr(findData.row["COMPANY_SID"])
                if (companySid == "0") {
                    custSid = "0"
                    custCode.value = ""
                    custName = ""
                    custNum1 = ""
                    custNum2 = ""
                } else {
                    val custFind = custList.find { rows -> rows.row["CODE_SID"] == companySid }
                    if (custFind != null) {
                        custSid = Util.getStr(custFind.row["CODE_SID"])
                        custCode.value = Util.getStr(custFind.row["VALUE1_CHAR"])
                        custName = Util.getStr(custFind.row["CODE_NAME"])
                        custNum1 = Util.getStr(custFind.row["VALUE2_CHAR"])
                        custNum2 = Util.getStr(custFind.row["VALUE3_CHAR"])
                    } else {
                        custSid = "0"
                        custCode.value = ""
                        custName = ""
                        custNum1 = ""
                        custNum2 = ""
                        Common.sendError("해당하는 대리점코드가 없습니다.")
                    }

                }
            }
        }
    }

    fun noticeClick(){
        val cargoSid = doubtSelect.value["CARGO_CONTROL_SID"]
        val seq = doubtSelect.value["CARGO_ACCEPT_SEQ"]
        if(cargoSid.isNullOrEmpty()||seq.isNullOrEmpty()){
            Common.sendError("목록을 선택해주세요")
            return
        }

        val findData = doubtList.value.find { rows->rows.row["CARGO_CONTROL_SID"] == cargoSid && rows.row["CARGO_ACCEPT_SEQ"] == seq }
        if(findData!= null){
            val tmp_status = Util.getStr(findData.row["STATUS"])
            val tmp_custSid = Util.getStr(findData.row["COMPANY_SID"])

            if(tmp_status == "의심화물접수" && custSid == "0"){
                Common.sendError("대리점 등록 필수")
                return
            }

            if(tmp_status == "통보완료" && tmp_custSid == "0"){
                Common.sendError("대리점 등록 필수")
                return
            }

            viewModelScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = api.setPWM_XRAY_L010_014(cargoSid,seq,custSid,"N",Common.userId.value)
                when(ret){
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                            Common.suc.value = "저장완료"
                            mawb.value = ""
                            getInfo()
                        }else{
                            Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                        }
                    }
                }

                Common.loadingOff()
            }

        }
    }

    fun releaseClick(){
        val cargoSid = doubtSelect.value["CARGO_CONTROL_SID"]
        val seq = doubtSelect.value["CARGO_ACCEPT_SEQ"]
        if(cargoSid.isNullOrEmpty()||seq.isNullOrEmpty()){
            Common.sendError("목록을 선택해주세요")
            return
        }

        val findData = doubtList.value.find { rows->rows.row["CARGO_CONTROL_SID"] == cargoSid && rows.row["CARGO_ACCEPT_SEQ"] == seq }
        if(findData!= null){
            val tmp_status = Util.getStr(findData.row["STATUS"])
            val tmp_custSid = Util.getStr(findData.row["COMPANY_SID"])

            if(tmp_status == "의심화물접수"){
                Common.sendError("통보저장 필수")
                return
            }

            if(tmp_status == "통보완료" && tmp_custSid == "0"){
                Common.sendError("대리점 등록 필수")
                return
            }

            viewModelScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = api.setPWM_XRAY_L010_014(cargoSid,seq,custSid,"Y",Common.userId.value)
                when(ret){
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                            Common.suc.value = "저장완료"
                            mawb.value = ""
                            getInfo()
                        }else{
                            Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                        }
                    }
                }

                Common.loadingOff()
            }

        }
    }
}