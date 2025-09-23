package com.aact.sat_pda.appconfig

import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.HeaderDTO

sealed class SubContent {

    data class GetTable(
        var bodylist: List<DataRow>,
        val headerList: List<HeaderDTO>,
        val selectValue: Map<String, String>,
        val onClick : (value:Map<String, String>)-> Unit,
        val endClick : () -> Unit
    ) : SubContent()

    object Calculate: SubContent()

    object ContentNone : SubContent()
}