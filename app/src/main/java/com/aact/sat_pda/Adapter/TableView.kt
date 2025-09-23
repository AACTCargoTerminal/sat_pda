package com.aact.sat_pda.Adapter

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.HeaderDTO
import kotlin.apply
import kotlin.collections.mapValues

class TableView(
    val data: List<List<DataRow>>,
    private val header: List<HeaderDTO>,
    private var select: Map<String, String>,
    private val selectMap: MutableMap<Int, DataRow>,
    private val onClick: (value: Map<String, String>) -> Unit
) : RecyclerView.Adapter<TableView.PageViewHolder>() {
    inner class PageViewHolder(val recyclerView: RecyclerView) :
        RecyclerView.ViewHolder(recyclerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val recyclerView = RecyclerView(parent.context).apply {
            layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.VERTICAL, false)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.TRANSPARENT)
        }
        val divider = DividerItemDecoration(parent.context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        return PageViewHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val rowList = data[position]
        holder.recyclerView.removeAllViews()

        holder.recyclerView.adapter = RowAdapter(rowList, header, select, selectMap) {
            updateSelect(it)
        }
    }

    fun updateSelect(newSelect: Map<String, String>) {
        select = newSelect
        onClick(newSelect)
        notifyDataSetChanged() // 모든 페이지(RowAdapter) 갱신
    }

    override fun getItemCount(): Int = data.size
}