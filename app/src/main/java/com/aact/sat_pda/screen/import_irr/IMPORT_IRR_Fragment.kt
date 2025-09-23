package com.aact.sat_pda.screen.import_irr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportIrrBinding
import com.aact.sat_pda.screen.BaseFragment

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
        item.strEditText(binding.holdType.editText)
        item.strEditText(binding.remark.editText)
        binding.irrPcs.editText.showSoftInputOnFocus = false
        binding.holdDate.editText.showSoftInputOnFocus = false
        binding.holdTime.editText.showSoftInputOnFocus = false

        item.setSpiner_Str(view.context, binding.spFlag.combo, model.ynList) {
            model.spFlag.value = it
        }

        action.doneAction(binding.holdType.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            getCommonParse(
                subContent = model.modalTable,
                body = model.typeList,
                filterStr = model.holdType.value,
                filterColumn = listOf("CODE_CODE","CODE_NAME")
            )
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
            val temp3 =
                route.params.find { it.first == "HOLD_TYPE_CODE" }?.second ?: ""
            model.cargoNo.value = temp
            model.cargoSid = temp2
            model.holdTypeCode = temp3
        }

        model.getLoad()

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