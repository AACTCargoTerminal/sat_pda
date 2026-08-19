package com.aact.sat_pda.screen.pallet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PALLET_UPDATE_Model : ViewModel() {
    val api = MainAPI_Impl()
    val biz = BizAPI()
    var isEditing = false

    var cargoControlSid = ""
    var acceptSid = ""

    val mawb = MutableLiveData<String>("")
    val oldPalletPcs = MutableLiveData<String>("")
    val newPalletPcs = MutableLiveData<String>("")

    fun setUpdate(callback: (Boolean) -> Unit) {
        if (acceptSid.isEmpty()) {
            Common.sendError("수정할 접수정보가 없습니다.")
            return
        }

        val newPalletValue = Util.getStr(newPalletPcs.value)

        if (newPalletValue.isEmpty()) {
            Common.sendError("수정할 파렛트 수량을 입력하여주세요.")
            return
        }

        val oldPalletCount = Util.getInt( Util.getStr(oldPalletPcs.value) )

        val palletCount = Util.getInt(newPalletValue)

        if (palletCount < 0) {
            Common.sendError("파렛트 수량을 확인하여주세요.")
            return
        }

        if (oldPalletCount == palletCount) {
            Common.sendError("변경된 값이 없습니다. 저장되지 않았습니다.")
            return
        }

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret =
                api.setPWM_CARGO_ACCEPT_M010_013(
                    acceptSid,
                    palletCount.toString()
                )

            when (ret) {
                is Result.Error -> {
                    Common.sendError(ret.message)
                }

                is Result.Success<DataTable> -> {
                    val data = ret.data.table

                    if (
                        Util.getTableCell(0,data[0],"COL1") == "OK"){
                        oldPalletPcs.value = palletCount.toString()
                        newPalletPcs.value = ""
                        callback(true)
                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }
}