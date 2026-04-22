package com.aact.sat_pda.screen.import_irr_release

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportCargoIrrReleaseBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.launch

class IMPORT_CARGO_IRR_RELEASE_Fragment: BaseFragment() {
    private var _binding: FragmentImportCargoIrrReleaseBinding? = null
    private val binding get() = _binding!!
    private val irrReleaseModel: IMPORT_CARGO_IRR_RELEASE_Model by lazy { IMPORT_CARGO_IRR_RELEASE_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportCargoIrrReleaseBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = irrReleaseModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.mfcsPcs.editText)
        item.disableEditText(binding.mfcsWt.editText)
        item.disableEditText(binding.pcs.editText)
        item.disableEditText(binding.wt.editText)
        item.disableEditText(binding.holdType.editText)
        item.disableEditText(binding.holdRemark.editText)
        item.disableEditText(binding.holdDate.editText)
        item.disableEditText(binding.holdTime.editText)


        irrReleaseModel.releaseTypeIndex.observe(viewLifecycleOwner) { index ->
            if (index >= 0 && index < irrReleaseModel.releaseTypeList.size) {
                val code = irrReleaseModel.releaseTypeList[index].row["CODE_CODE"]
                irrReleaseModel.releaseTypeCode.postValue(code ?: "")
            }
        }

        irrReleaseModel.cargoNo.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) return@observe
            else if(it.length == 19) {
                irrReleaseModel.setInfo()
            }
        }

        action.doneAction(binding.cargoNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            irrReleaseModel.setInfo()
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

            irrReleaseModel.getReleaseTypeList()

            if (irrReleaseModel.releaseTypeList.isNotEmpty()) {
                val displayList = irrReleaseModel.releaseTypeList.map { row ->
                    val code = row.row["CODE_CODE"] ?: ""
                    val name = row.row["CODE_NAME"] ?: ""
                    "$code : $name"
                }

                item.setSpiner_Str(
                    context = requireContext(),
                    view = binding.releaseType.combo,
                    list = displayList
                ) { selected ->
                    val index = displayList.indexOf(selected)
                    irrReleaseModel.releaseTypeIndex.postValue(index)
                }

                // 첫 번째 항목을 기본 선택으로 설정
                irrReleaseModel.releaseTypeIndex.postValue(0)
            }

            // 데이터 자동 로드
            if (cargoControlNo.isNotEmpty()) {
                irrReleaseModel.cargoSid = cargoControlSid
                irrReleaseModel.holdTypeCode = holdTypeCode
                irrReleaseModel.cargoNo.value = cargoControlNo  // observe가 트리거되어 setInfo() 자동 호출
            }
            else if (irrReleaseModel.cargoSid.isNotEmpty()) {
                irrReleaseModel.setInfo()
            }

            Common.loadingOff()
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) keyboardCtl()
                        irrReleaseModel.saveClick()
                    }
                }
                is BottomItem.KeyBoard -> {
                    item.onClick = { keyboardCtl()}
                }
                else -> null
            }
        }
    }
}