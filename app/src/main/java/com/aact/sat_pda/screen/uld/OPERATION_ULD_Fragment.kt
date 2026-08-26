package com.aact.sat_pda.screen.uld

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentOperationUldBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_Fragment : BaseFragment() {
    private var _binding: FragmentOperationUldBinding? = null
    private val binding get() = _binding!!
    private val uldModel: OPERATION_ULD_Model by lazy { OPERATION_ULD_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOperationUldBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.strEditText(binding.uldType.editText)
        item.strEditText(binding.uldSerialNo.editText)
        item.strEditText(binding.uldCarrier.editText)
        item.strEditText(binding.contourCode)
        item.strEditText(binding.contourNo)
        item.strEditText(binding.position.editText)
        item.disableEditText(binding.status.editText)
        item.limitEditText(binding.fltDate.editText, 10)
        item.strEditText(binding.fltNo.editText)
        item.strEditText(binding.fltDest.editText)
        item.strEditText(binding.remark.editText)
        item.disableEditText(binding.bdWeight.editText)
        item.disableEditText(binding.bdPcs.editText)
        item.strEditText(binding.transAt.editText)
        item.strEditText(binding.transFlt.editText)
        binding.uldWeight.editText.showSoftInputOnFocus = false
        item.strEditText(binding.agent.editText)

        uldModel.raFlag.observe(viewLifecycleOwner) {
            if (it) {
                uldModel.agent.value = "RA"
                uldModel.agentName = ""
            } else {
                uldModel.agent.value = ""
                uldModel.agentName = ""
            }
        }

        uldModel.contourCode.observe(viewLifecycleOwner) { contour ->
            if (contour == "BULK" && uldModel.bulkFlag.value != true) {
                uldModel.bulkFlag.value = true
            }
        }

        uldModel.bulkFlag.observe(viewLifecycleOwner) { bulk ->
            if (bulk == true) {
                if (uldModel.contourCode.value != "BULK") {
                    uldModel.contourCode.value = "BULK"
                }
            } else {
                if (uldModel.contourCode.value == "BULK") {
                    uldModel.contourCode.value = ""
                }
            }
        }

        action.textChange_after(binding.uldType.editText) {
            if (uldModel.isUldSetting) {
                return@textChange_after
            }

            uldModel.clearUldInfo()
        }

        action.textChange_after(binding.uldSerialNo.editText) {
            if (uldModel.isUldSetting) {
                return@textChange_after
            }

            uldModel.clearUldInfo()
        }

        action.textChange_after(binding.uldCarrier.editText) {
            if (uldModel.isUldSetting) {
                return@textChange_after
            }

            uldModel.clearUldInfo()
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

            if (uldModel.fltSelect.isEmpty()) {
                Common.sendError("편번을 선택해주세요")
                return@doneAction
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

        action.doneAction(binding.contourCode) {

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

        action.doneAction(binding.position.editText) {

            if (keboardFlag) {
                keyboardCtl()
            }
            if (uldModel.positionList.isEmpty()) {
                Common.sendError("위치 에러")
                return@doneAction
            }

            getCommonParse(
                subContent = uldModel.positionTable,
                body = uldModel.positionList,
                filterStr = Util.getStr(uldModel.position.value),
                filterColumn = listOf("CODE_CODE")
            )
        }

        action.doneAction(binding.uldCarrier.editText) {

            if (keboardFlag) {
                keyboardCtl()
            }
            if (uldModel.carrierList.isEmpty()) {
                Common.sendError("ULD CARRIER 에러")
                return@doneAction
            }

            getCommonParse(
                subContent = uldModel.carrierTable,
                body = uldModel.carrierList,
                filterStr = Util.getStr(uldModel.uldCarrier.value),
                filterColumn = listOf("CODE_CODE")
            )
        }

        action.doneAction(binding.uldType.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            if (uldModel.typeList.isEmpty()) {
                Common.sendError("ULD TYPE 에러")
                return@doneAction
            }

            getCommonParse(
                subContent = uldModel.typeTable,
                body = uldModel.typeList,
                filterStr = Util.getStr(uldModel.uldType.value),
                filterColumn = listOf("CODE_CODE")
            )
        }

        action.doneAction(binding.transAt.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            uldModel.transAt.value = "DOH"
        }

        action.doneAction(binding.remark.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            uldModel.remark.value = "ELI"
        }

        action.textChange_after(binding.fltDate.editText) { s ->
            if (uldModel.isEditing) return@textChange_after
            uldModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)

            if (result.replace("-", "").length != 8) {
                uldModel.fltNo.value = ""
                uldModel.fltSelect = ""
                uldModel.fltDest.value = ""
            }

            uldModel.isEditing = false
        }

    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "OPERATION_ULD_SID" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "ULD_NO" }?.second ?: ""
            val temp3 = route.params.find { it.first == "DATE" }?.second ?: Util.getNowDate()
            val temp4 = route.params.find { it.first == "FLIGHT_NO" }?.second ?: ""
            val temp5 = route.params.find { it.first == "STATUS_FLAG" }?.second ?: ""
            uldModel.operationSid = temp
            uldModel.uldNo = temp2
            uldModel.fltDate.value = temp3
            uldModel.fltNo.value = temp4

            if (temp5 == "C") {
                val idx = route.params.indexOfFirst { it.first == "STATUS_FLAG" }
                if(idx >= 0){
                    val params = route.params.toMutableList().apply { removeAt(idx) }
                    Common.route.value.params = params
                }

                uldModel.setComplete()
            }
        }

        uldModel.setDefault {
            if (it) {
                binding.uldSerialNo.editText.requestFocus()
            }
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Add -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        uldModel.clearAll()
                        binding.uldSerialNo.editText.requestFocus()
                    }
                }

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

                is BottomItem.Delete -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (uldModel.operationSid.isEmpty()) {
                            Common.sendError("저장되지 않은 작업 ULD 입니다.")
                            return
                        }

                        showYesNo("ULD", "작업 삭제처리 합니까?", onYes = { uldModel.setDelete() })

                        binding.uldSerialNo.editText.requestFocus()
                    }
                }

                is BottomItem.Complete -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        uldModel.setCompleteValid { flag ->
                            if (flag != null) {
                                if (flag == 0) {
                                    showYesNoCancel(
                                        "소모품",
                                        "소모품 내역이 없습니다. 작업완료처리합니까?",
                                        onYes = { uldModel.setComplete() },
                                        onNo = {
                                            val uldNo =
                                                uldModel.uldType.value + uldModel.uldSerialNo.value + uldModel.uldCarrier.value
                                            val originParams = listOf(
                                                Pair("OPERATION_ULD_SID", uldModel.operationSid),
                                                Pair("ULD_NO", uldNo),
                                                Pair("DATE", uldModel.fltDate.value),
                                                Pair("FLIGHT_NO", uldModel.fltNo.value),
                                                Pair("STATUS_FLAG", "C")
                                            )

                                            Common.route.value.params = originParams

                                            val params = listOf(
                                                Pair("OPERATION_ULD_SID", uldModel.operationSid),
                                                Pair("ULD_NO", uldNo),
                                                Pair("DATE", uldModel.fltDate.value),
                                                Pair("FLIGHT_NO", uldModel.fltNo.value),
                                                Pair("DEST", uldModel.fltDest.value)
                                            )

                                            val route = Route.UldMaterial
                                            route.params = params
                                            Common.addNavigate(route)

                                        })
                                } else {
                                    showYesNo(
                                        "작업",
                                        "작업 완료처리 합니까?",
                                        onYes = { uldModel.setComplete() })
                                }


                            }
                        }
                    }
                }

                is BottomItem.Export -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (uldModel.operationSid.isEmpty()) {
                            Common.sendError("저장되지 않은 작업ULD입니다.")
                            return
                        }
                        val uldNo =
                            uldModel.uldType.value + uldModel.uldSerialNo.value + uldModel.uldCarrier.value
                        val originParams = listOf(
                            Pair("OPERATION_ULD_SID", uldModel.operationSid),
                            Pair("ULD_NO", uldNo),
                            Pair("DATE", uldModel.fltDate.value),
                            Pair("FLIGHT_NO", uldModel.fltNo.value),
                        )
                        Common.route.value.params = originParams

                        val param = listOf(Pair("OPERATION_ULD_SID", uldModel.operationSid))
                        val route = Route.UldScale
                        route.params = param

                        Common.addNavigate(route)
                    }
                }

                is BottomItem.Exit -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (uldModel.operationSid.isEmpty()) {
                            Common.sendError("저장되지 않은 작업 ULD입니다.")
                            return
                        }

                        showYesNo("취소", "취소 합니까?", onYes = {
                            uldModel.setCancel()
                        })


                    }
                }

                is BottomItem.Offload -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (uldModel.operationSid.isEmpty()) {
                            Common.sendError("저장되지 않은 작업 ULD입니다.")
                            return
                        }

                        showYesNo("Offload", "Offload 등록 합니까?", onYes = {
                            uldModel.setOffload()
                        })


                    }
                }

                is BottomItem.Print -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        showYesNo("인쇄", "Slip 인쇄 합니까?", onYes = { uldModel.printClick() })
                    }
                }

                is BottomItem.Tmp01 ->{
                    item.onClick ={
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        lifecycleScope.launch {
                            Common.loadingOn(coroutineContext[Job])
                            val flag = uldModel.setULDValid()
                            if(!flag){
                                val inputFlag = uldModel.setULDInput()
                                if(!inputFlag){
                                    Common.loadingOff()
                                    return@launch
                                }

                                val validAgain = uldModel.setULDValid()

                                if (!validAgain) {
                                    Common.sendError("입력한 ULD가 재고에 없거나, 적합하지 않습니다.")
                                    Common.loadingOff()
                                    return@launch
                                }
                            }else{
                                val uldNo =
                                    uldModel.uldType.value + uldModel.uldSerialNo.value + uldModel.uldCarrier.value
                                val originParams = listOf(
                                    Pair("OPERATION_ULD_SID", uldModel.operationSid),
                                    Pair("ULD_NO", uldNo),
                                    Pair("DATE", uldModel.fltDate.value),
                                    Pair("FLIGHT_NO", uldModel.fltNo.value)
                                )

                                Common.route.value.params = originParams

                                val param = listOf<Pair<String,String>>(
                                    Pair("OPERATION_ULD_SID",uldModel.operationSid),
                                    Pair("ULD_NO",uldNo),
                                    Pair("DATE",uldModel.fltDate.value),
                                    Pair("FLIGHT_NO",uldModel.fltNo.value),
                                    Pair("SCHEDULE_SID",uldModel.fltSelect),
                                    Pair("DEST",uldModel.fltDest.value),
                                    Pair("TO_LOCATION",uldModel.position.value),
                                    Pair("CONTOUR_CODE",uldModel.contourCode.value),
                                    Pair("CONTOUR_NO",uldModel.contourNo.value),
                                    Pair("BULK_FLAG",if(uldModel.bulkFlag.value) "Y" else "N"),
                                    Pair("ORIGIN",uldModel.fltOrigin),
                                )

                                val route = Route.UldBuildUp
                                route.params = param
                                Common.addNavigate(route)

                            }
                            Common.loadingOff()
                        }

                    }
                }

                is BottomItem.Tmp02 ->{
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        if (uldModel.operationSid.isEmpty()) {
                            Common.sendError("저장되지 않은 작업ULD입니다.")
                            return
                        }

                        val uldNo =
                            uldModel.uldType.value + uldModel.uldSerialNo.value + uldModel.uldCarrier.value
                        val originParams = listOf(
                            Pair("OPERATION_ULD_SID", uldModel.operationSid),
                            Pair("ULD_NO", uldNo),
                            Pair("DATE", uldModel.fltDate.value),
                            Pair("FLIGHT_NO", uldModel.fltNo.value)
                        )

                        Common.route.value.params = originParams

                        val param = listOf<Pair<String,String>>(
                            Pair("OPERATION_ULD_SID",uldModel.operationSid),
                            Pair("ULD_NO",uldNo),
                            Pair("DATE",uldModel.fltDate.value),
                            Pair("FLIGHT_NO",uldModel.fltNo.value),
                            Pair("DEST",uldModel.fltDest.value),
                        )

                        val route = Route.UldMawbList
                        route.params = param
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