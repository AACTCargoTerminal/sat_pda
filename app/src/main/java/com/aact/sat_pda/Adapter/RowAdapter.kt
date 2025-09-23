package com.aact.sat_pda.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.Biz.Item
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.HeaderDTO
import kotlin.apply
import kotlin.collections.all
import kotlin.collections.any
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.lastIndex
import kotlin.collections.set
import kotlin.text.format
import kotlin.text.toDouble
import kotlin.text.trimEnd

class RowAdapter(
    private val rows: List<DataRow>,
    private val header: List<HeaderDTO>,
    private var select: Map<String, String>,
    private val selectMap: MutableMap<Int, DataRow> = mutableMapOf(),
    private val onClick: (value: Map<String, String>) -> Unit
) :
    RecyclerView.Adapter<RowAdapter.RowViewHolder>() {

    class RowViewHolder(val layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    var selectPosition = -1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            50
        ).apply {
            setMargins(
                2,
                0,
                2,
                0
            )
        }
        val row = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#00FFFFFF"))
            layoutParams = params
        }

        return RowViewHolder(row)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val dto = rows[position].row

        holder.layout.removeAllViews()
        holder.layout.isClickable = true
        holder.layout.isFocusable = false


        val isSelected = select.all { (key, value) ->
            dto.any { it.key == key && it.value == value }
        }

        if (isSelected) {
            selectPosition = position
            holder.layout.setBackgroundColor(Color.parseColor("#D3D3D3"))
        } else {
            holder.layout.setBackgroundColor(Color.TRANSPARENT) //Color.TRANSPARENT
        }

        holder.layout.setOnClickListener {
            val tempMap = mutableMapOf<String, String>()

            if (selectPosition == position) {
                dto.forEach { dtoIt ->
                    if (select.containsKey(dtoIt.key)) {
                        tempMap[dtoIt.key] = ""

                    }
                }
            } else {
                dto.forEach { dtoIt ->
                    if (select.containsKey(dtoIt.key)) {
                        tempMap[dtoIt.key] = dtoIt.value

                    }
                }
            }

            updateSelect(tempMap,position)
        }

        val item = Item()

        header.forEachIndexed { index, it ->
            val findData = dto[it.key]
            if (findData != null && it.avg != 0) {
                var cust_row: LinearLayout
                if (it.editFlag) {

                    cust_row = item.getTableRowEdit(
                        context = holder.layout.context,
                        value = findData, //formatSmartDecimal(findData)
                        w = it.avg
                    ){ str ->
                        val tmp = selectMap[position]

                        if(tmp == null){
                            val dataRow = DataRow(linkedMapOf(it.key to str))
                            selectMap[position] = dataRow
                        }else{
                            tmp.row[it.key] = str
                        }
                    }
                    cust_row.apply {
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }
                }else if(it.isChk){
                    val dtoIndex = selectMap[position]
                    var value = dtoIndex?.row[it.key] ?: findData
                    cust_row = item.getTableRowChk(context = holder.layout.context,
                        value = if(value=="Y") true else false,
                        w = it.avg) { flag ->
                        val tmp = selectMap[position]
                        val flag_tmp = if(flag) "Y" else "N"

                        if(tmp == null){
                            val dataRow = DataRow(linkedMapOf(it.key to flag_tmp))
                            selectMap[position] = dataRow
                        }else{
                            tmp.row[it.key] = flag_tmp
                        }
                    }
                    cust_row.apply {
                        isClickable = true // ✅ 클릭 방지
                        isFocusable = true
                    }
                }else {
                    cust_row = item.getTableRow(
                        context = holder.layout.context,
                        value = findData, //formatSmartDecimal(findData)
                        w = it.avg
                    )
                    cust_row.apply {
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }
                }


                holder.layout.addView(cust_row)
                if (index != header.lastIndex) {
                    val dividerDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(Color.parseColor("#DDDDDD")) // 회색
                        cornerRadius = 4f // 끝 둥글게
                    }

                    val divider = View(holder.layout.context).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(
                                3,
                                30
                            ).apply {
                                setMargins(0, 5, 0, 5)
                            }
                        background = dividerDrawable
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }

                    holder.layout.addView(divider)
                }
            }else if(findData == null && it.avg != 0){
                var cust_row: LinearLayout
                if (it.editFlag) {
                    cust_row = item.getTableRowEdit(
                        context = holder.layout.context,
                        value = "", //formatSmartDecimal(findData)
                        w = it.avg
                    ){ str ->
                        val tmp = selectMap[position]

                        if(tmp == null){
                            val dataRow = DataRow(linkedMapOf(it.key to str))
                            selectMap[position] = dataRow
                        }else{
                            tmp.row[it.key] = str
                        }
                    }
                    cust_row.apply {
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }
                }else if(it.isChk){
                    cust_row = item.getTableRowChk(context = holder.layout.context,
                        value = if(findData=="Y") true else false,
                        w = it.avg) { flag ->
                        val tmp = selectMap[position]
                        val flag_tmp = if(flag) "Y" else "N"

                        if(tmp == null){
                            val dataRow = DataRow(linkedMapOf(it.key to flag_tmp))
                            selectMap[position] = dataRow
                        }else{
                            tmp.row[it.key] = flag_tmp
                        }
                    }
                    cust_row.apply {
                        isClickable = true
                        isFocusable = true
                    }
                }else {
                    cust_row = item.getTableRow(
                        context = holder.layout.context,
                        value = "", //formatSmartDecimal(findData)
                        w = it.avg
                    )
                    cust_row.apply {
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }
                }


                holder.layout.addView(cust_row)
                if (index != header.lastIndex) {
                    val dividerDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(Color.parseColor("#DDDDDD")) // 회색
                        cornerRadius = 4f // 끝 둥글게
                    }

                    val divider = View(holder.layout.context).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(
                                3,
                                30
                            ).apply {
                                setMargins(0, 5, 0, 5)
                            }
                        background = dividerDrawable
                        isClickable = false // ✅ 클릭 방지
                        isFocusable = false
                    }

                    holder.layout.addView(divider)
                }
            }

        }
    }

    override fun getItemCount(): Int = rows.size

    fun updateSelect(newSelect: Map<String, String>,position: Int) {
        select = newSelect
        onClick(newSelect)
        notifyItemChanged(selectPosition)
        notifyItemChanged(position)
        if(position == selectPosition){
            selectPosition = -1
        }else{
            selectPosition = position
        }


    }

    fun formatSmartDecimal(str: String): String {
        return try {
            val value = str.toDouble()
            if (value % 1.0 == 0.0) {
                value.toInt().toString()
            } else {
                String.format("%.3f", value).trimEnd('0').trimEnd('.')
            }
        } catch (e: NumberFormatException) {
            str
        }
    }

}