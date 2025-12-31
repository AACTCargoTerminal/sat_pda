package com.aact.sat_pda.screen.import_irr_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportIrrListBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_IRR_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentImportIrrListBinding? = null
    private val binding get() = _binding!!
    private val model: IMPORT_IRR_LIST_Model by lazy { IMPORT_IRR_LIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImportIrrListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.strEditText(binding.cargoNo.editText)
        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.status.editText)
        item.disableEditText(binding.mfcsPcs.editText)
        item.disableEditText(binding.mfcsWt.editText)

        model.irrList.observe(viewLifecycleOwner) {

            if (it.isNotEmpty()) {
                model.irrSelect["HOLD_TYPE_CODE"] = Util.getStr(it[0].row["HOLD_TYPE_CODE"])
            } else {
                model.irrSelect["HOLD_TYPE_CODE"] = ""
            }

            binding.irrList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = model.irrHeader,
                bodyList = it,
                selectValue = model.irrSelect,
                height = 240,
            ) { params ->
                model.irrSelect =
                    Util.validSelectTable(model.irrSelect, params).toMutableMap()
            }
            binding.irrList.custItem.addView(table)
        }

        action.doneAction(binding.cargoNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            model.setInfo()
        }


    }

    override fun onStart() {
        super.onStart()
        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "CARGO_CONTROL_NO" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            model.cargoNo.value = temp
            model.cargoSid = temp2
        }

        if (model.cargoNo.value.isNotEmpty()) {
            model.setInfo()
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Camera -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        if (model.cargoNo.value.isEmpty()) {
                            Common.sendError("화물번호가 없습니다.")
                            return
                        }

                        val originParams = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_NO", model.cargoNo.value),
                            Pair("CARGO_CONTROL_SID", model.cargoSid)
                        )

                        route.params = originParams

                        val params1 = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", model.cargoSid),
                            Pair("MAWB", model.mawb.value)
                        )
                        val camera = Route.Camera
                        camera.params = params1
                        Common.addNavigate(camera)
                    }
                }
                is BottomItem.Add -> {
                    item.onClick = fun(){

                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        val originParams = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_NO", model.cargoNo.value),
                            Pair("CARGO_CONTROL_SID", model.cargoSid)
                        )

                        route.params = originParams

                        val params = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_NO", model.cargoNo.value),
                            Pair("CARGO_CONTROL_SID", model.cargoSid),
                            Pair("HOLD_TYPE_CODE", "")
                        )
                        val rt = Route.ImportIrr
                        rt.params = params
                        Common.addNavigate(rt)
                    }
                }

                else -> null
            }
        }
    }
}