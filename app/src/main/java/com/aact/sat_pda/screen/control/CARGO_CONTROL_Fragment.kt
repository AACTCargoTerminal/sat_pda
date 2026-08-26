package com.aact.sat_pda.screen.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.databinding.FragmentCargoControlBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_CONTROL_Fragment : BaseFragment() {
    private var _binding: FragmentCargoControlBinding? = null
    private val binding get() = _binding!!
    private val cargoModel: CARGO_CONTROL_Model by lazy { CARGO_CONTROL_Model() }
    private val header = Header()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoControlBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fltTable = SubContent.GetTable(
            headerList = header.fltHeader,
            bodylist = emptyList(),
            selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
            onClick = { value ->
                val select = value["SCHEDULE_SID"] ?: ""
                cargoModel.fltSelect.value = select
                cargoModel.fltNo.value = value["FLIGHT_NO"] ?: ""
                if (select.length > 0) {
                    cargoModel.getDest(select)
                }

            },
            endClick = {})
        val destTable = SubContent.GetTable(
            headerList = header.commonCodeHeader,
            bodylist = emptyList(),
            selectValue = mapOf("CODE_CODE" to ""),
            onClick = {
                cargoModel.fltDest.value = it["CODE_CODE"] ?: ""
            },
            endClick = {}
        )
        val agencyTable = SubContent.GetTable(
            headerList = header.agencyCodeHeader,
            bodylist = emptyList(),
            selectValue = mapOf("CUSTOMER_CODE" to ""),
            onClick = {
                cargoModel.company.value = it["CUSTOMER_CODE"] ?: ""
            },
            endClick = {}
        )


        val mawb = binding.mawb.editText
        val hawb = binding.hawb.editText
        val fltDest = binding.fltdest.editText
        val fltDate = binding.fltdate.editText
        val fltNo = binding.fltno.editText
        val company = binding.company.editText

        item.disableEditText(mawb)
        item.disableEditText(hawb)
        fltDate.showSoftInputOnFocus = false
        item.strEditText(fltDest)
        item.strEditText(fltNo)
        item.strEditText(company)

        cargoModel.bodyList.observe(viewLifecycleOwner) {
            binding.accept.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = cargoModel.headerList,
                bodyList = it,
                selectValue = cargoModel.selectList,
                height = 230,
            ) {
                cargoModel.selectList =
                    Util.validSelectTable(cargoModel.selectList, it)
            }
            binding.accept.custItem.addView(table)
        }

        action.textChange_after(fltDate) { s ->
            if (cargoModel.isEditing) return@textChange_after
            cargoModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            fltDate.setText(result)
            fltDate.setSelection(result.length)

            if (result.replace("-", "").length == 8) {
                lifecycleScope.launch {

                    Common.loadingOn(coroutineContext[Job])
                    val ret = cargoModel.biz.getFltight(result.replace("-", ""))
                    ret?.let { data ->
                        cargoModel.fltList.value = data
                    }
                    Common.loadingOff()
                }
            }


            cargoModel.isEditing = false
        }

        cargoModel.fltList.observe(viewLifecycleOwner) {
            action.doneAction(fltNo) {
                if (!it.isNullOrEmpty()) {
                    getCommonParse(
                        subContent = fltTable,
                        body = it,
                        filterStr = cargoModel.fltNo.value,
                        filterColumn = listOf("FLIGHT_NO")
                    )
                } else {
                    Common.sendError("해당 편번이 없습니다.")
                }
            }
        }



        action.doneAction(fltDest) {
            if (keboardFlag) {
                keyboardCtl()
            }
            fltDest.clearFocus()

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret =
                    cargoModel.biz.getCommonCode("APORT", Util.getStr(cargoModel.fltDest.value))

                if (!ret.isNullOrEmpty()) {
                    getCommonParse(
                        subContent = destTable,
                        body = ret,
                        filterStr = Util.getStr(cargoModel.fltDest.value),
                        filterColumn = listOf("CODE_CODE")
                    )
                }
                Common.loadingOff()
            }

        }
        action.doneAction(company) {
            if (keboardFlag) {
                keyboardCtl()
            }
            company.clearFocus()

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])
                val ret =
                    cargoModel.biz.getAgencyCode("AGENCY", Util.getStr(cargoModel.company.value))

                if (ret == null) {
                    Common.sendError("해당 대리점은 없습니다.")
                } else {
                    getCommonParse(
                        subContent = agencyTable,
                        body = ret,
                        filterStr = Util.getStr(cargoModel.company.value),
                        filterColumn = emptyList()
                    )
                }
                Common.loadingOff()
            }


        }
    }

    override fun onStart() {
        super.onStart()
        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            cargoModel.cargoControlSid = temp
        }

        cargoModel.startProc()

        for (item in route.bottomItems()) {
            when (item) {

                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoModel.updateProc(
                            Util.getStr(cargoModel.company.value),
                            Util.getStr(cargoModel.fltDest.value)
                        )
                    }
                }

                is BottomItem.Print -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        showYesNo("출력", "슬립지를 출력하시겠습니까?", onYes = { cargoModel.printProc() })
                    }
                }
                is BottomItem.Camera -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if(cargoModel.cargoControlSid.length <= 1){
                            Common.sendError("조회된 화물이없습니다.")
                            return
                        }

                        val params = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID",cargoModel.cargoControlSid)
                        )

                        route.params = params

                        val params1 = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", cargoModel.cargoControlSid),
                            Pair("MAWB", cargoModel.mawb.value)
                        )
                        //카메라이동
                        val camera = Route.Camera
                        camera.params = params1
                        Common.addNavigate(camera)
                    }
                }

                is BottomItem.Location -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", cargoModel.cargoControlSid)
                        )
                        val acceptSid = cargoModel.selectList["CARGO_ACCEPT_SID"]

                        if (acceptSid.isNullOrEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }
                        // 이동
                        val location = Route.Location
                        location.params = params
                        Common.addNavigate(location)
                    }
                }
                is BottomItem.Add -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoModel.inClick()
                    }
                }
                is BottomItem.Exit ->{
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoModel.cancelClick()
                    }
                }
                is BottomItem.Scale -> {
                    item.onClick = fun(){
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        val acceptSid = cargoModel.selectList["CARGO_ACCEPT_SID"]

                        if(acceptSid.isNullOrEmpty()){
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }
                        val params = listOf(
                            Pair("CARGO_CONTROL_SID",cargoModel.cargoControlSid),
                            Pair("CARGO_ACCEPT_SID",acceptSid),
                            Pair("MAWB",cargoModel.mawb.value)
                        )
                        //스케일 이동
                        val scale = Route.Scale
                        scale.params = params
                        Common.addNavigate(scale)
                    }
                }
                is BottomItem.Tmp01 -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        val acceptSid = cargoModel.selectList["CARGO_ACCEPT_SID"]

                        if (acceptSid.isNullOrEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        val selectedRow = cargoModel.bodyList.value?.find {
                            it.row["CARGO_ACCEPT_SID"] == acceptSid
                        }

                        val acceptTime = selectedRow?.row?.get("ACCEPTED_TIME") ?: ""

                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", cargoModel.cargoControlSid),
                            Pair("CARGO_ACCEPT_SID", acceptSid),
                            Pair("CARGO_ACCEPT_TIME", acceptTime),
                            Pair("MAWB", cargoModel.mawb.value.orEmpty()),
                            Pair("FLIGHT_NO", cargoModel.fltNo.value.orEmpty()),
                            Pair("FLIGHT_DATE", cargoModel.fltDate.value.orEmpty())
                        )

                        val volume = Route.Volume
                        volume.params = params
                        Common.addNavigate(volume)
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