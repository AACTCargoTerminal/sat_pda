package com.aact.sat_pda.screen.info

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.HeaderDTO

data class ItemDTO(
    val name: String,
    var view: View?,
    val data: MutableLiveData<*>
)

class InfoModel : ViewModel() {
    val headerList = listOf<HeaderDTO>(HeaderDTO("ip", "IP", 315), HeaderDTO("name", "서버명", 150))

    val infoList = mapOf(
        "userId" to
                ItemDTO("ID", null, Common.userId), "userName" to
                ItemDTO(

                    "사용자명", null,
                    Common.userName
                ), "companyName" to
                ItemDTO("법인", null, Common.companyName), "branchName" to
                ItemDTO(

                    "지사", null,
                    Common.branchName
                ), "departName" to
                ItemDTO("부서", null, Common.departName), "langName" to
                ItemDTO(
                    "언어", null,
                    Common.langName
                ), "terminalName" to
                ItemDTO("터미널명", null, Common.terminalName), "terminalCode" to
                ItemDTO("터미널코드", null, Common.terminalCode), "userIp" to
                ItemDTO("IP", null, Common.userIp), "timeout" to
                ItemDTO(

                    "Timeout",
                    null,
                    MutableLiveData<Long>(Common.timeout)
                ), "dateTime" to
                ItemDTO("DateTime", null, Common.dateTime), "permissionFlag" to
                ItemDTO("PDA권한", null, Common.permissionFlag),
                "version" to
                ItemDTO("Version", null, Common.version), "memo" to
                ItemDTO("Memo", null, Common.memo)
    )
}