package com.aact.sat_pda.screen.import_cargo_ovcd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportCargoOvcdBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_CARGO_OVCD_Fragment : BaseFragment() {
    private var _binding: FragmentImportCargoOvcdBinding? = null
    private val binding get() = _binding!!
    private val cargoOVCDModel: IMPORT_CARGO_OVCD_Model by lazy { IMPORT_CARGO_OVCD_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportCargoOvcdBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoOVCDModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // EditText 설정
        item.strEditText(binding.fltDate.editText)
        item.limitEditText(binding.fltDate.editText, 8)

        item.strEditText(binding.mawb.editText)
        item.limitEditText(binding.mawb.editText, 11)

        item.strEditText(binding.hawb.editText)

        item.strEditText(binding.mrn.editText)
        item.limitEditText(binding.mrn.editText, 11)

        item.disableEditText(binding.msn.editText)  // Fixed to "OVCD"
        item.disableEditText(binding.hsn.editText)  // Empty

        item.strEditText(binding.origin.editText)
        item.limitEditText(binding.origin.editText, 3)

        item.strEditText(binding.dest.editText)
        item.limitEditText(binding.dest.editText, 3)

        item.strEditText(binding.pcs.editText)
        item.strEditText(binding.wt.editText)

        item.strEditText(binding.holdDate.editText)
        item.limitEditText(binding.holdDate.editText, 8)

        item.strEditText(binding.holdTime.editText)
        item.limitEditText(binding.holdTime.editText, 6)

        item.strEditText(binding.remarks.editText)
        item.limitEditText(binding.remarks.editText, 200)


        // 일자 변경 시 편번 리스트 로드
        cargoOVCDModel.fltDate.observe(viewLifecycleOwner) { date ->
            if (date.length == 8) {
                cargoOVCDModel.getFlightList()
            }
        }

        // 편번 콤보박스
        cargoOVCDModel.flightNameList.observe(viewLifecycleOwner) { flights ->
            if (flights.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.fltNo.combo,
                    list = flights
                ) { selectedFlight ->
                    val index = flights.indexOf(selectedFlight)
                    cargoOVCDModel.flightIndex.value = index
                    if (index >= 0) {
                        cargoOVCDModel.scheduleSid = cargoOVCDModel.flightList[index].row["SCHEDULE_SID"] ?: ""
                        cargoOVCDModel.setFlightInfo()
                    }
                }
            }
        }

        // 보류 유형 콤보박스
        cargoOVCDModel.holdTypeNameList.observe(viewLifecycleOwner) { types ->
            if (types.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.holdType.combo,
                    list = types
                ) { selectedType ->
                    val index = types.indexOf(selectedType)
                    cargoOVCDModel.holdTypeIndex.value = index
                    if (index >= 0) {
                        cargoOVCDModel.holdTypeCode.value = cargoOVCDModel.holdTypeList[index].row["CODE_CODE"] ?: ""
                    }
                }

                // Set default index (index 2)
                if (cargoOVCDModel.holdTypeIndex.value != null && cargoOVCDModel.holdTypeIndex.value!! >= 0) {
                    binding.holdType.combo.setSelection(cargoOVCDModel.holdTypeIndex.value!!)
                }
            }
        }

        action.doneAction(binding.fltDate.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            if (cargoOVCDModel.fltDate.value?.length == 8) {
                cargoOVCDModel.getFlightList()
            }
        }

        cargoOVCDModel.initDateTime()
        cargoOVCDModel.getHoldTypeList()
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
                        cargoOVCDModel.saveClick()
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
