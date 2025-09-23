package com.aact.sat_pda.screen.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MenuModel : ViewModel() {
    val biz = BizAPI()
    val api: MainAPI = MainAPI_Impl()
    val mainItem = MutableLiveData<List<DataRow>>(emptyList())
    val selectMain = MutableLiveData<String>("")

    fun getMainItem() {

        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = biz.getCommonCode("PDAGR", "")
            if (ret == null) {
                mainItem.value = emptyList()
            } else {
                mainItem.value = ret
            }

            Common.loadingOff()
        }
    }

}