package com.aact.sat_pda.screen.stock_list_all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class STOCK_LIST_ALL_Model : ViewModel() {

    val api = MainAPI_Impl()

    val stockHeader = listOf<HeaderDTO>(
        HeaderDTO("INOUT_TIME", "반입일자", 220),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 200),
        HeaderDTO("NO_OF_PACKAGE", "수량", 80),
        HeaderDTO("NET_WEIGHT", "순중량", 120),
        HeaderDTO("FLIGHT_NO", "편명", 120),
        HeaderDTO("ESTIMATED_TIME", "ETD", 220),
        HeaderDTO("STAY_TIMES", "경과시간", 150),
        HeaderDTO("SPECIAL_CARGO_NAME", "특수화물", 150),
    )


    fun getInfo(outParam: (array: List<DataRow>) -> Unit) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = api.getPWM_CARGO_STOCK_L020_001()

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        data[1]?.let { table ->
                            table.forEach { rows ->
                                rows.row["INOUT_TIME"] = Util.formatDate_17(rows.row["INOUT_TIME"]?:"")
                                rows.row["ESTIMATED_TIME"] = Util.formatDate_17(rows.row["ESTIMATED_TIME"]?:"")
                            }
                            outParam(table)
                            Common.loadingOff()
                            return@launch
                        }

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            outParam(emptyList())
            Common.loadingOff()
        }
    }


}