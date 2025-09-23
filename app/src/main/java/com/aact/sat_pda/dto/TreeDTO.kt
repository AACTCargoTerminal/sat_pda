package com.aact.sat_pda.dto

sealed class TreeDTO {

    data class Group(
        val key: String,
        val value: String,
        val itemArray: MutableList<Item> = mutableListOf(),
        var isExpanded: Boolean = false
    ) : TreeDTO()

    data class Item(val key: String, val value: String, var changeFlag: String = "N",var checkFlag: String) : TreeDTO()

}