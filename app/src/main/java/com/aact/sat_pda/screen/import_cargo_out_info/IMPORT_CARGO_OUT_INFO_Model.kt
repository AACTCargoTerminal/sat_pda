package com.aact.sat_pda.screen.import_cargo_out_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_CARGO_OUT_INFO_Model : ViewModel() {
    val api = MainAPI_Impl()

    var cargoSid = ""
    val cargoNo = MutableLiveData("")
    val mawb = MutableLiveData("")
    val hawb = MutableLiveData("")

    // D/O (Delivery Order) 수량
    val doPcs = MutableLiveData("")
    val doWt = MutableLiveData("")

    // 반출승인 (Release Approval) 수량
    val releasePcs = MutableLiveData("")
    val releaseWt = MutableLiveData("")

    // 화물번호로 정보 조회
    fun setInfoByNo() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_RELEASE_M020_001_BY_NO(cargoNo.value)

            when(ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {
                        Common.suc.value = "조회 완료"

                        cargoSid = Util.getTableCell(0, data[1], "CARGO_CONTROL_SID")
                        mawb.value = Util.getTableCell(0, data[1], "MASTER_AIR_WAY_BILL_NO")
                        hawb.value = Util.getTableCell(0, data[1], "HOUSE_AIR_WAY_BILL_NO")

                        // D/O 수량 (재고 수량으로 설정)
                        doPcs.value = Util.getTableCell(0, data[1], "STOCK_NO_OF_PACKAGE")
                        doWt.value = Util.getTableCell(0, data[1], "STOCK_NET_WEIGHT")

                        // 반출승인 수량 (출고 수량으로 설정)
                        releasePcs.value = Util.getTableCell(0, data[1], "RELEASED_NO_OF_PACKAGE")
                        releaseWt.value = Util.getTableCell(0, data[1], "RELEASED_NET_WEIGHT")

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    // 화면 초기화
    fun setClear() {
        cargoSid = ""
        cargoNo.value = ""
        mawb.value = ""
        hawb.value = ""
        doPcs.value = ""
        doWt.value = ""
        releasePcs.value = ""
        releaseWt.value = ""
    }
}
