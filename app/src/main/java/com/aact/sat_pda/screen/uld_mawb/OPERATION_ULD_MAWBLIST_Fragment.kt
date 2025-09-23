package com.aact.sat_pda.screen.uld_mawb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentOperationMawbListBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_MAWBLIST_Fragment : BaseFragment() {

    private var _binding: FragmentOperationMawbListBinding? = null
    private val binding get() = _binding!!
    private val uldModel: OPERATION_ULD_MAWBLIST_Model by lazy { OPERATION_ULD_MAWBLIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOperationMawbListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.disableEditText(binding.uldNo.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltDest.editText)
        item.disableEditText(binding.fltNo.editText)
    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "OPERATION_ULD_SID" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "ULD_NO" }?.second ?: ""
            val temp3 = route.params.find { it.first == "DATE" }?.second ?: ""
            val temp4 = route.params.find { it.first == "FLIGHT_NO" }?.second ?: ""
            val temp5 = route.params.find { it.first == "DEST" }?.second ?: ""
            uldModel.operationSid = temp
            uldModel.uldNo.value = temp2
            uldModel.fltDate.value = temp3
            uldModel.fltNo.value = temp4
            uldModel.fltDest.value = temp5
        }

        setLoad()

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Delete -> {
                    item.onClick = fun() {

                        val select = uldModel.workSelect?.all { it.value.isEmpty() } ?: false

                        if (select) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        showYesNo("삭제", "선택한 MAWB 정보를 삭제처리 합니까?", onYes = fun(){
                            uldModel.setDelete{ flag->
                                if(flag){
                                    setLoad()
                                }
                            }

                        })

                    }
                }

                is BottomItem.Search -> {
                    item.onClick = {
                        setLoad()
                    }
                }

                else -> null
            }

        }
    }

    fun setLoad() {
        lifecycleScope.launch {
            Common.loadingOn(coroutineContext[Job])

            val ret = uldModel.setList()

            binding.workList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = uldModel.headerList,
                bodyList = ret,
                selectValue = uldModel.workSelect,
                height = 400,
            ) {
                uldModel.workSelect =
                    Util.validSelectTable(uldModel.workSelect, it)
            }
            binding.workList.custItem.addView(table)

            Common.loadingOff()
        }
    }

}