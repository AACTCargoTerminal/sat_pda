package com.aact.sat_pda.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.R
import com.aact.sat_pda.screen.import_cargo_out.IMPORT_CARGO_OUT_Model

// 251231 박제훈 추가
class LocationOutAdapter(
    private val cargoOutModel: IMPORT_CARGO_OUT_Model
) : RecyclerView.Adapter<LocationOutAdapter.LocationViewHolder>() {

    private var locationList: List<IMPORT_CARGO_OUT_Model.LocationItem> = emptyList()

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNo: TextView = itemView.findViewById(R.id.txtNo)
        val txtLocationName: TextView = itemView.findViewById(R.id.txtLocationName)
        val txtRackNo: TextView = itemView.findViewById(R.id.txtRackNo)
        val txtStockPcs: TextView = itemView.findViewById(R.id.txtStockPcs)
        val txtOutPcs: EditText = itemView.findViewById(R.id.txtOutPcs)

        private var textWatcher: TextWatcher? = null

        fun bind(item: IMPORT_CARGO_OUT_Model.LocationItem, position: Int) {
            txtNo.text = (position + 1).toString()
            txtLocationName.text = item.locationName
            txtRackNo.text = item.rackNo
            txtStockPcs.text = item.stockPcs

            // 기존 TextWatcher 제거
            textWatcher?.let { txtOutPcs.removeTextChangedListener(it) }

            // 출고수량 설정 (TextWatcher 추가 전에 설정)
            val currentValue = item.outPcs.value ?: "0"
            if (txtOutPcs.text.toString() != currentValue) {
                txtOutPcs.setText(currentValue)
            }

            // 새로운 TextWatcher 추가
            textWatcher = object : TextWatcher {
                private var isUpdating = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (isUpdating) return

                    val newValue = s.toString()
                    if (item.outPcs.value != newValue) {
                        isUpdating = true
                        item.outPcs.value = newValue
                        // 합계 자동 계산
                        cargoOutModel.calculateTotalOutPcs()
                        isUpdating = false
                    }
                }
            }
            txtOutPcs.addTextChangedListener(textWatcher)

            // 포커스 받을 때 전체 선택
            txtOutPcs.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    txtOutPcs.post { txtOutPcs.selectAll() }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_out, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locationList[position], position)
    }

    override fun getItemCount(): Int = locationList.size

    fun updateData(newList: List<IMPORT_CARGO_OUT_Model.LocationItem>) {
        locationList = newList
        notifyDataSetChanged()
    }
}
