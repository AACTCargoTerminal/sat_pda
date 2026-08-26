package com.aact.sat_pda.screen.volume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoVolumeBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlin.text.isNullOrEmpty

class CARGO_VOLUME_Fragment: BaseFragment() {

    private var _binding: FragmentCargoVolumeBinding? = null
    private val binding get() = _binding!!
    private val volumeModel: CARGO_VOLUME_Model by lazy { CARGO_VOLUME_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoVolumeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = volumeModel
        item.limitEditText(binding.mawb.editText,11)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        binding.w.editText.showSoftInputOnFocus = false
        binding.h.editText.showSoftInputOnFocus = false
        binding.d.editText.showSoftInputOnFocus = false
        binding.pcs.editText.showSoftInputOnFocus = false
        binding.volume.editText.showSoftInputOnFocus = false
        binding.volumeWt.editText.showSoftInputOnFocus = false
        item.disableEditText(binding.pcsSum.editText)
        item.disableEditText(binding.wtSum.editText)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        volumeModel.mawb.observe(viewLifecycleOwner) {
            if(it.length == 11){
                volumeModel.getAcceptList()
            }
        }

        volumeModel.pcs.observe(viewLifecycleOwner) {
            if(volumeModel.isEditing){
                return@observe
            }
            volumeModel.setCompute()
        }
        volumeModel.w.observe(viewLifecycleOwner) {
            if(volumeModel.isEditing){
                return@observe
            }
            volumeModel.setCompute()
        }
        volumeModel.h.observe(viewLifecycleOwner) {
            if(volumeModel.isEditing){
                return@observe
            }
            volumeModel.setCompute()
        }
        volumeModel.d.observe(viewLifecycleOwner) {
            if(volumeModel.isEditing){
                return@observe
            }
            volumeModel.setCompute()
        }



        volumeModel.acceptList.observe(viewLifecycleOwner) {
            val tempList = it.map { row ->
                val seq = row.row["CARGO_ACCEPT_SEQ"] ?: ""
                val time = row.row["ACCEPTED_TIME"] ?: ""
                "$seq : $time"
            }
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                tempList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.acceptList.combo.setAdapter(adapter)


        }

        binding.acceptList.combo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    volumeModel.acceptSelect.value = position

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        volumeModel.acceptSelect.observe(viewLifecycleOwner) {
            if (it > -1&&!volumeModel.acceptList.value.isNullOrEmpty()) {

                binding.acceptList.combo.setSelection(it)


                val acceptList = volumeModel.acceptList.value!!
                val seq = acceptList[it].row["CARGO_ACCEPT_SEQ"] ?: ""
                val sid = acceptList[it].row["CARGO_ACCEPT_SID"] ?: ""
                val cargoSid = acceptList[it].row["CARGO_CONTROL_SID"] ?: ""

                if (sid.isNotEmpty()) {
                    volumeModel.acceptSid = sid
                    volumeModel.cargoControlSid = cargoSid

                    volumeModel.setList()
                }

                binding.w.editText.apply {
                    requestFocus()
                    setSelection(text?.length ?: 0)
                }
            }
        }

        volumeModel.volumeSelect.observe(viewLifecycleOwner) {
            volumeModel.volumeListClick(it)
        }

        volumeModel.volumeList.observe(viewLifecycleOwner) {
            var pcs = 0
            var wt = 0.0
            it?.forEach {
                pcs += Util.getInt(it.row["NO_OF_PACKAGE"] ?: "0")
                wt += Util.getDouble(it.row["VOLUME_WEIGHT"] ?: "0.0")
            }
            volumeModel.pcsSum.value = pcs.toString()
            volumeModel.wtSum.value = String.format("%.3f", wt)

            binding.volumeList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = volumeModel.volumeHeader,
                bodyList = it,
                selectValue = volumeModel.volumeSelect.value,
                height = 320,
            ) {
                volumeModel.volumeSelect.value =
                    Util.validSelectTable(volumeModel.volumeSelect.value, it)
            }
            binding.volumeList.custItem.addView(table)
        }

        action.doneAction(binding.mawb.editText) {
            volumeModel.getAcceptList()
        }
        action.doneAction(binding.w.editText) {
            binding.h.editText.requestFocus()
        }

        action.doneAction(binding.h.editText) {
            binding.d.editText.requestFocus()
        }

        action.doneAction(binding.d.editText) {
            binding.pcs.editText.requestFocus()
        }
        action.doneAction(binding.pcs.editText) {
            if(keboardFlag){
                keyboardCtl()
            }
        }


    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        if(route.params.size > 0){
            val temp_param = route.params

            temp_param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_SID" -> volumeModel.cargoControlSid = it.second
                    "CARGO_ACCEPT_SID" -> volumeModel.acceptSid = it.second
                    "MAWB" -> volumeModel.mawb.value = it.second
                    "FLIGHT_NO" -> volumeModel.fltNo.value = it.second
                    "FLIGHT_DATE" -> volumeModel.fltDate.value = it.second
                }
            }
        }

        if(volumeModel.cargoControlSid.length == 0){
            binding.mawb.editText.requestFocus()
        }

        for(item in route.bottomItems()){
            when(item){
                is BottomItem.KeyBoard -> {
                    item.onClick = {
                        keyboardCtl()
                    }
                }
                is BottomItem.Save -> {
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        volumeModel.setSave{ bool->
                            if(bool){
                                Common.suc.value = "Volume 저장완료"
                                volumeModel.volumeSelect.value = mapOf(
                                    "VOLUME_SEQ" to "",
                                    "NO_OF_PACKAGE" to "",
                                    "DIMENSION_WIDTH" to "",
                                    "DIMENSION_LENGTH" to "",
                                    "DIMENSION_HEIGHT" to "",
                                    "VOLUME_WEIGHT" to "",
                                    "VOLUME" to "",
                                )
                                volumeModel.setInputClear()
                                binding.w.editText.apply {
                                    requestFocus()
                                    setSelection(text?.length ?: 0)
                                }
                            }
                        }
                    }
                }
                is BottomItem.Delete -> {
                    item.onClick = fun(){
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        val volumeSelect = volumeModel.volumeSelect.value["VOLUME_SEQ"]

                        if(volumeModel.acceptSid.length == 0 ||  volumeSelect.isNullOrEmpty()){
                            Common.sendError("목록을 선택하여주세요.")
                            return
                        }

                        showYesNo( "확인", "선택한 부피목록을 삭제합니까?", {
                            volumeModel.setDelete(volumeSelect)
                        }, { return@showYesNo })
                    }
                }
                is BottomItem.Print ->{
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        volumeModel.printClick()
                    }
                }
                is BottomItem.Add -> {
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        volumeModel.volumeSelect.value = mapOf(
                            "VOLUME_SEQ" to "",
                            "NO_OF_PACKAGE" to "",
                            "DIMENSION_WIDTH" to "",
                            "DIMENSION_LENGTH" to "",
                            "DIMENSION_HEIGHT" to "",
                            "VOLUME_WEIGHT" to "",
                            "VOLUME" to "",
                        )
                        volumeModel.setInputClear()
                        binding.w.editText.requestFocus()
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