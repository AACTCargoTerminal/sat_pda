package com.aact.sat_pda.screen.import_bd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportBdBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_BD_Fragment : BaseFragment() {
    private var _binding: FragmentImportBdBinding? = null
    private val binding get() = _binding!!
    private val bdModel: IMPORT_BD_Model by lazy { IMPORT_BD_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportBdBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = bdModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 날짜 필드 설정
        binding.fltDate.editText.showSoftInputOnFocus = false
        item.strEditText(binding.fltDate.editText)
        item.limitEditText(binding.fltDate.editText, 8)

        // 읽기 전용 필드 설정
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.cargoDes.editText)
        item.disableEditText(binding.sumPcs.editText)
        item.disableEditText(binding.sumPcsMfst.editText)
        item.disableEditText(binding.sumWt.editText)
        item.disableEditText(binding.sumWtMfst.editText)

        // 입력 필드 설정
        item.strEditText(binding.pcs.editText)
        item.strEditText(binding.wt.editText)

        // 날짜 입력 완료시 항공편 조회
        action.doneAction(binding.fltDate.editText) {
            bdModel.getFlightList()
        }

        // PCS 입력 완료시 WT로 이동
        action.doneAction(binding.pcs.editText) {
            binding.wt.editText.requestFocus()
        }

        // WT 입력 완료시 저장
        action.doneAction(binding.wt.editText) {
            bdModel.save()
        }

        // 날짜 변경 감지 - 8자리 입력시 자동 조회
        bdModel.fltDate.observe(viewLifecycleOwner) {
            if (it.length == 8) {
                bdModel.getFlightList()
            }
        }

        // 항공편 목록 갱신시 스피너 설정
        bdModel.flightList.observe(viewLifecycleOwner) { flights ->
            if (flights.isNotEmpty()) {
                val displayList = flights.map { it.row["FLIGHT_NO"]?.toString() ?: "" }
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.flight.combo,
                    list = displayList
                ) { selected ->
                    val selectedItem = flights.find { it.row["FLIGHT_NO"]?.toString() == selected }
                    selectedItem?.let {
                        bdModel.selectedFlightSid = it.row["SCHEDULE_SID"]?.toString() ?: ""
                        // 항공편 선택시 ULD, MAWB 목록 조회
                        bdModel.getULDList()
                        bdModel.getMAWBList()
                    }
                }
            }
        }

        // ULD 목록 갱신시 스피너 설정
        bdModel.uldList.observe(viewLifecycleOwner) { ulds ->
            if (ulds.isNotEmpty()) {
                val displayList = ulds.map { it.row["ULD_NO"]?.toString() ?: "" }
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.uld.combo,
                    list = displayList
                ) { selected ->
                    val selectedItem = ulds.find { it.row["ULD_NO"]?.toString() == selected }
                    selectedItem?.let {
                        bdModel.selectedUldSid = it.row["OPERATION_ULD_SID"]?.toString() ?: ""
                    }
                }
            }
        }

        // MAWB 목록 갱신시 스피너 설정
        bdModel.mawbList.observe(viewLifecycleOwner) { mawbs ->
            if (mawbs.isNotEmpty()) {
                val displayList = mawbs.map { it.row["MASTER_AIR_WAY_BILL_NO"]?.toString() ?: "" }
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.mawb.combo,
                    list = displayList
                ) { selected ->
                    bdModel.selectedMawbNo = selected
                    // MAWB 선택시 HAWB 목록 조회
                    bdModel.getHAWBList()
                }
            }
        }

        // HAWB 목록 갱신시 스피너 설정
        bdModel.hawbList.observe(viewLifecycleOwner) { hawbs ->
            if (hawbs.isNotEmpty()) {
                val displayList = hawbs.map { it.row["HOUSE_AIR_WAY_BILL_NO"]?.toString() ?: "" }
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.hawb.combo,
                    list = displayList
                ) { selected ->
                    val selectedItem = hawbs.find { it.row["HOUSE_AIR_WAY_BILL_NO"]?.toString() == selected }
                    selectedItem?.let {
                        bdModel.cargoControlSid = it.row["CARGO_CONTROL_SID"]?.toString() ?: ""
                        // HAWB 선택시 상세정보 조회
                        bdModel.getInfo()
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value.bottomItems()
        for (item in route) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        val pcsValue = bdModel.pcs.value?.toDoubleOrNull() ?: 0.0
                        if (pcsValue == 0.0) {
                            Common.sendError("수량을 확인하십시오.")
                            binding.pcs.editText.requestFocus()
                            return
                        }
                        bdModel.save()
                    }
                }

                is BottomItem.Tmp01 -> {
                    // 파손 버튼
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (bdModel.cargoControlSid.isEmpty()) {
                            Common.sendError("HAWB를 선택하십시오.")
                            return
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_NO", bdModel.cargoNo.value ?: ""),
                            Pair("CARGO_CONTROL_SID", bdModel.cargoControlSid)
                        )
                        val route = Route.ImportCargoDamage
                        route.params = params
                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Tmp02 -> {
                    // 이례 버튼
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (bdModel.cargoControlSid.isEmpty()) {
                            Common.sendError("HAWB를 선택하십시오.")
                            return
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", bdModel.cargoControlSid),
                            Pair("CARGO_CONTROL_NO", bdModel.cargoNo.value ?: "")
                        )
                        val route = Route.ImportIrr
                        route.params = params
                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Add -> {
                    // 입고 버튼
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (bdModel.cargoControlSid.isEmpty()) {
                            Common.sendError("HAWB를 선택하십시오.")
                            return
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", bdModel.cargoControlSid),
                            Pair("CARGO_CONTROL_NO", bdModel.cargoNo.value ?: "")
                        )
                        val route = Route.ImportCargoIn
                        route.params = params
                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Search -> {
                    // 조회 버튼
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        bdModel.getFlightList()
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
