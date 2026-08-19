package com.aact.sat_pda.screen.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoLocationBinding
import com.aact.sat_pda.screen.BaseFragment

class CARGO_LOCATION_Fragment : BaseFragment() {
    private var _binding: FragmentCargoLocationBinding? = null
    private val binding get() = _binding!!
    private val cargoLocationModel: CARGO_LOCATION_Model by lazy { CARGO_LOCATION_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoLocationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoLocationModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mawb = binding.mawb.editText
        val hawb = binding.hawb.editText
        val fltDate = binding.fltdate.editText
        val fltNo = binding.fltno.editText
        val pcs = binding.pcs.editText
        val position = binding.position.editText
        val rackNo = binding.rack.combo

        item.disableEditText(mawb)
        item.disableEditText(hawb)
        item.disableEditText(fltDate)
        item.disableEditText(fltNo)
        pcs.showSoftInputOnFocus = false
        item.strEditText(position)

        cargoLocationModel.positionList.observe(viewLifecycleOwner) {
            action.doneAction(position) {
                getCommonParse(
                    subContent = cargoLocationModel.modalTable,
                    body = it,
                    filterStr = position.text.toString(),
                    filterColumn = emptyList()
                )
            }
        }

        cargoLocationModel.position.observe(viewLifecycleOwner) {
            cargoLocationModel.positionStr.value = it?.second ?: ""
        }
        cargoLocationModel.rackNo.observe(viewLifecycleOwner) {
            val position = cargoLocationModel.rackList.value?.indexOf(it) ?: 0
            if (position >= 0) {
                rackNo.setSelection(position)
            }
        }

        cargoLocationModel.locationList.observe(viewLifecycleOwner) {
            binding.locationTable.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = cargoLocationModel.locationHeader,
                bodyList = it,
                selectValue = cargoLocationModel.selectLocation,
                height = 400,
            ) {
                cargoLocationModel.selectLocation =
                    Util.validSelectTable(cargoLocationModel.selectLocation, it)
                it["NO_OF_PACKAGE"]?.let { pcs ->
                    cargoLocationModel.pcs.value = pcs
                }
            }
            binding.locationTable.custItem.addView(table)
        }

        cargoLocationModel.rackList.observe(viewLifecycleOwner) {
            val adapter =
                ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            rackNo.setAdapter(adapter)
        }

        rackNo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                cargoLocationModel.rackNo.value = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val pcs = binding.pcs.editText

        val route = Common.route.value

        if (route.params.size > 0) {
            val temp =
                route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
            cargoLocationModel.cargoControlSid = temp
        }

        cargoLocationModel.startProc()

        for (item in route.bottomItems()) {
            when (item) {

                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoLocationModel.saveProc(Util.getStr(pcs.text.toString())) {
                            if (it == "Y") {
                                cargoLocationModel.startProc()
                            }
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