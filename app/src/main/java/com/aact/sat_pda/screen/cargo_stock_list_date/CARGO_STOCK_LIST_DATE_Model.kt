package com.aact.sat_pda.screen.cargo_stock_list_date

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.Result

class CARGO_STOCK_LIST_DATE_Model : ViewModel() {

    val api = MainAPI_Impl()

    var isEditing = false

    val fltDate = MutableLiveData<String>(Util.formatDate_15(Common.dateTime.value))

    var listDateSelect = mapOf("SCHEDULE_SID" to "")
    val listDateHeader = listOf<HeaderDTO>(
        HeaderDTO("SCHEDULE_SID", "id"),
        HeaderDTO("FLIGHT_NO", "편명", 150),
        HeaderDTO("ESTIMATED_TIME", "출항일시", 180),
        HeaderDTO("NO_OF_MAWB", "AWS(s)", 120),
        HeaderDTO("NO_OF_PACKAGE", "수량", 100),
        HeaderDTO("NET_WEIGHT", "중량", 150),
        HeaderDTO("VOLUME_WEIGHT", "부피중량", 150),
    )

    val listSchHeader = listOf<HeaderDTO>(
        HeaderDTO("FLIGHT_NO", "편명", 150),
        HeaderDTO("MASTER_AIR_WAY_BILL_NO", "MAWB", 180),
        HeaderDTO("NO_OF_PACKAGE", "수량", 100),
        HeaderDTO("NET_WEIGHT", "중량", 150),
        HeaderDTO("VOLUME_WEIGHT", "부피중량", 150),
        HeaderDTO("ESTIMATED_TIME", "출항일시", 180),
        HeaderDTO("DESTINATION_NAME", "도착지", 100),
        HeaderDTO("CARGO_STATUS_NAME", "상태", 150),
    )


    suspend fun setListDate(): List<DataRow>? {

        val ret = api.getPWM_CARGO_STOCK_L010_001(fltDate.value.replace("-", ""))

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    data[1]?.let { table ->
                        table.forEach { row ->
                            val tmp = row.row["ESTIMATED_TIME"]

                            if(!tmp.isNullOrEmpty()){
                                if(tmp.length >= 5){
                                    row.row["ESTIMATED_TIME"] = tmp.substring(5)
                                }
                            }
                        }
                        return table
                    }

                    return emptyList()
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }

    suspend fun setListSch(sid:String): List<DataRow>? {

        val ret = api.getPWM_CARGO_STOCK_L010_002(fltDate.value.replace("-", ""), sid)

        when (ret) {
            is Result.Error -> Common.sendError(ret.message)
            is Result.Success<DataTable> -> {
                val data = ret.data.table
                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    data[1]?.let { table ->
                        table.forEach { row ->

                            val tmp = row.row["ESTIMATED_TIME"]

                            if(!tmp.isNullOrEmpty()){
                                if(tmp.length >= 5){
                                    row.row["ESTIMATED_TIME"] = tmp.substring(5)
                                }
                            }
                        }
                        return table
                    }

                    return emptyList()
                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }

        return null
    }


}