package com.aact.sat_pda.screen.uld_buildup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentUldBuildupBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OPERATION_ULD_BUILDUP_Fragment : BaseFragment() {
    private var _binding: FragmentUldBuildupBinding? = null
    private val binding get() = _binding!!
    private val uldModel: OPERATION_ULD_BUILDUP_Model by lazy { OPERATION_ULD_BUILDUP_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUldBuildupBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = uldModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.disableEditText(binding.uldNo.editText)
        item.limitEditText(binding.mawb.editText,11)
        item.limitEditText(binding.fltDate.editText,10)
        item.strEditText(binding.fltNo.editText)
        binding.pcs.editText.showSoftInputOnFocus = false
        binding.weight.editText.showSoftInputOnFocus = false
        item.strEditText(binding.spcCode.editText)
        item.strEditText(binding.spcRemark.editText)

        action.doneAction(binding.mawb.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])
                uldModel.setInfo2()
                Common.loadingOff()
            }
        }

        action.textChange_after(binding.fltDate.editText) { s ->
            if (uldModel.isEditing) return@textChange_after
            uldModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)

            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)

            val dateParam = Util.formatDate_8(result)

            if (dateParam.length == 8) {
                lifecycleScope.launch {
                    uldModel.setFlight()
                }
            }

            uldModel.isEditing = false
        }

        action.doneAction(binding.fltNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            val dateParam = Util.formatDate_8(uldModel.fltDate.value)

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                if (dateParam.replace("-", "").length < 8) {
                    Common.sendError("일자를 확인해주세요")
                    Common.loadingOff()
                    return@launch
                }

                uldModel.setFlight()

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

        action.doneAction(binding.spcCode.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }

            if (uldModel.spcList.isNotEmpty()) {
                getCommonParse(
                    subContent = uldModel.spcTable,
                    body = uldModel.spcList,
                    filterStr = Util.getStr(uldModel.spcCode.value),
                    filterColumn = listOf("CODE_CODE","CODE_NAME")
                )
            }
        }
        uldModel.workList.observe(viewLifecycleOwner) {
            binding.workList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = uldModel.workHeader,
                bodyList = it,
                selectValue = uldModel.workSelect.value,
                selectList = uldModel.workSelectMap,
                height = 260,
            ) {
                uldModel.workSelect.value =
                    Util.validSelectTable(uldModel.workSelect.value, it)
            }
            binding.workList.custItem.addView(table)
        }

        uldModel.mawb.observe(viewLifecycleOwner) {
            val isMawbSearch = it.length == 11

            binding.fltDate.editText.isEnabled = !isMawbSearch
            binding.fltNo.editText.isEnabled = !isMawbSearch
        }

        uldModel.workSelect.observe(viewLifecycleOwner) {
            val cargoSid = it["CARGO_CONTROL_SID"]
            if(!cargoSid.isNullOrEmpty()){
                uldModel.workSelectIndex(cargoSid)
            }else{
                uldModel.locationList.value = emptyList()
                uldModel.locationSelect.value = mapOf(
                    "CARGO_CONTROL_SID" to "",
                    "LOCATION_CODE" to ""
                )
            }
        }

        uldModel.locationList.observe(viewLifecycleOwner) {
            binding.locationList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = uldModel.locationHeader,
                bodyList = it,
                selectValue = mapOf(
                    "CARGO_CONTROL_SID" to Util.getTableCell(0,it,"CARGO_CONTROL_SID"),
                    "LOCATION_CODE" to Util.getTableCell(0,it,"LOCATION_CODE")
                ),
                height = 110,
            ) {
                uldModel.locationSelect.value =
                    Util.validSelectTable(uldModel.locationSelect.value, it)
            }
            binding.locationList.custItem.addView(table)
            uldModel.locationSelect.value = mapOf(
                "CARGO_CONTROL_SID" to Util.getTableCell(0,it,"CARGO_CONTROL_SID"),
                "LOCATION_CODE" to Util.getTableCell(0,it,"LOCATION_CODE")
            )
        }

        uldModel.locationSelect.observe(viewLifecycleOwner) {
            val cargoSid = it["CARGO_CONTROL_SID"]
            val locationCode = it["LOCATION_CODE"]

            if(cargoSid.isNullOrEmpty()||locationCode.isNullOrEmpty()){
                uldModel.pcs.value = ""
                uldModel.weight.value = ""
                uldModel.spcCode.value = ""
                uldModel.spcRemark.value = ""
            }else{
                val list = uldModel.locationList.value

                if(list.isNotEmpty()){
                    val rowTmp = list.find { row-> Util.getStr(row.row["CARGO_CONTROL_SID"]) ==cargoSid && Util.getStr(row.row["LOCATION_CODE"])==locationCode }
                    if(rowTmp != null){
                        uldModel.isEdit = true
                        uldModel.pcs.value = Util.getStr(rowTmp.row["NO_OF_PACKAGE"])
                        val workValue = uldModel.workSelect.value["SPECIAL_CARGO_CODE"]
                        uldModel.locationDist()
                        if(workValue!= null){
                            if(workValue.indexOf(",")<=0){
                                uldModel.spcCode.value = workValue
                            }else{
                                uldModel.spcRemark.value = workValue
                            }
                        }
                        uldModel.isEdit = false
                    }
                }

            }
        }

        uldModel.pcs.observe(viewLifecycleOwner) {
            if(!uldModel.isEdit && it.length >0){
                uldModel.locationDist()
            }
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
            val temp3 = route.params.find { it.first == "DATE" }?.second ?: ""
            val temp4 = route.params.find { it.first == "FLIGHT_NO" }?.second ?: ""
            val temp5 = route.params.find { it.first == "DEST" }?.second ?: ""
            val temp6 = route.params.find { it.first == "TO_LOCATION" }?.second ?: ""
            val temp7 = route.params.find { it.first == "CONTOUR_CODE" }?.second ?: ""
            val temp8 = route.params.find { it.first == "CONTOUR_NO" }?.second ?: ""
            val temp9 = route.params.find { it.first == "BULK_FLAG" }?.second ?: ""
            val temp10 = route.params.find { it.first == "ORIGIN" }?.second ?: ""
            val temp11 = route.params.find { it.first == "SCHEDULE_SID" }?.second ?: ""
            uldModel.operationSid = temp
            uldModel.uldNo.value = temp2
            uldModel.fltDate.value = temp3
            uldModel.fltNo.value = temp4
            uldModel.fltDest = temp5
            uldModel.toLocation = temp6
            uldModel.contourCode = temp7
            uldModel.contourNo = temp8
            uldModel.bulkFlag = temp9
            uldModel.fltOrigin = temp10
            uldModel.fltSelect = temp11
        }

        uldModel.setLoad()
        binding.mawb.editText.requestFocus()

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        uldModel.setSave()

                    }
                }
                is BottomItem.Tmp01 ->{
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        uldModel.allSave()
                    }
                }
                is BottomItem.Search ->{
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        lifecycleScope.launch {
                            Common.loadingOn(coroutineContext[Job])

                            uldModel.setInfo2()

                            Common.loadingOff()
                        }
                    }
                }
                else -> null
            }
        }
    }

}