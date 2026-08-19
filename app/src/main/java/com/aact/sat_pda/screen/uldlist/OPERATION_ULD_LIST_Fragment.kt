package com.aact.sat_pda.screen.uldlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentOperationUldListBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentOperationUldListBinding? = null
    private val binding get() = _binding!!
    private val uldListModel: OPERATION_ULD_LIST_Model by lazy { OPERATION_ULD_LIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOperationUldListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldListModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.numDefEditText(binding.fltDate.editText)
        item.strEditText(binding.fltNo.editText)
        item.strEditText(binding.fltDest.editText)

        uldListModel.fltNo.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                uldListModel.fltSelect.value = ""
                uldListModel.fltDest.value = ""
                uldListModel.fltOrigin = ""
                uldListModel.fltDestNm = ""
            }
        }

        uldListModel.uldList.observe(viewLifecycleOwner) {
            binding.uldList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = uldListModel.headerList,
                bodyList = it,
                selectValue = uldListModel.selectList,
                height = 400,
            ) {
                uldListModel.selectList =
                    Util.validSelectTable(uldListModel.selectList, it)
            }
            binding.uldList.custItem.addView(table)
        }

        action.textChange_after(binding.fltDate.editText) { s ->
            if (uldListModel.isEditing) return@textChange_after
            uldListModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)

            if (result.replace("-", "").length == 8) {
                uldListModel.setFlight { }
            } else {
                uldListModel.fltNo.value = ""
                uldListModel.fltSelect.value = ""
                uldListModel.fltDest.value = ""
                uldListModel.fltOrigin = ""
                uldListModel.fltDestNm = ""
            }

            uldListModel.isEditing = false
        }

        action.doneAction(binding.fltNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            uldListModel.fltSelect.value = ""
            uldListModel.fltDest.value = ""
            uldListModel.setFlight { data ->
                getCommonParse(
                    subContent = uldListModel.fltTable,
                    body = data,
                    filterStr = uldListModel.fltNo.value,
                    filterColumn = listOf("FLIGHT_NO")
                )
            }
            binding.fltNo.editText.clearFocus()
        }

        action.doneAction(binding.fltDest.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            binding.fltDest.editText.clearFocus()

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret =
                    uldListModel.biz.getCommonCode("APORT", Util.getStr(uldListModel.fltDest.value))

                if (!ret.isNullOrEmpty()) {
                    getCommonParse(
                        subContent = uldListModel.destTable,
                        body = ret,
                        filterStr = Util.getStr(uldListModel.fltDest.value),
                        filterColumn = listOf("CODE_CODE")
                    )
                }
                Common.loadingOff()
            }

        }
    }

    override fun onStart() {
        super.onStart()

        uldListModel.getList()

        //하단 바텀버튼 클릭지정
        val route = Common.route.value

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Search -> {
                    item.onClick = {
                        uldListModel.getList()
                    }
                }

                is BottomItem.Add -> {
                    item.onClick = fun() {
                        val params = listOf(Pair("DATE",Util.validDate(uldListModel.fltDate.value)), Pair("SCHEDULE_SID",uldListModel.fltSelect.value))

                        val route = Route.OperationUld
                        route.params = params

                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Open -> {
                    item.onClick = fun() {
                        if (uldListModel.selectList.values.all { it.isEmpty() }) {
                            Common.sendError("목록을 선택해주세요")
                            return
                        }
                        val params = listOf(
                            Pair(
                                "OPERATION_ULD_SID",
                                uldListModel.selectList["OPERATION_ULD_SID"] ?: ""
                            ),
                            Pair("ULD_NO", uldListModel.selectList["ULD_NO"] ?: "")
                        )

                        val route = Route.OperationUld
                        route.params = params

                        Common.addNavigate(route)
                    }
                }

                else -> null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}