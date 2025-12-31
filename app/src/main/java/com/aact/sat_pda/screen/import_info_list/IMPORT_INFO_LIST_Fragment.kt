package com.aact.sat_pda.screen.import_info_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportInfoListBinding
import com.aact.sat_pda.screen.BaseFragment

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


        binding.fltdate.editText.showSoftInputOnFocus = false
        item.disableEditText(binding.i00Count.editText)
        item.disableEditText(binding.i02Count.editText)
        item.disableEditText(binding.i03Count.editText)

        infoListModel.cargoList.observe(viewLifecycleOwner) {

            infoListModel.cargoSelect["CARGO_CONTROL_SID"] =
                if (it.isNotEmpty()) Util.getStr(it[0].row["CARGO_CONTROL_SID"]) else ""

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
            item.setSpiner_Str(view.context, binding.fltno.combo, stringList) { selectedNo ->
                val selectedRow = dataRowList.find { it.row["FLIGHT_NO"] == selectedNo}
                infoListModel.scheduleSid = selectedRow?.row?.get("SCHEDULE_SID") ?: ""
                infoListModel.fltNo.value = selectedNo
                infoListModel.setList()
            }
        }


        infoListModel.fltDate.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                infoListModel.getFlight()
            }
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

            infoListModel.fltNo.value = temp
            infoListModel.cargoSid.value = temp2

        }

        if (infoListModel.cargoSid.value.isNotEmpty()) {
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

                        val params = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", selectedSid)
                        )

                        val rt = Route.ImportBDWithNo
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

                        val params = listOf<Pair<String, String>> (
                            Pair("CARGO_CONTROL_NO", selectedNo)
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