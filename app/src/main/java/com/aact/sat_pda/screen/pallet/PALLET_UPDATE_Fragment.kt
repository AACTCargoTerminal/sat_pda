package com.aact.sat_pda.screen.pallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentPalletUpdateBinding
import com.aact.sat_pda.screen.BaseFragment

class PALLET_UPDATE_Fragment : BaseFragment() {

    private var _binding: FragmentPalletUpdateBinding? = null
    private val binding get() = _binding!!
    private val palletUpdateModel: PALLET_UPDATE_Model by lazy { PALLET_UPDATE_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPalletUpdateBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = palletUpdateModel

        val mawb = binding.mawb.editText
        val oldPalletPcs = binding.oldPalletPcs.editText
        val newPalletPcs = binding.newPalletPcs.editText

        item.disableEditText(mawb)
        item.disableEditText(oldPalletPcs)

        newPalletPcs.showSoftInputOnFocus = false

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val newPalletPcs =
            binding.newPalletPcs.editText

        action.doneAction(newPalletPcs) {
            if (keboardFlag) {
                keyboardCtl()
            }

            palletUpdateModel.setUpdate { bool ->
                if (bool) {
                    Common.suc.value =
                        "파렛트 수량 수정완료"

                    newPalletPcs.requestFocus()
                    newPalletPcs.setSelection(
                        newPalletPcs.text?.length ?: 0
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val route =
            Common.route.value

        if (route.params.size > 0) {
            val cargoControlSid =
                route.params.find {
                    it.first == "CARGO_CONTROL_SID"
                }?.second ?: ""

            val cargoAcceptSid =
                route.params.find {
                    it.first == "CARGO_ACCEPT_SID"
                }?.second ?: ""

            val mawb =
                route.params.find {
                    it.first == "MAWB"
                }?.second ?: ""

            val totalPalletCnt  =
                route.params.find {
                    it.first == "TOTAL_PALLET_CNT"
                }?.second ?: ""

            palletUpdateModel.cargoControlSid =
                cargoControlSid

            palletUpdateModel.acceptSid =
                cargoAcceptSid

            palletUpdateModel.mawb.value =
                mawb

            palletUpdateModel.oldPalletPcs.value =
                totalPalletCnt
        }

        val newPalletPcs =
            binding.newPalletPcs.editText

        newPalletPcs.post {
            newPalletPcs.requestFocus()
            newPalletPcs.setSelection(
                newPalletPcs.text?.length ?: 0
            )
        }

        for (item in route.bottomItems()) {
            when (item) {

                is BottomItem.KeyBoard -> {
                    item.onClick = {
                        keyboardCtl()
                    }
                }

                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        palletUpdateModel.setUpdate { bool ->
                            if (bool) {
                                Common.suc.value =
                                    "파렛트 수량 수정 되었습니다."
                            }
                        }
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