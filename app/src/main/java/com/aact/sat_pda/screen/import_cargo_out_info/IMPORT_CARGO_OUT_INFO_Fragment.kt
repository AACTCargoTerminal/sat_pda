package com.aact.sat_pda.screen.import_cargo_out_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportCargoOutInfoBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_CARGO_OUT_INFO_Fragment : BaseFragment() {
    private var _binding: FragmentImportCargoOutInfoBinding? = null
    private val binding get() = _binding!!
    private val model: IMPORT_CARGO_OUT_INFO_Model by lazy { IMPORT_CARGO_OUT_INFO_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportCargoOutInfoBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // EditText 설정 - cargoNo만 입력 가능, 나머지는 읽기 전용
        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.cargoNo.editText, 20)

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.doPcs.editText)
        item.disableEditText(binding.doWt.editText)
        item.disableEditText(binding.releasePcs.editText)
        item.disableEditText(binding.releaseWt.editText)

        // cargoNo observe - 자동 조회는 20자리일 때만
        model.cargoNo.observe(viewLifecycleOwner) {
            if (it.length == 20) {
                model.setInfoByNo()
            }
        }

        // doneAction - 엔터 누르면 조회 (빈 값 제외)
        action.doneAction(binding.cargoNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            val cargoNo = model.cargoNo.value ?: ""
            if (cargoNo.isNotEmpty()) {
                model.setInfoByNo()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value ?: run {
            binding.cargoNo.editText.requestFocus()
            return
        }

        val param = route.params
        if (param.size > 0) {
            param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_SID" -> model.cargoSid = it.second
                    "CARGO_CONTROL_NO" -> model.cargoNo.value = it.second
                }
            }
        }

        if (model.cargoSid.isNotEmpty() || model.cargoNo.value.isNotEmpty()) {
            model.setInfoByNo()
        } else {
            binding.cargoNo.editText.requestFocus()
        }

        // Bottom buttons (없을 수도 있지만 기본 패턴 유지)
        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Tmp01 -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        // 필요시 추가 기능 구현
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
