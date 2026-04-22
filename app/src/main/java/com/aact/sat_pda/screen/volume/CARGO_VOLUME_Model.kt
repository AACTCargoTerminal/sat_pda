package com.aact.sat_pda.screen.volume

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.text.isNullOrEmpty
import com.aact.sat_pda.dto.Result

class CARGO_VOLUME_Model : ViewModel() {

    val api = MainAPI_Impl()

    var cargoControlSid = ""
    var acceptSid = ""
    var isEditing = false

    val mawb = MutableLiveData<String>("")
    val fltDate = MutableLiveData<String>("")
    val fltNo = MutableLiveData<String>("")
    val w = MutableLiveData<String>("")
    val h = MutableLiveData<String>("")
    val d = MutableLiveData<String>("")
    val pcs = MutableLiveData<String>("")
    val volumn = MutableLiveData<String>("")
    val volumnWt = MutableLiveData<String>("")
    val pcsSum = MutableLiveData<String>("")
    val wtSum = MutableLiveData<String>("")

    val acceptList = MutableLiveData<List<DataRow>>(emptyList())
    val acceptSelect = MutableLiveData<Int>(-1)

    val volumeHeader = listOf(
        HeaderDTO("VOLUME_SEQ", "NO", 80),
        HeaderDTO("VOLUME_TIME", "id"),
        HeaderDTO("DIMENSION_WIDTH", "가로", 80),
        HeaderDTO("DIMENSION_LENGTH", "세로", 80),
        HeaderDTO("DIMENSION_HEIGHT", "높이", 80),
        HeaderDTO("NO_OF_PACKAGE", "수량", 80),
        HeaderDTO("VOLUME_WEIGHT", "V W/T", 120),
        HeaderDTO("VOLUME", "Vol", 200),
    )
    val volumeList = MutableLiveData<List<DataRow>>(emptyList())
    val volumeSelect = MutableLiveData<Map<String, String>>(
        mapOf(
            "VOLUME_SEQ" to "",
            "DIMENSION_WIDTH" to "",
            "DIMENSION_HEIGHT" to "",
            "DIMENSION_LENGTH" to "",
            "NO_OF_PACKAGE" to "",
            "VOLUME_WEIGHT" to "",
            "VOLUME" to "",
        )
    )

    fun getAcceptList() {
        val mawb_tmp = mawb.value
        if (mawb_tmp.length < 11) {
            Common.sendError("MAWB 입력하여주세요")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPCM_GET_CARGO_ACCEPT_BY_MAWB(mawb_tmp)
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        val data1 = data[1]

                        if (data1.isNullOrEmpty()) {
                            cargoControlSid = ""
                            acceptSid = ""
                            acceptSelect.value = -1
                            acceptList.value = emptyList()


                        } else {
                            val select = data1.size - 1
                            cargoControlSid = data1[select].row["CARGO_CONTROL_SID"] ?: ""
                            acceptSid = data1[select].row["CARGO_ACCEPT_SID"] ?: ""
                            acceptSelect.value = select
                            acceptList.value = data1


                        }

                        fltDate.value = Util.getTableCell(0, data[2], "FLIGHT_DATE")
                        fltNo.value = Util.getTableCell(0, data[2], "FLIGHT_NO")
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setList() {
        if (acceptSid.length == 0) {
            Common.sendError("선택한 접수 목록이 없습니다.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_VOLUME_M010_002(acceptSid)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        val data1 = data[1]

                        if (data1.isNullOrEmpty()) {
                            volumeList.value = emptyList()

                        } else {
                            volumeList.value = data1
                        }
                        volumeSelect.value = mapOf(
                            "VOLUME_SEQ" to "",
                            "NO_OF_PACKAGE" to "",
                            "DIMENSION_WIDTH" to "",
                            "DIMENSION_LENGTH" to "",
                            "DIMENSION_HEIGHT" to "",
                            "VOLUME_WEIGHT" to "",
                            "VOLUME" to "",
                        )

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setSave(callback: (Boolean) -> Unit) {
        val volumeSelect = volumeSelect.value["VOLUME_SEQ"] ?: ""

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_VOLUME_M010_011(
                acceptSid,
                volumeSelect,
                pcs.value,
                w.value,
                h.value,
                d.value,
                volumn.value,
                volumnWt.value
            )
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        setList()
                        setInputClear()
                        callback(true)
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setDelete(seq: String) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_VOLUME_M010_021(acceptSid, seq, "Y")

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "Volume 삭제완료"
                        setList()
                        setInputClear()
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun setCompute() {
        val volume =
            Util.getDouble(w.value) * Util.getDouble(h.value) * Util.getDouble(d.value) * Util.getDouble(
                pcs.value
            )

        volumn.value =
            String.format("%.3f", ((Math.ceil((volume / 1000.0) - 0.4) * 1000.0) / 1000000.0))
        volumnWt.value = String.format("%.1f", (volume / 6000.0))
    }

    fun setInputClear() {
        w.value = ""
        h.value = ""
        d.value = ""
        pcs.value = ""
        volumn.value = ""
        volumnWt.value = ""
    }

    fun volumelistClick(param: Map<String, String>) {
        val volumeSeq = param["VOLUME_SEQ"] ?: ""
        val volumeW = param["DIMENSION_WIDTH"] ?: ""
        val volumeH = param["DIMENSION_LENGTH"] ?: ""
        val volumeD = param["DIMENSION_HEIGHT"] ?: ""
        val volumePcs = param["NO_OF_PACKAGE"] ?: ""
        val volume = param["VOLUME"] ?: ""
        val volumeWt = param["VOLUME_WEIGHT"] ?: ""

        if (acceptSid.length == 0 || volumeSeq.isNullOrEmpty()) {
            return
        }

        setInputClear()

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_VOLUME_M010_001(cargoControlSid, acceptSid, volumeSeq)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        isEditing = true

                        w.value = Util.getTableCell(0, data[1], "DIMENSION_WIDTH", volumeW)
                        h.value = Util.getTableCell(0, data[1], "DIMENSION_LENGTH", volumeH)
                        d.value = Util.getTableCell(0, data[1], "DIMENSION_HEIGHT", volumeD)
                        pcs.value = Util.getTableCell(0, data[1], "NO_OF_PACKAGE", volumePcs)
                        volumn.value = Util.getTableCell(0, data[1], "VOLUME", volume)
                        volumnWt.value = Util.getTableCell(0, data[1], "VOLUME_WEIGHT", volumeWt)

                        isEditing = false
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }


    }

    fun printClick() {
        var ip = "CARGOACCEPT"
        if (Common.terminalCode.value == "T2") {
            ip = "CARGOACCEPT_T2"
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.setPWM_CARGO_VOLUME_PRINT_MAWB(cargoControlSid, ip)

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        Common.suc.value = "출력이 완료됫습니다."

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }

    }
}