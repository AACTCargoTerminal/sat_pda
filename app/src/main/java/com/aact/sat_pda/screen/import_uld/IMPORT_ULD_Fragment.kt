package com.aact.sat_pda.screen.import_uld

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportUldBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_ULD_Fragment : BaseFragment() {
    private var _binding: FragmentImportUldBinding? = null
    private val binding get() = _binding!!
    private val uldModel: IMPORT_ULD_Model by lazy { IMPORT_ULD_Model() }
    private val uldListAdapter: ULDListAdapter by lazy { ULDListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportUldBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // EditText 설정
        item.strEditText(binding.fltDate.editText)
        item.limitEditText(binding.fltDate.editText, 8)

        item.strEditText(binding.serialNo.editText)
        item.limitEditText(binding.serialNo.editText, 5)

        item.strEditText(binding.origin.editText)
        item.limitEditText(binding.origin.editText, 11)

        // RecyclerView 설정
        binding.uldListView.adapter = uldListAdapter
        uldListAdapter.setOnItemClickListener { item ->
        }

        // 일자 변경 시 편번 리스트 로드
        uldModel.fltDate.observe(viewLifecycleOwner) { date ->
            if (date.length == 8) {
                uldModel.getFlightList()
            }
        }

        // ULD 타입 콤보박스
        uldModel.uldTypeNameList.observe(viewLifecycleOwner) { types ->
            if (types.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.uldType.combo,
                    list = types
                ) { selectedType ->
                    val index = types.indexOf(selectedType)
                    uldModel.uldTypeIndex.value = index
                    if (index >= 0) {
                        uldModel.uldTypeCode.value = uldModel.uldTypeList[index].row["CODE_CODE"] ?: ""
                    }
                }
            }
        }

        // 항공사 콤보박스
        uldModel.carrierNameList.observe(viewLifecycleOwner) { carriers ->
            if (carriers.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.carrier.combo,
                    list = carriers
                ) { selectedCarrier ->
                    val index = carriers.indexOf(selectedCarrier)
                    uldModel.carrierIndex.value = index
                    if (index >= 0) {
                        uldModel.carrierCode.value = uldModel.carrierList[index].row["CODE_CODE"] ?: ""
                    }
                }
            }
        }

        // 편번 콤보박스
        uldModel.flightNameList.observe(viewLifecycleOwner) { flights ->
            if (flights.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.fltNo.combo,
                    list = flights
                ) { selectedFlight ->
                    val index = flights.indexOf(selectedFlight)
                    uldModel.flightIndex.value = index
                    if (index >= 0) {
                        uldModel.scheduleSid = uldModel.flightList[index].row["SCHEDULE_SID"] ?: ""
                        uldModel.origin.value = uldModel.flightList[index].row["ORIGIN_CODE"] ?: ""
                    }
                }
            }
        }

        // ULD 목록 Observer
        uldModel.uldList.observe(viewLifecycleOwner) { list ->
            uldListAdapter.setData(list)
        }

        // doneAction - fltDate
        action.doneAction(binding.fltDate.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            if (uldModel.fltDate.value?.length == 8) {
                uldModel.getFlightList()
            }
        }

        uldModel.initDateTime()
        uldModel.getULDTypeList()
        uldModel.getCarrierList()
        uldModel.getULDList()
    }

    override fun onStart() {
        super.onStart()

        binding.fltDate.editText.requestFocus()

        val route = Common.route.value ?: return

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        uldModel.saveClick()
                    }
                }
                is BottomItem.Cancel -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        uldModel.setClear()
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
