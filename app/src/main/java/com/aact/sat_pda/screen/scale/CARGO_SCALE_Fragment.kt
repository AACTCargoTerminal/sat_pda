package com.aact.sat_pda.screen.scale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.databinding.FragmentCargoScaleBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_SCALE_Fragment : BaseFragment() {
    private var _binding: FragmentCargoScaleBinding? = null
    private val binding get() = _binding!!
    private val cargoScaleModel: CARGO_SCALE_Model by lazy { CARGO_SCALE_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCargoScaleBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoScaleModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mawb = binding.mawb.editText
        val acceptList = binding.acceptList.combo
        val statusNo = binding.statusNo.editText
        val fltDate = binding.fltDate.editText
        val fltNo = binding.fltNo.editText
        val fltDest = binding.fltDest.editText
        val agent = binding.agent.editText
        val pcs = binding.pcs.editText
        val weight = binding.weight.editText
        val spc = binding.spc.editText
        val scaleNo = binding.scaleNo.combo
        val pallet = binding.pallet.combo
        val palletPcs = binding.palletPcs.editText
        val palletWt = binding.palletWt.editText
        val netWt = binding.netWt.editText
        val volumeWt = binding.volumeWt.editText
        val acceptPcs = binding.acceptPcs.editText
        val acceptWt = binding.acceptWt.editText
        val mfstPcs = binding.mfstPcs.editText
        val mfstWt = binding.mfstWt.editText

        item.limitEditText(binding.mawb.editText, 11)
        item.disableEditText(statusNo)
        fltDate.showSoftInputOnFocus = false
        item.strEditText(fltNo)
        item.strEditText(fltDest)
        item.strEditText(agent)
        pcs.showSoftInputOnFocus = false
        weight.showSoftInputOnFocus = false
        item.strEditText(spc)
        palletPcs.showSoftInputOnFocus = false
        palletWt.showSoftInputOnFocus = false
        item.disableEditText(netWt)
        item.disableEditText(volumeWt)
        item.disableEditText(acceptPcs)
        item.disableEditText(acceptWt)
        mfstPcs.showSoftInputOnFocus = false
        mfstWt.showSoftInputOnFocus = false

        cargoScaleModel.mawb.observe(viewLifecycleOwner) {
            if (it == "") {
                cargoScaleModel.setClearAll()
            } else if (it.length == 11) {
                if (keboardFlag) {
                    keyboardCtl()
                }
                cargoScaleModel.getAcceptList()
            }

        }

        cargoScaleModel.acceptSelect.observe(viewLifecycleOwner) {
            if (it.length > 0) {
                pcs.requestFocus()
            }
        }

        cargoScaleModel.acceptList.observe(viewLifecycleOwner) {
            val displayList = it.values.map { value ->
                val splitValue = value.split(",")
                val acceptInfo = splitValue.getOrNull(0) ?: ""
                val statusCode = splitValue.getOrNull(1)?.trim() ?: ""
                "$acceptInfo , $statusCode"
            }.toTypedArray()

            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                displayList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            acceptList.setAdapter(adapter)
            val acceptSelectData = it.keys.indexOf(cargoScaleModel.acceptSelect.value)
            if (acceptSelectData < 0) {
                acceptList.setSelection(0)
            } else {
                acceptList.setSelection(acceptSelectData)
            }
        }

        acceptList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val key = cargoScaleModel.acceptList.value?.keys?.elementAt(position) ?: ""
                val value = cargoScaleModel.acceptList.value?.values?.elementAt(position) ?: ""


                cargoScaleModel.acceptSelect.value = key
                cargoScaleModel.acceptSeq.value = value.split(":").getOrNull(0) ?: ""
                cargoScaleModel.cargoStatusCode = value.split(",").getOrNull(1)?.trim() ?: ""

                viewLifecycleOwner.lifecycleScope.launch {
                    cargoScaleModel.setPaperPCSWT()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        cargoScaleModel.palletPcs.observe(viewLifecycleOwner) {
            cargoScaleModel.setPalletCumpute()
        }

        cargoScaleModel.scaleRadio.observe(viewLifecycleOwner) {

            val tempList: List<String> = it?.let { array ->
                array.mapIndexed { index, item ->
                    when {
                        index == 0 -> "1"
                        index == 1 -> "2"
                        index == 2 -> "3"
                        index == 3 -> if (Common.terminalCode.value == "T2") "X2" else null
                        else -> null
                    }

                }
            }?.filterNotNull() ?: run { emptyList() }

            val adapter =
                ArrayAdapter(
                    binding.root.context,
                    android.R.layout.simple_spinner_item,
                    tempList
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
            scaleNo.setAdapter(adapter)
            if (tempList?.size ?: 0 > cargoScaleModel.scaleRadioSelect.value) {
                scaleNo.setSelection(cargoScaleModel.scaleRadioSelect.value)
            }

        }
        scaleNo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cargoScaleModel.scaleRadioSelect.value = position
                val terminalCode = Common.terminalCode.value
                cargoScaleModel.weight.value = ""

                if (terminalCode == "T2" && position == 1) {
                    item.disableEditText(weight)
                } else {
                    item.numDefEditText(weight)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        action.doneAction(mawb) {
            if (keboardFlag) {
                keyboardCtl()
            }
            cargoScaleModel.getAcceptList()
        }

        action.doneAction(fltNo) {
            if (keboardFlag) {
                keyboardCtl()
            }
            cargoScaleModel.fltSelect.value = ""
            cargoScaleModel.fltDest.value = ""
            cargoScaleModel.setFlight { data ->
                getCommonParse(
                    subContent = cargoScaleModel.fltTable,
                    body = data,
                    filterStr = cargoScaleModel.fltNo.value,
                    filterColumn = listOf("FLIGHT_NO")
                )
            }
            fltNo.clearFocus()
        }
        val destTable = SubContent.GetTable(
            headerList = Header().commonCodeHeader,
            bodylist = emptyList(),
            selectValue = mapOf("CODE_CODE" to ""),
            onClick = {
                cargoScaleModel.fltDest.value = it["CODE_CODE"] ?: ""
            },
            endClick = {}
        )

        action.doneAction(fltDest) {
            if (keboardFlag) {
                keyboardCtl()
            }
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret =
                    cargoScaleModel.biz.getCommonCode(
                        "APORT",
                        Util.getStr(cargoScaleModel.fltDest.value)
                    )

                if (!ret.isNullOrEmpty()) {
                    getCommonParse(
                        subContent = destTable,
                        body = ret,
                        filterStr = Util.getStr(cargoScaleModel.fltDest.value),
                        filterColumn = listOf("CODE_CODE")
                    )
                }
                fltDest.clearFocus()
                Common.loadingOff()
            }
        }

        val agencyTable = SubContent.GetTable(
            headerList = Header().agencyCodeHeader,
            bodylist = emptyList(),
            selectValue = mapOf("CUSTOMER_CODE" to "", "AGENCY_CODE" to "", "CUSTOMER_NAME" to ""),
            onClick = {
                cargoScaleModel.agent.value = it["AGENCY_CODE"] ?: ""
                cargoScaleModel.agentName.value = it["CUSTOMER_NAME"] ?: ""
                cargoScaleModel.customerCode = it["CUSTOMER_CODE"] ?: ""
            },
            endClick = {}
        )

        action.doneAction(agent) {
            if (keboardFlag) {
                keyboardCtl()
            }
            agent.clearFocus()

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = cargoScaleModel.biz.getAgencyCode(
                    "AGENCY",
                    Util.getStr(cargoScaleModel.agent.value)
                )

                if (ret == null) {
                    Common.sendError("해당 대리점은 없습니다.")
                } else {
                    getCommonParse(
                        subContent = agencyTable,
                        body = ret,
                        filterStr = Util.getStr(cargoScaleModel.agent.value),
                        filterColumn = listOf("CUSTOMER_CODE", "AGENCY_CODE", "CUSTOMER_NAME")
                    )
                }


                Common.loadingOff()
            }


        }

        cargoScaleModel.fltSelect.observe(viewLifecycleOwner) {

            if (it.length != 0 && cargoScaleModel.fltDest.value.length != 3) {
                lifecycleScope.launch {
                    val ret = cargoScaleModel.biz.getOriginDest(it)
                    if (ret == null) {
                        cargoScaleModel.fltDest.value = ""
                    } else {
                        cargoScaleModel.fltDest.value = ret.row["DESTINATION_CODE"] ?: ""
                    }
                }
            }


        }


        cargoScaleModel.scaleRadioSelect.observe(viewLifecycleOwner) {
            if (cargoScaleModel.scaleRadio.value?.size ?: 0 > it) {
                scaleNo.setSelection(it)
            }

        }

        cargoScaleModel.palletList.observe(viewLifecycleOwner) {
            val data = Util.getOneColumn(it, "CODE_NAME")
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                data
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            pallet.setAdapter(adapter)
            if (it?.size ?: 0 > cargoScaleModel.palletSelect.value) {
                pallet.setSelection(cargoScaleModel.palletSelect.value)
            }
        }

        pallet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cargoScaleModel.palletSelect.value = position
                cargoScaleModel.setPalletCumpute()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        cargoScaleModel.palletSelect.observe(viewLifecycleOwner) {
            if (cargoScaleModel.palletList.value?.size ?: 0 > it) {
                pallet.setSelection(it)
            }
        }

        cargoScaleModel.spcList.observe(viewLifecycleOwner) {
            action.doneAction(spc) {
                if (keboardFlag) {
                    keyboardCtl()
                }
                getCommonParse(
                    subContent = cargoScaleModel.modalTable,
                    body = it,
                    filterStr = cargoScaleModel.spc.value,
                    filterColumn = listOf("CODE_CODE", "CODE_NAME")
                )
            }
        }

        cargoScaleModel.spcSelect.observe(viewLifecycleOwner) {
            cargoScaleModel.spc.value = it?.second ?: ""
        }

        cargoScaleModel.weight.observe(viewLifecycleOwner) {
            setNetWt(it, cargoScaleModel.palletWt.value)
        }

        cargoScaleModel.palletWt.observe(viewLifecycleOwner) {
            setNetWt(cargoScaleModel.weight.value, it)
        }

        action.textChange_after(fltDate) { s ->
            if (cargoScaleModel.isEditing) return@textChange_after
            cargoScaleModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            fltDate.setText(result)
            fltDate.setSelection(result.length)

            if (result.replace("-", "").length != 8) {
                cargoScaleModel.fltNo.value = ""
                cargoScaleModel.fltSelect.value = ""
                cargoScaleModel.fltDest.value = ""
            }

            cargoScaleModel.isEditing = false
        }

        action.textChange_after(fltNo) { s ->
            if (cargoScaleModel.isEditing) return@textChange_after
            cargoScaleModel.isEditing = true
            val str = s?.toString() ?: ""
            if (str.length == 0) {
                cargoScaleModel.fltDest.value = ""
                cargoScaleModel.fltSelect.value = ""
            }
            cargoScaleModel.isEditing = false
        }


    }


    override fun onStart() {
        super.onStart()
        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            val temp2 =
                route.params.find { it.first == "CARGO_ACCEPT_SID" }?.second ?: ""
            val temp3 =
                route.params.find { it.first == "MAWB" }?.second ?: ""
            cargoScaleModel.cargoControlSid = temp
            cargoScaleModel.acceptSelect.value = temp2
            cargoScaleModel.mawb.value = temp3
        }
        cargoScaleModel.getList()
        val mawb = binding.mawb.editText
        val pcs = binding.pcs.editText
        if (Util.getStr(cargoScaleModel.mawb.value).length > 0) {
            pcs.post { pcs.requestFocus() }
        } else {
            mawb.post { mawb.requestFocus() }
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Scale -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoScaleModel.scaleClick()
                    }
                }

                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoScaleModel.saveClick { data ->
                            val ret = data?.table
                            ret?.let {
                                showYesNo(
                                    "Message",
                                    "현재 화물은 입고가 되어있습니다. 접수 화면으로 가시겠습니까?",
                                    onYes = {

                                        val originParams = listOf<Pair<String, String>>(
                                            Pair(
                                                "CARGO_CONTROL_SID",
                                                cargoScaleModel.cargoControlSid
                                            ),
                                            Pair(
                                                "CARGO_ACCEPT_SID",
                                                cargoScaleModel.acceptSelect.value
                                            ),
                                            Pair("MAWB", cargoScaleModel.mawb.value),
                                        )

                                        route.params = originParams

                                        val params = listOf<Pair<String, String>>(
                                            Pair(
                                                "CARGO_ACCEPT_SID",
                                                cargoScaleModel.acceptSelect.value
                                            ),
                                            Pair(
                                                "CARGO_CONTROL_SID",
                                                cargoScaleModel.cargoControlSid
                                            ),
                                            Pair(
                                                "CARGO_ACCEPT_SEQ",
                                                cargoScaleModel.acceptSeq.value
                                            ),
                                            Pair("MAWB", cargoScaleModel.mawb.value),
                                            Pair("FLIGHT_NO", cargoScaleModel.fltNo.value),
                                            Pair("FLIGHT_DEST", cargoScaleModel.fltDest.value)
                                        )
                                        val accept = Route.Accept
                                        accept.params = params
                                        Common.addNavigate(accept)
                                    },
                                    onNo = {
                                        return@showYesNo
                                    }
                                )
                            }
                        }
                    }

                }

                is BottomItem.List -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (cargoScaleModel.acceptSelect.value == "" || cargoScaleModel.cargoControlSid == "" || cargoScaleModel.mawb.value == "") {
                            Common.sendError("조회한 화물이 없습니다.")
                            return
                        }

                        val originParams = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                            Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                            Pair("MAWB", cargoScaleModel.mawb.value),
                        )

                        route.params = originParams

                        val params = listOf<Pair<String, String>>(
                            Pair(
                                "CARGO_ACCEPT_SID",
                                cargoScaleModel.acceptSelect.value
                            ),
                            Pair(
                                "CARGO_CONTROL_SID",
                                cargoScaleModel.cargoControlSid
                            ),
                            Pair(
                                "CARGO_ACCEPT_SEQ",
                                cargoScaleModel.acceptSeq.value
                            ),
                            Pair("MAWB", cargoScaleModel.mawb.value),
                            Pair("FLIGHT_NO", cargoScaleModel.fltNo.value),
                            Pair("FLIGHT_DATE", cargoScaleModel.fltDate.value)
                        )
                        val scaleList = Route.ScaleList
                        scaleList.params = params
                        Common.addNavigate(scaleList)
                    }
                }

                is BottomItem.Location -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        if (cargoScaleModel.mawb.value.length > 0
                            && cargoScaleModel.fltDate.value.length > 0
                            && cargoScaleModel.fltSelect.value.length > 0
                            && cargoScaleModel.fltDest.value.length > 0
                            && cargoScaleModel.pcs.value.length > 0
                            && cargoScaleModel.weight.value.length > 0
                            && cargoScaleModel.netWt.value.length > 0
                        ) {

                            cargoScaleModel.saveClick { row ->

                                if (cargoScaleModel.cargoControlSid.length == 0 || cargoScaleModel.acceptSelect.value.length == 0) {
                                    Common.sendError("저장되지 않았습니다. 저장후 진행해주세요")
                                    return@saveClick
                                }

                                val originParams = listOf<Pair<String, String>>(
                                    Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                                    Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                                    Pair("MAWB", cargoScaleModel.mawb.value),
                                )

                                route.params = originParams

                                val params = listOf<Pair<String, String>>(
                                    Pair(
                                        "CARGO_ACCEPT_SID",
                                        cargoScaleModel.acceptSelect.value
                                    ),
                                    Pair(
                                        "CARGO_CONTROL_SID",
                                        cargoScaleModel.cargoControlSid
                                    ),
                                    Pair("MAWB", cargoScaleModel.mawb.value),
                                    Pair("FLIGHT_NO", cargoScaleModel.fltNo.value),
                                    Pair("FLIGHT_DATE", cargoScaleModel.fltDate.value)
                                )
                                //볼륨 이동
                                val volume = Route.Volume
                                volume.params = params
                                Common.addNavigate(volume)
                            }
                        } else {
                            val originParams = listOf<Pair<String, String>>(
                                Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                                Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                                Pair("MAWB", cargoScaleModel.mawb.value),
                            )

                            route.params = originParams

                            val params = listOf<Pair<String, String>>(
                                Pair(
                                    "CARGO_ACCEPT_SID",
                                    cargoScaleModel.acceptSelect.value
                                ),
                                Pair(
                                    "CARGO_CONTROL_SID",
                                    cargoScaleModel.cargoControlSid
                                ),
                                Pair("MAWB", cargoScaleModel.mawb.value),
                                Pair("FLIGHT_NO", cargoScaleModel.fltNo.value),
                                Pair("FLIGHT_DATE", cargoScaleModel.fltDate.value)
                            )
                            //볼륨이동
                            val volume = Route.Volume
                            volume.params = params
                            Common.addNavigate(volume)

                        }
                    }
                }

                is BottomItem.Add -> {
                    item.onClick = fun() {
                        val seq = cargoScaleModel.acceptSeq.value
                        val sid = cargoScaleModel.acceptSelect.value
                        if (seq.length == 0 || sid.length == 0) {
                            Common.sendError("입고 처리할 접수정보가 없습니다.")
                            return
                        }

                        val originParmas = listOf(
                            Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                            Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                            Pair("MAWB", cargoScaleModel.mawb.value)
                        )

                        Common.route.value.params = originParmas

                        val params =
                            listOf(Pair("CARGO_ACCEPT_SID", sid), Pair("CARGO_ACCEPT_SEQ", seq), Pair("AUTO_FLAG", "Y"))

                        val route = Route.Stock
                        route.params = params
                        Common.addNavigate(route)

                    }
                }

                is BottomItem.Tmp01 -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        if (cargoScaleModel.mawb.value.trim().length == 0) {
                            Common.sendError("MAWB 정보가 없습니다.")

                            binding.mawb.editText.requestFocus()
                            return
                        }

                        if (cargoScaleModel.acceptSelect.value.length == 0) {
                            Common.sendError("접수정보가 없습니다. 접수 후 진행해주세요.")
                            return
                        }

                        val acceptValue = cargoScaleModel.acceptList.value?.get(cargoScaleModel.acceptSelect.value) ?: ""

                        if (acceptValue.length == 0) {
                            Common.sendError("선택한 접수정보를 찾을 수 없습니다.")
                            return
                        }

                        val totalPalletCnt = acceptValue.split(",").getOrNull(2)?.trim() ?: ""

                        val originParams =
                            listOf<Pair<String, String>>(
                                Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                                Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                                Pair("MAWB", cargoScaleModel.mawb.value)
                            )

                        route.params = originParams

                        val params =
                            listOf<Pair<String, String>>(
                                Pair("CARGO_ACCEPT_SID", cargoScaleModel.acceptSelect.value),
                                Pair("CARGO_CONTROL_SID", cargoScaleModel.cargoControlSid),
                                Pair("MAWB", cargoScaleModel.mawb.value),
                                Pair("TOTAL_PALLET_CNT", totalPalletCnt)
                            )

                        val palletUpdate = Route.PalletUpdate
                        palletUpdate.params = params
                        Common.addNavigate(palletUpdate)
                    }
                }

                is BottomItem.Print -> {
                    item.onClick = fun() {
                        cargoScaleModel.printClick()
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

    fun setNetWt(weight: String, palletWt: String) {
        val netWt = binding.netWt.editText
        val temp_wt = Util.getDouble(weight) - Util.getDouble(palletWt)
        netWt.setText(temp_wt.toString())
    }
}