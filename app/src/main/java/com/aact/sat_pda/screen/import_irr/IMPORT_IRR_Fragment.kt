package com.aact.sat_pda.screen.import_irr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportIrrBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.launch

class IMPORT_IRR_Fragment : BaseFragment() {

    private var _binding: FragmentImportIrrBinding? = null
    private val binding get() = _binding!!
    private val model: IMPORT_IRR_Model by lazy { IMPORT_IRR_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImportIrrBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item.disableEditText(binding.cargoNo.editText)
        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.mfcsPcs.editText)
        item.disableEditText(binding.mfcsWt.editText)
        binding.pcs.editText.showSoftInputOnFocus = false
        binding.wt.editText.showSoftInputOnFocus = false
        item.strEditText(binding.remark.editText)
        binding.irrPcs.editText.showSoftInputOnFocus = false
        binding.holdDate.editText.showSoftInputOnFocus = false
        binding.holdTime.editText.showSoftInputOnFocus = false

        item.setSpiner_Str(view.context, binding.spFlag.combo, model.ynList) {
            model.spFlag.value = it
        }

    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        var cargoControlNo = ""
        var cargoControlSid = ""
        var holdTypeCode = ""

        if (route.params.size > 0) {
            cargoControlNo = route.params.find { it.first == "CARGO_CONTROL_NO" }?.second ?: ""
            cargoControlSid = route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            holdTypeCode = route.params.find { it.first == "HOLD_TYPE_CODE" }?.second ?: ""
        }

        viewLifecycleOwner.lifecycleScope.launch {
            Common.loadingOn(null)

            model.loadTypeList()

            if (model.typeList.isNotEmpty()) {
                val displayList = model.typeList.map { row ->
                    val code = row.row["CODE_CODE"] ?: ""
                    val name = row.row["CODE_NAME"] ?: ""
                    "$code : $name"
                }

                item.setSpiner_Str(
                    context = requireContext(),
                    view = binding.holdType.combo,
                    list = displayList
                ) { selected ->
                    val index = displayList.indexOf(selected)
                    if (index >= 0 && index < model.typeList.size) {
                        val code = model.typeList[index].row["CODE_CODE"] ?: ""
                        model.holdTypeCode = code
                    }
                }

                // 기존 holdTypeCode가 있으면 해당 항목 선택
                if (holdTypeCode.isNotEmpty()) {
                    val index = model.typeList.indexOfFirst { it.row["CODE_CODE"] == holdTypeCode }
                    if (index >= 0) {
                        binding.holdType.combo.setSelection(index)
                        model.holdTypeCode = holdTypeCode
                    }
                }
            }

            // 데이터 로드
            if (cargoControlNo.isNotEmpty()) {
                model.cargoSid = cargoControlSid
                model.holdTypeCode = holdTypeCode
                model.cargoNo.value = cargoControlNo
                model.setInfo()
            }

            Common.loadingOff()
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        model.saveClick()
                    }
                }
                else -> null
            }
        }

    }

}