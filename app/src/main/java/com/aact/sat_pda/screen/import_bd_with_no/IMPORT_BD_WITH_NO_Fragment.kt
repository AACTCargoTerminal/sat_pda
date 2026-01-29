package com.aact.sat_pda.screen.import_bd_with_no

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportBdWithNoBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_BD_WITH_NO_Fragment : BaseFragment() {
    private var _binding: FragmentImportBdWithNoBinding? = null
    private val binding get() = _binding!!
    private val infobdModel: IMPORT_BD_WITH_NO_Model by lazy { IMPORT_BD_WITH_NO_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportBdWithNoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = infobdModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fltDate.editText.showSoftInputOnFocus = false
        infobdModel.getLocationList()

        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.cargoNo.editText, 20)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.status.editText)
        item.disableEditText(binding.cargoDes.editText)
        item.strEditText(binding.pcs.editText)
        item.strEditText(binding.wt.editText)
        item.disableEditText(binding.wt.editText)


        action.doneAction(binding.pcs.editText) {
            binding.wt.editText.requestFocus()
        }

        action.doneAction(binding.wt.editText) {
            infobdModel.setBreakDown()
        }

        action.doneAction(binding.cargoNo.editText) {
            infobdModel.setInfo()
        }

        infobdModel.cargoNo.observe(viewLifecycleOwner) {
            if (it.length == 22) {
                infobdModel.setInfo()
            }
            else {
                infobdModel.setInfo()
            }
        }

        infobdModel.pcs.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                infobdModel.calculateWeight()
            }
        }

        infobdModel.locationList.observe(viewLifecycleOwner) { locations ->
            if (locations.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.location.combo,
                    list = locations
                ) {infobdModel.location.postValue(it)}
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val param = Common.route.value.params
        if (param.size > 0) {

            param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_NO" -> infobdModel.cargoNo.value = it.second
                }
            }
        }

        if (infobdModel.cargoNo.value.isNullOrEmpty()) {
            infobdModel.setInfo()
        } else {
            binding.cargoNo.editText.requestFocus()
        }


        val route = Common.route.value.bottomItems()
        for (item in route) {
            when (item) {

                is BottomItem.Save -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        val pcsValue = infobdModel.pcs.value?.toDoubleOrNull() ?: 0.0
                        if (pcsValue == 0.0) {
                            Common.sendError("수량을 확인하십시오.")
                            binding.pcs.editText.requestFocus()
                            return
                        }

                        val sumPcs = infobdModel.sumPcs.value?.toDoubleOrNull() ?: 0.0
                        val sumMfcsPcs = infobdModel.sumMfcsPcs.value?.toDoubleOrNull() ?: 0.0

                        // 이미 완료된 경우 - 보류 화면으로 이동 (저장 안함)
                        if (sumMfcsPcs == sumPcs) {
                            showCompletionDialog()
                            return
                        }
                        // 초과되는 경우 - 보류 화면으로 이동 (저장 안함)
                        if (sumMfcsPcs < sumPcs + pcsValue) {
                            showCompletionDialog()
                            return
                        }

                        // 정상인 경우만 저장
                        infobdModel.setBreakDown()
                    }
                }
                is BottomItem.Tmp01 -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", infobdModel.cargoSid),
                            Pair("CARGO_CONTROL_NO", infobdModel.cargoNo.value ?: "")
                        )
                        val route = Route.ImportCargoDamage
                        route.params = params
                        Common.addNavigate(route)
                        return
                    }
                }

                is BottomItem.Tmp02 -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", infobdModel.cargoSid),
                            Pair("CARGO_CONTROL_NO", infobdModel.cargoNo.value ?: "")
                        )
                        val route = Route.ImportIrr
                        route.params = params
                        Common.addNavigate(route)
                        return
                    }
                }

                is BottomItem.Add -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (infobdModel.hawb.value.isEmpty()) {
                            Common.sendError("HAWB이 입력이 안되었습니다.")
                        }

                        //수량 체크
                        val sumPcs = infobdModel.sumPcs.value?.toDoubleOrNull() ?: 0.0
                        val sumMfcsPcs = infobdModel.sumMfcsPcs.value?.toDoubleOrNull() ?: 0.0
                        if (sumPcs != sumMfcsPcs) {
                            Common.sendError("적하목록 수량과 하기수량이 불일치 합니다.")
                        }

                        //입고화면으로 Route
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID",infobdModel.cargoSid),
                            Pair("CARGO_CONTROL_NO",infobdModel.cargoNo.value ?: "")
                        )
                        val route = Route.ImportCargoIn
                        route.params = params
                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Complete -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        infobdModel.setInfo()
                    }
                }
                is BottomItem.Camera -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        infobdModel.setInfo()
                    }
                }
                else -> null
            }
        }

    }

    private fun showCompletionDialog() {
        val params = listOf(
            Pair("CARGO_CONTROL_NO", infobdModel.cargoNo.value ?: ""),
            Pair("CARGO_CONTROL_SID", infobdModel.cargoSid)
        )

        val route = Route.ImportIrrList
        route.params = params
        Common.addNavigate(route)
    }



}