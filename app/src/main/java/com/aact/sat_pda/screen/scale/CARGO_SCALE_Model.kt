package com.aact.sat_pda.screen.scale

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

class CARGO_SCALE_Model : ViewModel() {
    val api = MainAPI_Impl()
    val biz = BizAPI()
    var isEditing = false

    val scaleRadio = MutableLiveData<List<DataRow>>(emptyList())

    val scaleRadioSelect = MutableLiveData<Int>(1)


    var cargoControlSid = ""
    var cargoStatusCode = ""
    val mawb = MutableLiveData<String>("")
    val weight = MutableLiveData<String>("")
    val netWt = MutableLiveData<String>("")
    val fltDest = MutableLiveData<String>("")

    val fltNo = MutableLiveData<String>("")
    val fltSelect = MutableLiveData<String>("")
    val fltList = MutableLiveData<List<DataRow>>(emptyList())

    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))
    val agent = MutableLiveData<String>("")
    val agentName = MutableLiveData<String>("")
    var customerCode = ""
    val pcs = MutableLiveData<String>("")
    val spc = MutableLiveData<String>("")
    val palletPcs = MutableLiveData<String>("1")
    val palletWt = MutableLiveData<String>("")
    val volumeWt = MutableLiveData<String>("")
    val acceptPcs = MutableLiveData<String>("")
    val acceptWt = MutableLiveData<String>("")
    val mfstPcs = MutableLiveData<String>("")
    val mfstWt = MutableLiveData<String>("")
    val statusNo = MutableLiveData<String>("")

    val palletList = MutableLiveData<List<DataRow>>(emptyList())
    val palletSelect = MutableLiveData<Int>(0)
    val spcList = MutableLiveData<List<DataRow>>(emptyList())

    val spcHeader = listOf(
        HeaderDTO("CODE_CODE", "Code", 120),
        HeaderDTO("CODE_NAME", "Name", 300)
    )
    val spcSelect = MutableLiveData<Pair<String, String>>(Pair("", ""))

    val acceptList = MutableLiveData<LinkedHashMap<String, String>>(linkedMapOf())
    val acceptSelect = MutableLiveData<String>("")
    val acceptSeq = MutableLiveData<String>("")

    val modalTable = SubContent.GetTable(
        headerList = spcHeader,
        bodylist = emptyList(),
        selectValue = mapOf("CODE_CODE" to "", "CODE_NAME" to ""),
        onClick = { value ->
            spcSelect.value = Pair(value["CODE_CODE"] ?: "", value["CODE_NAME"] ?: "")
        },
        endClick = {})

    val fltHeader: List<HeaderDTO> = listOf<HeaderDTO>(
        HeaderDTO("SCHEDULE_SID", "id"),
        HeaderDTO("FLIGHT_NO", "편번", 140),
        HeaderDTO("TERMINAL_CODE", "터미널", 100)
    )

    val fltTable = SubContent.GetTable(
        headerList = fltHeader,
        bodylist = emptyList(),
        selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
        onClick = { value ->
            val select = value["SCHEDULE_SID"] ?: ""
            fltSelect.value = select
            fltNo.value = value["FLIGHT_NO"] ?: ""
        },
        endClick = {})


    fun getList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val retPallet = async { biz.getCommonCode("PLTCD", "") }
            retPallet.await()?.let {
                palletList.value = it
            }
            val retSpc = async { biz.getCommonCode("SPCGO", "") }
            retSpc.await()?.let {
                spcList.value = it
            }
            val retScale = async { biz.getCommonCode("SCALE", "") }

            retScale.await()?.let {
                scaleRadio.value = it
            }

            Common.loadingOff()
        }
    }

    fun scaleClick() {
        var ip = ""

        val scaleSelect = scaleRadioSelect.value
        val terminalCode = Common.terminalCode.value

        val ip1 = "WGIP#1"
        val ip2 = "WGIP#2"
        val ip3 = "WGIP#3"
        val ip6 = "WGIP#6"

        when (scaleSelect) {
            0 -> {
                ip =
                    if (terminalCode == "T1") {
                        ip1
                    } else {
                        "${ip1}_${terminalCode}"
                    }
            }

            1 -> {
                ip =
                    if (terminalCode == "T1") {
                        ip2
                    } else {
                        "${ip2}_${terminalCode}"
                    }
            }

            2 -> {
                ip =
                    if (terminalCode == "T1") {
                        ip3
                    } else {
                        "${ip3}_${terminalCode}"
                    }
            }

            3 -> {
                ip =
                    if (terminalCode == "T1") {
                        ip6
                    } else {
                        "${ip6}_${terminalCode}"
                    }
            }

            else -> {
                Common.sendError("알 수 없는 Scale 입니다.")
                return
            }
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getScaleWeight(ip)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    data[0]?.let { status ->

                        if (Util.getTableCell(0, status, "COL1") == "OK") {
                            weight.value = Util.getTableCell(0, status, "COL3")
                            Common.suc.value = "Scale이 완료됐습니다."
                        } else {
                            Common.sendError(Util.getTableCell(0, status, "COL2"))
                        }

                    } ?: run {
                        Common.sendError("서버에러")
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun getAcceptList() {

        var mawb_tmp = Util.getStr(mawb.value)

        if (mawb_tmp.length < 11) {
            Common.sendError("MAWB를 확인하세요")
            return
        }
        mawb_tmp = mawb_tmp.substring(0,11)
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPCM_GET_CARGO_ACCEPT_BY_MAWB(mawb_tmp)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    data[0]?.let { status ->
                        if (Util.getTableCell(0, status, "COL1") == "OK") {
                            setClearAll()
                            data[1]?.let { data1 ->
                                val tempList = linkedMapOf<String, String>()
                                if (data1.size > 0) {

                                    data1.forEach { cells ->
                                        val addStr =
                                            Util.getStr(cells.row["CARGO_ACCEPT_SEQ"]) + " : " +  cells.row["ACCEPTED_TIME"] + " , " + Util.getStr(cells.row["CARGO_STATUS_CODE"]) + " , " + Util.getStr(cells.row["NO_OF_PALLET"])
                                        tempList.put(
                                            Util.getStr(cells.row["CARGO_ACCEPT_SID"]),
                                            addStr
                                        )
                                        cargoStatusCode = Util.getStr(cells.row["CARGO_STATUS_CODE"])
                                    }
                                    val lastIndex = data1.size - 1

                                    cargoControlSid =
                                        Util.getTableCell(lastIndex, data1, "CARGO_CONTROL_SID")
                                    acceptSelect.value =
                                        Util.getTableCell(lastIndex, data1, "CARGO_ACCEPT_SID")
                                    acceptSeq.value =
                                        Util.getTableCell(lastIndex, data1, "CARGO_ACCEPT_SEQ")

                                }
                                acceptList.value = tempList

                                agent.value = Util.getTableCell(0, data[2], "AGENT_CUSTOMER_CODE")
                                agentName.value =
                                    Util.getTableCell(0, data[2], "AGENT_CUSTOMER_NAME")
                                fltDate.value = Util.getTableCell(0, data[2], "FLIGHT_DATE")
                                fltNo.value = Util.getTableCell(0, data[2], "FLIGHT_NO")
                                fltDest.value = Util.getTableCell(0, data[2], "DESTINATION_CODE")
                                fltSelect.value = Util.getTableCell(0, data[2], "SCHEDULE_SID")
                                setPaperPCSWT()
                                data[3]?.let { data3 ->
                                    if (data3.size > 0) {
                                        customerCode = Util.getTableCell(0, data3, "CUSTOMER_CODE")
                                        agent.value = Util.getTableCell(0, data3, "AGENCY_CODE")
                                        agentName.value =
                                            Util.getTableCell(0, data3, "CUSTOMER_NAME")
                                    }

                                }
                                Common.suc.value = "조회가 완료됐습니다."
                            }
                        } else {
                            setClearAll()
                            Common.suc.value = Util.getTableCell(0, status, "COL2")
                        }
                    }
                }
            }

            if (ret is Result.Success) {
                val ret2 = api.getPWM_EC_XML_L010_001(mawb.value)

                when (ret2) {
                    is Result.Error -> Common.sendError(ret2.message)
                    is Result.Success<DataTable> -> {
                        val data = ret2.data.table

                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                            statusNo.value = data[1]?.takeIf { it.isNotEmpty() }?.let { data1 ->
                                Util.getTableCell(0, data1, "STATUSCODE") + ":" + Util.getTableCell(
                                    0,
                                    data1,
                                    "STATUSNAME"
                                )
                            } ?: "미신고"

                        } else {
                            statusNo.value = Util.getTableCell(0, data[0], "COL2", "Error")
                        }
                    }
                }
            }



            Common.loadingOff()
        }
    }

    suspend fun setPaperPCSWT() {
        if (cargoControlSid == "") {
            acceptPcs.value = ""
            acceptWt.value = ""
            mfstPcs.value = ""
            mfstWt.value = ""
            return
        }

        val ret = api.getPWM_CARGO_CONTROL_M010_001_VWT(cargoControlSid)
        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                    mfstPcs.value = Util.getTableCell(0, data[1], "MFST_NO_OF_PACKAGE")
                    mfstWt.value = Util.getTableCell(0, data[1], "MFST_NET_WEIGHT")
                    acceptPcs.value = Util.getTableCell(0, data[1], "ACCEPT_NO_OF_PACKAGE")
                    acceptWt.value = Util.getTableCell(0, data[1], "ACCEPT_NET_WEIGHT")
                    volumeWt.value = Util.getTableCell(0, data[2], "SUM_VOLUME_WEIGHT")
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2", "조회에러"))
                }
            }
        }


    }

    fun setFlight(outParam:(suc:List<DataRow>)-> Unit) {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val date = Util.formatDate_8(fltDate.value)
            val prefix = Util.getStr(mawb.value) + "   "

            val ret = biz.getPWMFlightNoWithPreFix(date, prefix.substring(0, 3))

            ret?.let {
                if (it.size == 0) {
                    Common.sendError("해당날짜의 Prefix에 해당하는 편번은 없습니다.")
                } else {
                    outParam(it)
                }

            }
            Common.loadingOff()
        }
    }

    fun setClearAll() {
        cargoControlSid = ""
        acceptSelect.value = ""
        acceptList.value = linkedMapOf()
        weight.value = ""
        fltDest.value = ""
        fltNo.value = ""
        fltDate.value = Util.formatDate_15(Common.dateTime.value)
        fltList.value = emptyList()
        fltSelect.value = ""
        agent.value = ""
        agentName.value = ""
        customerCode = ""
        pcs.value = ""
        spc.value = ""
        palletPcs.value = ""
        palletWt.value = ""
        volumeWt.value = ""
        acceptPcs.value = ""
        acceptWt.value = ""
        mfstPcs.value = ""
        mfstWt.value = ""
        statusNo.value = ""

    }

    fun saveClick(callback:(DataTable?)-> Unit) {
        if (Util.getStr(mawb.value).length != 11) {
            Common.sendError("MAWB번호를 확인하십시요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val agencyCode = Util.getStr(agent.value)
            val agencyName = Util.getStr(agentName.value)

            if (agencyCode.length > 0 && agencyName.length == 0) {
                val agencyRet = biz.getAgencyInfo(agencyCode)
                if (agencyRet != null && agencyRet.row.size > 0) {
                    customerCode = agencyRet.row["CUSTOMER_CODE"] ?: ""
                    agentName.value = agencyRet.row["CUSTOMER_NAME"] ?: ""
                } else {
                    Common.sendError("대리점 코드를 확인하십시요")
                    Common.loadingOff()
                    return@launch
                }
            }

            val wt = Util.getDouble(weight.value)
            val palletCntText = Util.getStr(palletPcs.value).trim()
            val palletCnt = Util.getDouble(palletPcs.value)

            val palletIndex = palletSelect.value ?: -1

            if (palletIndex < 0 && wt != 0.0) {
                Common.sendError("파렛트를(필수선택) 확인하십시요.")
                Common.loadingOff()
                return@launch
            }

            if (Util.getDouble(netWt.value) < 0.0) {
                Common.sendError("순중량이 (-) 입니다. PCS, WT, 파렛트 수량을 확인하여 주십시요.")
                Common.loadingOff()
                return@launch
            }

            if (palletCntText == "" || palletCnt < 0.0) {
                Common.sendError("총 파렛트 값을 확인해주세요.")
                Common.loadingOff()
                return@launch
            }

            var reloadFlag = false

            if (Util.getStr(cargoControlSid).length == 0 && Util.getStr(acceptSelect.value).length == 0) {
                val ret = api.setPWM_CARGO_ACCEPT_M010_011(
                    "0",
                    Util.getStr(mawb.value),
                    "",
                    Util.getStr(fltSelect.value),
                    "ICN",
                    Util.getStr(fltDest.value).uppercase(),
                    Util.getStr(mfstPcs.value),
                    Util.getStr(mfstWt.value),
                    Util.getStr(customerCode),
                    Util.getStr(agentName.value),
                    "N",
                    "N",
                    "N",
                    palletCntText
                )

                when (ret) {
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                            cargoControlSid = Util.getTableCell(0, data[0], "COL2")
                            acceptSelect.value = Util.getTableCell(0, data[0], "COL3")
                        } else {
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                            Common.loadingOff()
                            return@launch
                        }
                    }
                }

                reloadFlag = true

            }

            val currDt = biz.getCurDateTime()
            val scaleSelect = scaleRadioSelect.value
            val scaleList = scaleRadio.value
            val scaleSelectStr = when (scaleSelect) {
                3 -> "SCALE#X2"
                else -> scaleList[scaleSelect].row["CODE_CODE"] ?: ""
            }

            val ret2 = api.setPWM_CARGO_SCALE_M010_011(
                cargoControlSid,
                acceptSelect.value,
                scaleSelectStr,
                Common.userId.value,
                "0",
                currDt.substring(0, 8),
                currDt.substring(8, 12),
                spcSelect.value?.first ?: "",
                pcs.value,
                weight.value,
                palletList.value[palletSelect.value].row["VALUE1_NUMBER"] ?: "0",
                palletPcs.value,
                palletWt.value,
                netWt.value,
                fltDest.value,
                customerCode,
                agentName.value,
                mfstPcs.value,
                mfstWt.value
            )

            Common.loadingOff()
            when (ret2) {
                is Result.Error -> Common.sendError(ret2.message)
                is Result.Success<DataTable> -> {
                    val data = ret2.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        setClear()
                        if (reloadFlag) {
                            getAcceptList()
                        } else {
                            setPaperPCSWT()
                        }
                    } else {

                        val code = listOf("E05", "E06", "E07", "E08", "E09")
                        if (code.any { cargoStatusCode.contains(it) }) {
                            callback(ret2.data)

                        }else{
                            Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                        }
                    }
                }
            }
            callback(null)

        }
    }

    fun setClear() {
        spcSelect.value = Pair("", "")
        palletSelect.value = 0
        pcs.value = ""
        weight.value = ""
        netWt.value = ""
        palletWt.value = ""
        palletPcs.value = "1"
    }

    fun printClick(){
        val cargoStatus = cargoStatusCode
        if(cargoStatus == "E05" || cargoStatus == "E06" ||
            cargoStatus == "E07" || cargoStatus == "E08" ||
            cargoStatus == "E09"){

            var printIp = "TEST"

            val scaleSelect = scaleRadioSelect.value
            val terminalCode = Common.terminalCode.value

            val printIp1 = "SCALE#1"
            val printIp2 = "SCALE#2"
            val printIp3 = "SCALE#3"
            val printIpX2 = "SCALE#X2"

            when (scaleSelect) {
                0 -> {
                    printIp =
                        if (terminalCode == "T1") {
                            printIp1
                        } else {
                            "${printIp1}_${terminalCode}"
                        }
                }

                1 -> {
                    printIp =
                        if (terminalCode == "T1") {
                            printIp2
                        } else {
                            "${printIp2}_${terminalCode}"
                        }
                }

                2 -> {
                    printIp =
                        if (terminalCode == "T1") {
                            printIp3
                        } else {
                            "${printIp3}_${terminalCode}"
                        }
                }

                3 -> {
                    printIp =
                        if (terminalCode == "T1") {
                            printIpX2
                        } else {
                            "${printIpX2}_${terminalCode}"
                        }
                }

                else -> {
                    Common.sendError("알 수 없는 Scale 입니다.")
                    return
                }
            }

            viewModelScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = api.setPWM_CARGO_SCALE_PRINT_MAWB(cargoControlSid,printIp)

                when(ret){
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                            Common.suc.value = "출력이 완료됐습니다."
                        }else{
                            Util.getTableCell(0,data[0],"COL2")
                        }
                    }
                }

                Common.loadingOff()
            }

        }else{
            Common.sendError("입고가 되지 않았습니다. 입고 후 출력 하시기 바랍니다.")
        }
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
}