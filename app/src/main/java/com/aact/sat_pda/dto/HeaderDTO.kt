package com.aact.sat_pda.dto

data class HeaderDTO(
    val key:String,
    val value:String,
    var avg:Int = 0,
    var editFlag : Boolean = false,
    var isChk : Boolean = false
)