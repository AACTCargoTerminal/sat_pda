package com.aact.sat_pda.screen.import_cargo_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportCargoInBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_CARGO_IN_Fragment: BaseFragment() {
    private var _binding: FragmentImportCargoInBinding? = null
    private val binding get() = _binding!!
    private val cargoInModel: IMPORT_CARGO_IN_Model by lazy {IMPORT_CARGO_IN_Model()}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportCargoInBinding.inflate(inflater,container,false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoInModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargoInModel.getLocationList()
        cargoInModel.getRackNoList()

        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.cargoNo.editText, 20)

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.unLoadCode.editText)
        item.disableEditText(binding.status.editText)
        item.disableEditText(binding.paperPcs.editText)
        item.disableEditText(binding.paperWt.editText)
        item.disableEditText(binding.cargoDes.editText)

        item.strEditText(binding.workPcs.editText)
        item.strEditText(binding.workWt.editText)

        cargoInModel.ediFlag.observe(viewLifecycleOwner) {
            val index = cargoInModel.ynList.indexOf(it)
            if (index >= 0) {
                binding.ediFlag.combo.setSelection(index)
            }
        }

        cargoInModel.spFlag.observe(viewLifecycleOwner) {
            val index = cargoInModel.ynList.indexOf(it)
            if (index >= 0) {
                binding.spFlag.combo.setSelection(index)
            }
        }

        cargoInModel.locationList.observe(viewLifecycleOwner) { locations ->
            if (locations.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.location.combo,
                    list = locations
                ) { cargoInModel.location.postValue(it) }
                val current = cargoInModel.location.value ?: ""
                val idx = locations.indexOf(current)
                binding.location.combo.setSelection(if (idx >= 0) idx else 0)
            }
        }

        cargoInModel.location.observe(viewLifecycleOwner) { loc ->
            val list = cargoInModel.locationList.value ?: return@observe
            if (list.isNotEmpty()) {
                val idx = list.indexOf(loc ?: "")
                if (idx >= 0) binding.location.combo.setSelection(idx)
            }
        }

        cargoInModel.rackNoList.observe(viewLifecycleOwner) { racks ->
            if (racks.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.RackNo.combo,
                    list = racks
                ) { cargoInModel.rackNo.postValue(it)}
            }
        }

        cargoInModel.cargoNo.observe(viewLifecycleOwner) {
            if (it.length == 19) {
                cargoInModel.setInfoByNo()
            }
        }

        item.setSpiner_Str(view.context, binding.ediFlag.combo, cargoInModel.ynList) {
            cargoInModel.ediFlag.value = it
        }

        item.setSpiner_Str(view.context, binding.spFlag.combo, cargoInModel.ynList) {
            cargoInModel.spFlag.value = it
        }


        action.doneAction(binding.cargoNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            val cargoNo = cargoInModel.cargoNo.value ?: ""
            if (cargoNo.isNotEmpty()) {
                cargoInModel.setInfoByNo()
            }
        }

        action.doneAction(binding.workPcs.editText) {
            binding.workWt.editText.requestFocus()
        }

    }

    override fun onStart() {
        super.onStart()

        val param = Common.route.value.params
        if (param.isNotEmpty()) {
            param.forEach {
                when(it.first) {
                    "CARGO_CONTROL_SID" -> cargoInModel.cargoSid = it.second
                    "CARGO_CONTROL_NO" -> cargoInModel.cargoNo.value = it.second
                }
            }
        }

        when {
            cargoInModel.cargoSid.isNotEmpty() -> {
                if (cargoInModel.mawb.value.isNullOrEmpty()) {
                    cargoInModel.setInfo()
                }
                binding.location.combo.requestFocus()
            }
            !cargoInModel.cargoNo.value.isNullOrEmpty() -> {
                if (cargoInModel.mawb.value.isNullOrEmpty()) {
                    cargoInModel.setInfoByNo()
                }
                binding.location.combo.requestFocus()
            }
            else -> {
                binding.cargoNo.editText.requestFocus()
            }
        }


        val route = Common.route.value.bottomItems()
        for (item in route) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        if (cargoInModel.cargoSid.isNullOrEmpty()) {
                            Common.sendError("입고처리 화물이 조회되지 않았습니다.")
                            return
                        }

                        if (cargoInModel.locationList.value.isNullOrEmpty() || cargoInModel.rackNoList.value.isNullOrEmpty()) {
                            Common.sendError("입고위치, RackNo를 확인하십시오")
                            return
                        }
                        cargoInModel.saveClick()
                    }
                }

                is BottomItem.Tmp01 -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (cargoInModel.cargoNo.value.isEmpty()) {
                            Common.sendError("화물번호가 입력이 안되었습니다.")
                            return
                        }


                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", cargoInModel.cargoSid),
                            Pair("CARGO_CONTROL_NO", cargoInModel.cargoNo.value ?: "")
                        )

                        val route = Route.ImportCargoDamage
                        route.params = params
                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Tmp02 -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", cargoInModel.cargoSid),
                            Pair("CARGO_CONTROL_NO", cargoInModel.cargoNo.value ?: "")
                        )

                        val route = Route.ImportIrrList
                        route.params = params
                        Common.addNavigate(route)
                    }
                }


                else -> null
            }
        }

    }


}