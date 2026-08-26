package com.aact.sat_pda.screen.control_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.BizAPI
import com.aact.sat_pda.Biz.Header
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.databinding.FragmentCargoListBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_CONTROL_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentCargoListBinding? = null
    private val binding get() = _binding!!
    private val controlListModel: CARGO_CONTROL_LIST_Model by lazy { CARGO_CONTROL_LIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = controlListModel

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val modalTable = SubContent.GetTable(
            headerList = Header().fltHeader,
            bodylist = emptyList(),
            selectValue = mapOf("SCHEDULE_SID" to "", "FLIGHT_NO" to ""),
            onClick = { value ->
                controlListModel.fltSelect = value["SCHEDULE_SID"] ?: ""
                controlListModel.fltNo.value = value["FLIGHT_NO"] ?: ""

                controlListModel.selectClick(
                    mawb = Util.getStr(controlListModel.mawb.value),
                    fltDate = controlListModel.fltDate.value.replace("-", ""),
                    fltNo = Util.getStr(controlListModel.fltNo.value)
                )
            },
            endClick = {})

        val mawbEdit = binding.mawb.editText
        val fltDateEdit = binding.fltdate.editText
        val fltNoEdit = binding.fltno.editText

        item.limitEditText(mawbEdit,11)
        fltDateEdit.showSoftInputOnFocus = false
        item.strEditText(fltNoEdit)

        controlListModel.mawb.observe(viewLifecycleOwner) {
            fltDateEdit.isEnabled = it.length != 11
            fltNoEdit.isEnabled = it.length != 11

            if(it.length == 11){
                val txt = Util.getStr(it)
                controlListModel.selectClick(
                    mawb = txt,
                    fltDate = controlListModel.fltDate.value.replace("-", ""),
                    fltNo = Util.getStr(controlListModel.fltNo.value)
                )
                mawbEdit.clearFocus()
            }
        }

        controlListModel.fltBodyList.observe(viewLifecycleOwner) {
            action.doneAction(fltNoEdit) {

                if(!it.isNullOrEmpty()){
                    getCommonParse(
                        subContent = modalTable,
                        body = it,
                        filterStr = controlListModel.fltNo.value,
                        filterColumn = listOf("FLIGHT_NO")
                    )
                }else{
                    Common.sendError("해당 편번이 없습니다.")
                }


            }

        }

        controlListModel.bodyList.observe(viewLifecycleOwner) { value ->
            val count = value?.size ?: 0
            val mawb_text = Util.getStr(controlListModel.mawb.value)

            when (count) {
                0 -> {
                    if (mawb_text.length == 11) {
                        //화물계량 이동
                        val param = listOf(Pair("MAWB",mawb_text))
                        val scale = Route.Scale
                        scale.params = param
                        Common.addNavigate(scale)
                    } else {
                        binding.cargoTableContent.removeAllViews()
                    }
                }

                1 -> {
                    if(controlListModel.returnFromControl){
                        controlListModel.returnFromControl = false

                        binding.cargoTableContent.removeAllViews()
                        val table = item.getTable_Click(
                            context = binding.root.context,
                            headerList = controlListModel.headerList,
                            bodyList = value,
                            selectValue = controlListModel.selectList,
                            height = 400,
                        ) {
                            controlListModel.selectList =
                                Util.validSelectTable(controlListModel.selectList, it)
                        }

                        binding.cargoTableContent.addView(table)
                    }else{
                        controlListModel.saveFltDate = controlListModel.fltDate.value
                        controlListModel.saveFltNo = controlListModel.fltNo.value
                        controlListModel.saveScheduleSid = controlListModel.fltSelect
                        controlListModel.returnFromControl = true

                        //수출화물 이동
                        val params =
                            listOf(Pair("CARGO_CONTROL_SID", value[0].row["CARGO_CONTROL_SID"] ?: ""))

                        val control = Route.Control
                        control.params = params
                        Common.addNavigate(control)
                    }
                }

                else -> {
                    binding.cargoTableContent.removeAllViews()
                    val table = item.getTable_Click(
                        context = binding.root.context,
                        headerList = controlListModel.headerList,
                        bodyList = value,
                        selectValue = controlListModel.selectList,
                        height = 400,
                    ) {
                        controlListModel.selectList =
                            Util.validSelectTable(controlListModel.selectList, it)
                    }

                    binding.cargoTableContent.addView(table)
                }
            }
        }



        action.doneAction(mawbEdit) {
            val txt = Util.getStr(controlListModel.mawb.value)
            controlListModel.selectClick(
                mawb = txt,
                fltDate = controlListModel.fltDate.value.replace("-", ""),
                fltNo = Util.getStr(controlListModel.fltNo.value)
            )
            mawbEdit.clearFocus()
        }

        action.textChange_after(fltDateEdit) { s ->
            if (controlListModel.isEditing || controlListModel.settingRouteParams) return@textChange_after
            controlListModel.isEditing = true

            val str = s?.toString() ?: ""

            val result = Util.validDate(str)
            fltDateEdit.setText(result)
            fltDateEdit.setSelection(result.length)

            if (result.replace("-", "").length == 8) {
                controlListModel.fltSelect = ""
                controlListModel.fltNo.value = ""

                lifecycleScope.launch {

                    Common.loadingOn(coroutineContext[Job])
                    val ret = BizAPI().getFltight(result.replace("-", ""))
                    ret?.let { data ->
                        controlListModel.fltBodyList.value = data
                    }
                    Common.loadingOff()
                }

                controlListModel.selectClick(
                    mawb = "",
                    fltDate = result.replace("-", ""),
                    fltNo = "",
                    firstLoad = true
                )
            }

            controlListModel.isEditing = false
        }


    }

    override fun onStart() {
        super.onStart()

        if(controlListModel.saveFltDate.isNotEmpty()){
            controlListModel.returnFromControl = true

            val restoreDate = controlListModel.saveFltDate
            val restoreFltNo = controlListModel.saveFltNo
            val restoreScheduleSid = controlListModel.saveScheduleSid

            controlListModel.settingRouteParams = true
            controlListModel.fltDate.value = restoreDate

            binding.root.post {
                controlListModel.fltNo.value = restoreFltNo
                controlListModel.fltSelect = restoreScheduleSid
                controlListModel.settingRouteParams = false

                val date = restoreDate.replace("-", "")

                lifecycleScope.launch {
                    Common.loadingOn(coroutineContext[Job])
                    val ret = BizAPI().getFltight(date)
                    ret?.let { data ->
                        controlListModel.fltBodyList.value = data
                    }
                    Common.loadingOff()
                }

                if(restoreFltNo.isNotEmpty() && restoreScheduleSid.isNotEmpty()){
                    controlListModel.selectClick(
                        mawb = "",
                        fltDate = date,
                        fltNo = restoreFltNo
                    )
                }else{
                    controlListModel.selectClick(
                        mawb = "",
                        fltDate = date,
                        fltNo = "",
                        firstLoad = true
                    )
                }
            }

            controlListModel.saveFltDate = ""
            controlListModel.saveFltNo = ""
            controlListModel.saveScheduleSid = ""
        }else{
            binding.mawb.editText.post {
                binding.mawb.editText.requestFocus()
            }
            controlListModel.fltDate.value = Util.getNowDate()
            controlListModel.fltSelect = ""
            controlListModel.fltNo.value = ""

            controlListModel.selectClick(
                mawb = "",
                fltDate = controlListModel.fltDate.value.replace("-", ""),
                fltNo = "",
                firstLoad = true
            )
        }

        val route = Common.route.value.bottomItems()

        for(item in route){
            when(item){
                is BottomItem.Search ->{
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        val txt = Util.getStr(controlListModel.mawb.value)
                        controlListModel.selectClick(
                            mawb = txt,
                            fltDate = controlListModel.fltDate.value.replace("-", ""),
                            fltNo = Util.getStr(controlListModel.fltNo.value)
                        )
                        binding.mawb.editText.clearFocus()
                    }
                }

                is BottomItem.Add ->{
                    item.onClick = fun(){
                        //접수 링크

                        val mawb = controlListModel.selectList["MASTER_AIR_WAY_BILL_NO"] ?: ""
                        val txtMawb = controlListModel.mawb.value
                        if(mawb.length == 0 && txtMawb.length == 0){
                            Common.sendError("선택된 화물이 없습니다.")
                            return
                        }



                        val params = listOf(
                            Pair("MAWB", if(mawb.length == 0) txtMawb else mawb)
                        )

                        val accept = Route.Accept
                        accept.params = params
                        Common.addNavigate(accept)
                    }
                }

                is BottomItem.Open ->{
                    item.onClick = {
                        val sid = controlListModel.selectList["CARGO_CONTROL_SID"]
                        if (!sid.isNullOrEmpty()) {
                            controlListModel.saveFltDate = controlListModel.fltDate.value
                            controlListModel.saveFltNo = controlListModel.fltNo.value
                            controlListModel.saveScheduleSid = controlListModel.fltSelect
                            controlListModel.returnFromControl = true

                            val params = listOf(
                                Pair("CARGO_CONTROL_SID", sid)
                            )
                            // Control이동
                            val control = Route.Control
                            control.params = params
                            Common.addNavigate(control)
                        } else {
                            Common.sendError("선택한 화물이없습니다.")
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