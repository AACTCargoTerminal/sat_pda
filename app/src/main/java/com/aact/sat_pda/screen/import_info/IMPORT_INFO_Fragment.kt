package com.aact.sat_pda.screen.import_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentImportInfoBinding
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_INFO_Fragment : BaseFragment() {
    private var _binding: FragmentImportInfoBinding? = null
    private val binding get() = _binding!!
    private val model: IMPORT_INFO_Model by lazy { IMPORT_INFO_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImportInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.mawb.editText,11)
        item.disableEditText(binding.hawb.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        item.disableEditText(binding.assign.editText)
        item.disableEditText(binding.mfcsPcs.editText)
        item.disableEditText(binding.mfcsWt.editText)
        item.disableEditText(binding.inPcs.editText)
        item.disableEditText(binding.inWt.editText)
        item.disableEditText(binding.outPcs.editText)
        item.disableEditText(binding.outWt.editText)
        item.disableEditText(binding.desc.editText)
        item.disableEditText(binding.status.editText)

        action.doneAction(binding.cargoNo.editText) {
            model.setInfo()
        }
        action.doneAction(binding.mawb.editText) {
            model.setHAWBList{ table->
                    if(table.size ==1){
                        model.setInfoMawb()
                    }else if(table.size > 1){
                        getCommonParse(
                            subContent = model.modalTable,
                            body = table,
                            filterStr = "",
                            filterColumn = listOf("HOUSE_AIR_WAY_BILL_NO")
                        )
                    }else{
                        model.setInfoMawb()
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.mawb.editText.requestFocus()

        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "CARGO_CONTROL_NO" }?.second ?: ""
            model.cargoNo.value = temp
        }

        if(model.cargoNo.value.isNotEmpty()){
            model.setInfo()
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Camera -> {
                    item.onClick = fun(){
                        if(model.cargoNo.value.isEmpty()){
                            Common.sendError("수입화물이 조회되지 않았습니다.")
                            return
                        }

                        val originParams = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_NO",model.cargoNo.value)
                        )

                        route.params = originParams

                        val params1 = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_SID", model.cargoSid),
                            Pair("MAWB", model.mawb.value)
                        )
                        //카메라이동
                        val camera = Route.Camera
                        camera.params = params1
                        Common.addNavigate(camera)
                    }
                }
                is BottomItem.Tmp01 -> {
                    item.onClick = fun(){
                        if(model.cargoNo.value.isEmpty()){
                            Common.sendError("수입화물이 조회되지 않았습니다.")
                            return
                        }

                        val originParams = listOf<Pair<String, String>>(
                            Pair("CARGO_CONTROL_NO",model.cargoNo.value)
                        )

                        route.params = originParams

                        val params = listOf(Pair("CARGO_CONTROL_NO",model.cargoNo.value),Pair("CARGO_CONTROL_SID",model.cargoSid))

                        val rt = Route.ImportIrrList

                        rt.params = params

                        Common.addNavigate(rt)
                    }
                }
                else -> null
            }
        }
    }
}