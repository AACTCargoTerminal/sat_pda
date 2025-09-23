package com.aact.sat_pda.screen.login

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.dto.DataTable
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.aact.sat_pda.dto.Result
import java.util.concurrent.CancellationException

class LoginModel : ViewModel() {

    val api: MainAPI = MainAPI_Impl()

    fun loginClick(debug: Boolean) {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val ret = api.getUserInfoPDA(Common.userId.value)

            when (ret) {
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    data[0]?.let {
                        if (it.isNotEmpty()) {
                            if (it[0].row["COL1"] == "OK") {

                                data[1]?.let { loginData ->
                                    if (loginData[0].row["TERMINAL_CODE_WORK"] != Common.terminalCode.value && Util.getStr(
                                            loginData[0].row["TERMINAL_CODE_WORK"]
                                        ) != "" && debug
                                    ) {
                                        Common.sendError("터미널코드를 확인해주세요")
                                        Common.loadingOff()
                                        return@launch
                                    } else {
                                        changeLogin(loginData[0].row)
                                        when (val retSub =
                                            Util.formatDate_14(it[0].row["COL3"].toString())) {
                                            is Result.Success -> {
                                                val biz = BizAPI()
                                                biz.setAuth()

                                                Common.dateTime.value = retSub.data
                                                //메뉴 가기 추가
                                                Common.sound.value = "SUC"
                                                Common.route.value = Route.Menu

                                            }

                                            is Result.Error -> {
                                                Common.dateTime.value = ""
                                                Common.loadingOff()
                                                Common.sendError(retSub.message)
                                                return@launch
                                            }
                                        }
                                    }
                                }

                            } else {
                                Common.sendError((it[0].row["COL2"] ?: "로그인에러"))
                            }
                        }
                    }
                }

                is Result.Error -> {
                    Common.sendError(ret.message)
                }
            }
            Common.loadingOff()
        }

    }

    fun changeLogin(data: Map<String, String>) {
        Common.userSid.value = data["USER_SID"]?.toLongOrNull() ?: 0L
        Common.userId.value = data["USER_ID"] ?: ""
        Common.userName.value = data["USER_NAME"] ?: ""
        Common.langCode.value = data["DEFAULT_LANGUAGE_CODE"] ?: ""
        Common.langName.value = data["DEFAULT_LANGUAGE_NAME"] ?: ""
        Common.companyCode.value = data["COMPANY_CODE"] ?: ""
        Common.companyName.value = data["COMPANY_NAME"] ?: ""
        Common.branchCode.value = data["BRANCH_CODE"] ?: ""
        Common.branchName.value = data["BRANCH_NAME"] ?: ""
        Common.departCode.value = data["DEPARTMENT_CODE"] ?: ""
        Common.departName.value = data["DEPARTMENT_NAME"] ?: ""
        Common.userAuth_In_Cancel_Yn.value = data["AUTH_IN_CANCEL_YN"] ?: ""
        Common.terminalCode.value = data["TERMINAL_CODE_WORK"] ?: "ALL"
        Common.terminalName.value = data["TERMINAL_NAME_WORK"] ?: "ALL"
        Common.userTerminalIp.value =
            Common.terminalCode.value + ":" + Common.userIp.value
    }


}