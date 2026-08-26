package com.aact.sat_pda.screen.uld_scale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentUldScaleBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_SCALE_Fragment : BaseFragment() {

    private var _binding: FragmentUldScaleBinding? = null
    private val binding get() = _binding!!
    private val uldModel: OPERATION_ULD_SCALE_Model by lazy { OPERATION_ULD_SCALE_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUldScaleBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.strEditText(binding.uldNo.editText)
        item.strEditText(binding.contourCode.editText)
        item.strEditText(binding.contourNo.editText)
        item.limitEditText(binding.fltDate.editText,10)
        item.strEditText(binding.fltNo.editText)
        item.strEditText(binding.fltDest.editText)
        binding.weight.editText.showSoftInputOnFocus = false
        binding.dollyWeight.editText.showSoftInputOnFocus = false
        item.disableEditText(binding.scaleUser.editText)

        item.setSpiner_Str(view.context, binding.scaleNo.combo,uldModel.scaleNoList) { selected ->
            uldModel.scaleNoSelect.value = selected

            when (selected) {
                "1:10FT" -> uldModel.dollyWeight.value = "0"
                "2:20FT" -> uldModel.dollyWeight.value = "3720"
            }
        }

        uldModel.scaleNoSelect.observe(viewLifecycleOwner) {
            val index = uldModel.scaleNoList.indexOf(it)
            if (index >= 0) {
                binding.scaleNo.combo.setSelection(index)
            }
        }


        action.doneAction(binding.uldNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            uldModel.uldClick { data ->
                if(data != null){
                    getCommonParse(
                        subContent = uldModel.uldModal,
                        body = data,
                        filterStr = Util.getStr(uldModel.uldNo.value),
                        filterColumn = listOf("ULD_NO")
                    )
                }
            }
        }

        action.doneAction(binding.contourCode.editText) {

            if (keboardFlag) {
                keyboardCtl()
            }

            if (uldModel.contourList.isEmpty()) {
                Common.sendError("Contour 에러")
                return@doneAction
            }

            getCommonParse(
                subContent = uldModel.countourTable,
                body = uldModel.contourList,
                filterStr = Util.getStr(uldModel.contourCode.value),
                filterColumn = listOf("CODE_CODE")
            )

        }

        action.textChange_after(binding.fltDate.editText) { s ->
            if (uldModel.isEditing) return@textChange_after
            uldModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)

            if (result.replace("-", "").length == 8) {
                lifecycleScope.launch {
                    uldModel.setFlight(false)
                }
            } else {
                uldModel.fltNo.value = ""
                uldModel.fltSelect = ""
                uldModel.fltDest.value = ""
            }

            uldModel.isEditing = false
        }

        action.doneAction(binding.fltNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                uldModel.setFlight(true)

                if (uldModel.fltList.isNotEmpty()) {
                    getCommonParse(
                        subContent = uldModel.fltTable,
                        body = uldModel.fltList,
                        filterStr = Util.getStr(uldModel.fltNo.value),
                        filterColumn = listOf("FLIGHT_NO")
                    )
                }

                Common.loadingOff()
            }

        }

        action.doneAction(binding.fltDest.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret =
                    uldModel.biz.getCommonCode("APORT", Util.getStr(uldModel.fltDest.value))

                if (!ret.isNullOrEmpty()) {
                    getCommonParse(
                        subContent = uldModel.destTable,
                        body = ret,
                        filterStr = Util.getStr(uldModel.fltDest.value),
                        filterColumn = listOf("CODE_CODE")
                    )
                }

                Common.loadingOff()
            }
        }

        action.doneAction(binding.weight.editText) {
            uldModel.scaleRec()
        }

    }

    override fun onStart() {
        super.onStart()



        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "OPERATION_ULD_SID" }?.second ?: ""
            uldModel.operationSid = temp
        }

        uldModel.setDefault()

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        lifecycleScope.launch {
                            Common.loadingOn(coroutineContext[Job])

                            uldModel.setSave()

                            Common.loadingOff()
                        }

                    }
                }

                is BottomItem.Tmp01 ->{
                    item.onClick = fun(){
                        showYesNo("저장/출고","출고는 저장후 출고합니다. 처리합니까?", onYes = fun(){

                            lifecycleScope.launch {
                                Common.loadingOn(coroutineContext[Job])

                                val retSave = uldModel.setSave()

                                if(retSave == false){
                                    Common.loadingOff()
                                    return@launch
                                }

                                uldModel.setExport()

                                Common.loadingOff()
                            }

                        })
                    }
                }
                is BottomItem.Tmp02 -> {
                    item.onClick = fun(){
                        showYesNo("인쇄/출고","인쇄는 출고처리와 동시에 처리됩니다. 인쇄 합니까?", onYes = fun(){
                            lifecycleScope.launch {
                                Common.loadingOn(coroutineContext[Job])

                                uldModel.printClick()

                                uldModel.setExport()

                                Common.loadingOff()
                            }
                        })
                    }
                }

                else -> null
            }
        }
    }

}