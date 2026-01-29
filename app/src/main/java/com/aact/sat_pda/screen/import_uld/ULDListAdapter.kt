package com.aact.sat_pda.screen.import_uld

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.R
import com.aact.sat_pda.dto.DataRow

class ULDListAdapter : RecyclerView.Adapter<ULDListAdapter.ULDViewHolder>() {

    private var uldList: List<DataRow> = emptyList()
    private var onItemClickListener: ((DataRow) -> Unit)? = null

    fun setData(list: List<DataRow>) {
        uldList = list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (DataRow) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ULDViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_uld_list, parent, false)
        return ULDViewHolder(view)
    }

    override fun onBindViewHolder(holder: ULDViewHolder, position: Int) {
        val item = uldList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = uldList.size

    class ULDViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtULD: TextView = itemView.findViewById(R.id.txtULD)
        private val txtFltDt: TextView = itemView.findViewById(R.id.txtFltDt)
        private val txtFltNo: TextView = itemView.findViewById(R.id.txtFltNo)
        private val txtOrg: TextView = itemView.findViewById(R.id.txtOrg)

        fun bind(item: DataRow) {
            // TODO: 실제 컬럼명으로 수정 필요
            txtULD.text = item.row["ULD_NO"] ?: ""
            txtFltDt.text = item.row["FLIGHT_DATE"] ?: ""
            txtFltNo.text = item.row["FLIGHT_NO"] ?: ""
            txtOrg.text = item.row["ORIGIN_CODE"] ?: ""
        }
    }
}
