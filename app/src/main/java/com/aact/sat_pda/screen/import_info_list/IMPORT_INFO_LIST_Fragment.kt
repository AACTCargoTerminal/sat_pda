package com.aact.sat_pda.screen.import_info_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportInfoListBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IMPORT_INFO_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentImportInfoListBinding? = null
    private val binding get() = _binding!!
    private val infoListModel: IMPORT_INFO_LIST_Model by lazy { IMPORT_INFO_LIST_Model()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportInfoListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = infoListModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fltDateEdit = binding.fltdate.editText
        fltDateEdit.showSoftInputOnFocus = false
        item.disableEditText(binding.i00Count.editText)
        item.disableEditText(binding.i02Count.editText)
        item.disableEditText(binding.i03Count.editText)

        infoListModel.cargoList.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                infoListModel.cargoSelect["CARGO_CONTROL_SID"] = Util.getStr(it[0].row["CARGO_CONTROL_SID"])
                infoListModel.cargoSelect["CARGO_CONTROL_NO"] = Util.getStr(it[0].row["CARGO_CONTROL_NO"])
                infoListModel.cargoSelect["STATUS_NAME"] = Util.getStr(it[0].row["STATUS_NAME"])
                infoListModel.cargoSelect["STATUS_CODE"] = Util.getStr(it[0].row["STATUS_CODE"])
            } else {
                infoListModel.cargoSelect["CARGO_CONTROL_SID"] = ""
                infoListModel.cargoSelect["CARGO_CONTROL_NO"] = ""
                infoListModel.cargoSelect["STATUS_NAME"] = ""
                infoListModel.cargoSelect["STATUS_CODE"] = ""
            }

            binding.cargoList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = infoListModel.infoListHeader,
                bodyList = it,
                selectValue = infoListModel.cargoSelect,
                height = 240,
            ) {params ->
                infoListModel.cargoSelect =
                    Util.validSelectTable(infoListModel.cargoSelect, params).toMutableMap()
            }
            binding.cargoList.custItem.addView(table)
        }




        infoListModel.fltList.observe(viewLifecycleOwner) {dataRowList ->
            val stringList = dataRowList.map { it.row["FLIGHT_NO"] ?: ""}

            // 저장된 편번 위치 찾기
            val savedFltNo = infoListModel.fltNo.value ?: ""
            val savedPosition = if (savedFltNo.isNotEmpty()) stringList.indexOf(savedFltNo) else -1

            item.setSpiner_Str(view.context, binding.fltno.combo, stringList) { selectedNo ->
                val selectedRow = dataRowList.find { it.row["FLIGHT_NO"] == selectedNo}
                infoListModel.scheduleSid = selectedRow?.row?.get("SCHEDULE_SID") ?: ""
                infoListModel.fltNo.value = selectedNo
                infoListModel.setList()
            }

            // 저장된 편번이 있으면 해당 position 선택
            if (savedPosition >= 0) {
                binding.fltno.combo.setSelection(savedPosition)
            }
        }


        infoListModel.fltDate.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                infoListModel.getFlight()
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
            infoListModel.fltDate.value = result

            isEditing = false
        }

    }


    override fun onStart() {
        super.onStart()
        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "FLIGHT_NO" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            val temp3 =
                route.params.find { it.first == "FLT_DATE" }?.second ?: ""
            val temp4 =
                route.params.find {it.first == "SCHEDULE_SID"}?.second ?: ""


            infoListModel.fltNo.value = temp
            infoListModel.cargoSid.value = temp2
            infoListModel.fltDate.value = temp3
            infoListModel.scheduleSid = temp4
        }

        if (infoListModel.cargoSid.value.isNotEmpty()) {
            infoListModel.setList()
        }

        if (infoListModel.scheduleSid.isNotEmpty()) {
            infoListModel.setList()
        }



        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Add -> {
                    item.onClick = fun(){
                     if (keboardFlag) {
                         keyboardCtl()
                     }
                        val selectedSid = infoListModel.cargoSelect["CARGO_CONTROL_SID"]
                        println("선택된 SID: $selectedSid")
                        if (selectedSid.isNullOrEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        val chkStatus = infoListModel.cargoSelect["STATUS_NAME"]?: ""
                        println("선택된 SID: $chkStatus")
                        if (chkStatus != "검수") {
                            Common.sendError("B/D 작업을 먼저 해 주시기 바랍니다.")
                            return
                        }

                        Route.ImportInfoList.params = listOf(
                            Pair("FLT_DATE", infoListModel.fltDate.value ?: ""),
                            Pair("SCHEDULE_SID",infoListModel.scheduleSid),
                            Pair("FLIGHT_NO", infoListModel.fltNo.value ?: "")
                        )

                        val params = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", selectedSid),
                            Pair("CARGO_CONTROL_NO", infoListModel.cargoSelect["CARGO_CONTROL_NO"] ?: ""),
                            Pair("FLT_DATE", infoListModel.fltDate.value ?: "")
                        )

                        val rt = Route.ImportCargoIn
                        rt.params = params
                        Common.addNavigate(rt)

                    }
                 }

                is BottomItem.Save -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        val selectedNo = infoListModel.cargoSelect["CARGO_CONTROL_NO"]
                        println("선택된 SID: $selectedNo")
                        if (selectedNo.isNullOrEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        Route.ImportInfoList.params = listOf(
                            Pair("FLT_DATE", infoListModel.fltDate.value ?: ""),
                            Pair("SCHEDULE_SID",infoListModel.scheduleSid),
                            Pair("FLIGHT_NO", infoListModel.fltNo.value ?: "")
                        )

                        val params = listOf<Pair<String, String>> (
                            Pair("CARGO_CONTROL_NO", selectedNo),
                            Pair("FLT_DATE", infoListModel.fltDate.value ?: "")
                        )
                        val rt = Route.ImportBDWithNo
                        rt.params = params
                        Common.addNavigate(rt)
                    }
                }

                else -> null
            }
        }

    }


}