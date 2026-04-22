package com.aact.sat_pda.screen.import_bd_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.databinding.FragmentImportBdListBinding
import com.aact.sat_pda.screen.BaseFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class IMPORT_BD_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentImportBdListBinding? = null
    private val binding get() = _binding!!
    private val bdListModel: IMPORT_BD_LIST_Model by lazy {IMPORT_BD_LIST_Model()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportBdListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = bdListModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fltDateEdit = binding.fltdate.editText
        fltDateEdit.showSoftInputOnFocus = false


        bdListModel.fltList.observe(viewLifecycleOwner) { dataRowList ->
            val stringList = dataRowList.map { it.row["FLIGHT_NO"] ?: "" }
            item.setSpiner_Str(view.context, binding.fltno.combo, stringList) { selectedNo ->
                val selectedRow = dataRowList.find { it.row["FLIGHT_NO"] == selectedNo}
                bdListModel.scheduleSid = selectedRow?.row?.get("SCHEDULE_SID") ?: ""
                bdListModel.fltNo.value = selectedNo
                bdListModel.setList()
            }
        }

        bdListModel.fltDate.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                bdListModel.setFlight()
            }
        }

        var isEditing = false

        action.textChange_after(fltDateEdit) { s ->
            if (isEditing) return@textChange_after
            isEditing = true

            val str = s?.toString() ?: ""

            val result = Util.validDate(str)
            fltDateEdit.setText(result)
            fltDateEdit.setSelection(result.length)
            bdListModel.fltDate.value = result

            isEditing = false
        }

        bdListModel.cargoList.observe(viewLifecycleOwner) {

            bdListModel.cargoSelect["CARGO_CONTROL_NO"] =
                if (it.isNotEmpty()) Util.getStr(it[0].row["CARGO_CONTROL_NO"]) else ""

            binding.cargoList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = bdListModel.cargoListHeader,
                bodyList = it,
                selectValue = bdListModel.cargoSelect,
                height = 240,
            ) {params ->
                bdListModel.cargoSelect =
                    Util.validSelectTable(bdListModel.cargoSelect, params).toMutableMap()
            }
            binding.cargoList.custItem.addView(table)
        }



    }
}