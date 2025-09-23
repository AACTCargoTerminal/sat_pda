package com.aact.sat_pda.dto

data class DataTable(
    val table: LinkedHashMap<Int, MutableList<DataRow>>
)

data class DataRow(
    val row: LinkedHashMap<String, String>
)