package com.aact.sat_pda.screen.uld_material

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentOperationMaterialBinding
import com.aact.sat_pda.screen.BaseFragment

class OPERATION_ULD_MATERIAL_Fragment : BaseFragment() {

    private var _binding: FragmentOperationMaterialBinding? = null
    private val binding get() = _binding!!
    private val uldModel: OPERATION_ULD_MATERIAL_Model by lazy { OPERATION_ULD_MATERIAL_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOperationMaterialBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.disableEditText(binding.uldNo.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        item.disableEditText(binding.fltDest.editText)

        uldModel.materialList.observe(viewLifecycleOwner) {
            binding.materialList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = uldModel.headerList,
                bodyList = it,
                selectValue = uldModel.selectList,
                selectList = uldModel.selectMap,
                height = 400,
            ) {
                uldModel.selectList =
                    Util.validSelectTable(uldModel.selectList, it)
            }
            binding.materialList.custItem.addView(table)
        }
    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "OPERATION_ULD_SID" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "ULD_NO" }?.second ?: ""
            val temp3 = route.params.find { it.first == "DATE" }?.second ?: Util.getNowDate()
            val temp4 = route.params.find { it.first == "FLIGHT_NO" }?.second ?: ""
            val temp5 = route.params.find { it.first == "DEST" }?.second ?: ""
            uldModel.operationSid = temp
            uldModel.uldNo.value = temp2
            uldModel.fltDate.value = temp3
            uldModel.fltNo.value = temp4
            uldModel.fltDest.value = temp5
        }

        uldModel.getList()

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        uldModel.setSave()

                    }
                }
                else -> null
            }
        }
    }
}