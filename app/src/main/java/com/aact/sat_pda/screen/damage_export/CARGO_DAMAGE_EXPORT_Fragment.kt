package com.aact.sat_pda.screen.damage_export

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aact.sat_pda.Adapter.TreeAdapter
import com.aact.sat_pda.Biz.GroupDividerDecoration
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentCargoDamageExportBinding
import com.aact.sat_pda.screen.BaseFragment

class CARGO_DAMAGE_EXPORT_Fragment : BaseFragment() {
    private var _binding: FragmentCargoDamageExportBinding? = null
    private val binding get() = _binding!!
    private val damageModel: CARGO_DAMAGE_EXPORT_Model by lazy { CARGO_DAMAGE_EXPORT_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoDamageExportBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = damageModel

        item.limitEditText(binding.mawb.editText,11)
        binding.fltDate.editText.showSoftInputOnFocus = false
        item.strEditText(binding.fltNo.editText)
        binding.dmgPcs.editText.showSoftInputOnFocus = false
        binding.sumPcs.editText.showSoftInputOnFocus = false
        item.strEditText(binding.remark.editText)
        item.strEditText(binding.goods.editText)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        damageModel.mawb.observe(viewLifecycleOwner) {
            if(!damageModel.bSearchCon){
                return@observe
            }

            if(it.length == 0){
                damageModel.setClear()
            }else if(it.length == 11){
                when(damageModel.acceptType){
                    "N" -> damageModel.searchMawb_before()
                    "Y" -> damageModel.searchMawb_after()
                }
            }
        }

        damageModel.damageExportSid.observe(viewLifecycleOwner) {
            if(!damageModel.bSearchCon){
                return@observe
            }

            if(it.length > 0){
                damageModel.getCode()
                damageModel.searchInfo()
            }
        }


        val adapter = TreeAdapter()
        val divider = GroupDividerDecoration(2, Color.WHITE)
        binding.dmgItem.recyclerView.addItemDecoration(divider)
        binding.dmgItem.recyclerView.adapter = adapter
        binding.dmgItem.recyclerView.layoutManager = LinearLayoutManager(view.context)

        damageModel.groups.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }


        action.doneAction(binding.mawb.editText) {
            when(damageModel.acceptType){
                "N" -> damageModel.searchMawb_before()
                "Y" -> damageModel.searchMawb_after()
            }
        }

        action.textChange_after(binding.fltDate.editText) { s ->
            if (damageModel.isEditing) return@textChange_after
            damageModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)

            damageModel.isEditing = false
        }
    }


    override fun onStart() {
        super.onStart()

        binding.mawb.editText.post {
            binding.mawb.editText.requestFocus()
        }
        val route = Common.route.value

        if(route.params.size > 0){
            val temp_param = route.params

            damageModel.bSearchCon = false
            temp_param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_SID" -> damageModel.cargoControlSid = it.second
                    "MAWB" -> damageModel.mawb.value = it.second
                    "ACCEPT_TYPE" -> damageModel.acceptType = it.second
                    "DAMAGE_EXPORT_SID" -> damageModel.damageExportSid.value= it.second
                }
            }
            damageModel.bSearchCon = true

            if(damageModel.damageExportSid.value.length > 0){
                damageModel.searchInfo()
            }else if(damageModel.mawb.value.length == 11){
                when(damageModel.acceptType){
                    "N" -> damageModel.searchMawb_before()
                    "Y" -> damageModel.searchMawb_after()
                }
            }
        }
        damageModel.getGroupCode()
        for(item in route.bottomItems()){
            when(item){
                is BottomItem.Save -> {
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        damageModel.parseXml(damageModel.groups.value)
                    }
                }
                is BottomItem.Camera -> {
                    item.onClick = fun() {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        if(damageModel.damageExportSid.value.length <= 1){
                            Common.sendError("등록되지 않은 화물입니다.")
                            return
                        }

                        val params = listOf<Pair<String, String>>(
                            Pair("DAMAGE_EXPORT_SID", damageModel.damageExportSid.value),
                            Pair("ACCEPT_TYPE",damageModel.acceptType),
                            Pair("MAWB", damageModel.mawb.value)
                        )

                        route.params = params

                        val params1 = listOf<Pair<String, String>>(
                            Pair("DAMAGE_EXPORT_SID", damageModel.damageExportSid.value),
                            Pair("MAWB", damageModel.mawb.value)
                        )

                        val camera = Route.Camera
                        camera.params = params1

                        Common.addNavigate(camera)

                    }
                }
                is BottomItem.Print ->{
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        damageModel.printPdf()
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