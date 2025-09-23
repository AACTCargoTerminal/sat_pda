package com.aact.sat_pda.screen.scale_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoScaleListBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.text.get

class CARGO_SCALE_LIST_Fragment : BaseFragment() {
    private var _binding: FragmentCargoScaleListBinding? = null
    private val binding get() = _binding!!
    private val cargoScaleListModel: CARGO_SCALE_LIST_Model by lazy { CARGO_SCALE_LIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoScaleListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoScaleListModel

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.acceptSeq.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.fltNo.editText)
        item.disableEditText(binding.pcsSum.editText)
        item.disableEditText(binding.wtSum.editText)
        binding.pcs.editText.showSoftInputOnFocus = false
        binding.wt.editText.showSoftInputOnFocus = false
        binding.palletWt.editText.showSoftInputOnFocus = false
        binding.palletPcs.editText.showSoftInputOnFocus = false
        binding.netWt.editText.showSoftInputOnFocus = false
        item.strEditText(binding.spc.editText)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val retPallet = async { cargoScaleListModel.biz.getCommonCode("PLTCD", "") }
            retPallet.await().let {
                cargoScaleListModel.palletList.value = it
            }
            val retSpc = async { cargoScaleListModel.biz.getCommonCode("SPCGO", "") }
            retSpc.await().let {
                cargoScaleListModel.spcList.value = it
            }

            cargoScaleListModel.setList()
            Common.loadingOff()
        }


        cargoScaleListModel.palletPcs.observe(viewLifecycleOwner) {
            cargoScaleListModel.setPalletCumpute()
            cargoScaleListModel.setNetWTCompute()
        }

        cargoScaleListModel.palletWt.observe(viewLifecycleOwner) {
            cargoScaleListModel.setNetWTCompute()
        }

        cargoScaleListModel.wt.observe(viewLifecycleOwner) {
            cargoScaleListModel.setNetWTCompute()
        }

        cargoScaleListModel.scaleList.observe(viewLifecycleOwner) {

            var pcs = 0
            var wt = 0.0

            it?.forEach {
                pcs += Util.getInt(it.row["NO_OF_PACKAGE"] ?: "0")
                wt += Util.getDouble(it.row["NET_WEIGHT"] ?: "0.0")
            }

            cargoScaleListModel.pcsSum.value = pcs.toString()
            cargoScaleListModel.wtSum.value = wt.toString()

            binding.scaleList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = cargoScaleListModel.scaleHeader,
                bodyList = it,
                selectValue = cargoScaleListModel.scaleSelect.value,
                height = 240,
            ) {
                cargoScaleListModel.scaleSelect.value =
                    Util.validSelectTable(cargoScaleListModel.scaleSelect.value, it)
            }
            binding.scaleList.custItem.addView(table)
        }

        cargoScaleListModel.scaleSelect.observe(viewLifecycleOwner) {
            val seq = it["SCALE_SEQ"]

            seq?.let {
                if (it.isNotEmpty()) {
                    cargoScaleListModel.getScaleSelect(it)
                }

            }

        }

        cargoScaleListModel.palletList.observe(viewLifecycleOwner) {
            val data = Util.getOneColumn(it, "CODE_NAME")
            val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                data
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            binding.pallet.combo.setAdapter(adapter)
            if (it?.size ?: 0 > cargoScaleListModel.palletSelect.value) {
                binding.pallet.combo.setSelection(cargoScaleListModel.palletSelect.value)
            }
        }

        binding.pallet.combo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                cargoScaleListModel.palletSelect.value = position
                cargoScaleListModel.setPalletCumpute()
                cargoScaleListModel.setNetWTCompute()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        cargoScaleListModel.palletSelect.observe(viewLifecycleOwner) {
            if (cargoScaleListModel.palletList.value?.size ?: 0 > it) {
                binding.pallet.combo.setSelection(it)
            }
        }

        cargoScaleListModel.spcList.observe(viewLifecycleOwner) {
            action.doneAction(binding.spc.editText) {
                getCommonParse(
                    subContent = cargoScaleListModel.spcModal,
                    body = it,
                    filterStr = cargoScaleListModel.spcStr.value,
                    filterColumn = listOf("CODE_CODE", "CODE_NAME")
                )
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
                    "CARGO_CONTROL_SID" -> cargoScaleListModel.cargoControlSid = it.second
                    "CARGO_ACCEPT_SID" -> cargoScaleListModel.acceptSid = it.second
                    "MAWB" -> cargoScaleListModel.mawb.value = it.second
                    "FLIGHT_NO" -> cargoScaleListModel.fltNo.value = it.second
                    "FLIGHT_DATE" -> cargoScaleListModel.fltDate.value = it.second
                }
            }
        }

        for(item in route.bottomItems()){
            when(item){
                is BottomItem.Save -> {
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        cargoScaleListModel.setUpdate()
                    }
                }
                is BottomItem.Delete ->{
                    item.onClick =  fun(){
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        val idx = cargoScaleListModel.scaleSelect.value["SCALE_SEQ"]
                        if (idx.isNullOrEmpty()) {
                            Common.sendError("선택한 항목이 없습니다.")
                            return
                        }

                        showYesNo(
                            "삭제",
                            "수량 : " + cargoScaleListModel.pcs.value
                                    + " Net W/T : " + cargoScaleListModel.wt.value
                                    + "를 삭제 합니까?",
                            onYes = {
                                cargoScaleListModel.setDelete(idx)
                            },
                            onNo = {
                                return@showYesNo
                            }
                        )
                    }
                }
                is BottomItem.Print -> {
                    item.onClick = fun(){
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        cargoScaleListModel.printClick()
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