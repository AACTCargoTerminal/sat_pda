package com.aact.sat_pda.screen.stock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoStockBinding
import com.aact.sat_pda.screen.BaseFragment

class CARGO_STOCK_Fragment: BaseFragment() {
    private var _binding: FragmentCargoStockBinding? = null
    private val binding get() = _binding!!
    private val cargoStockModel: CARGO_STOCK_Model by lazy { CARGO_STOCK_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCargoStockBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoStockModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //2. Edit부분 변수설정
        val acceptDate = binding.acceptDate.editText
        val acceptSeq = binding.acceptSeq.editText
        val mawb = binding.mawb.editText
        val hawb = binding.hawb.editText
        val fltDate = binding.fltdate.editText
        val fltNo = binding.fltno.editText
        val pcs = binding.pcs.editText
        val weight = binding.weight.editText
        val spc = binding.spc.editText
        val position = binding.position.editText
        val rack = binding.rack.combo
        val spcFlag = binding.spcFlag.combo

        //3. Edit부분 변경 기본 숫자입력 키보드 안올라오게 설정해줘야됨 ( readOnly : itemProc.disable... , 한글가능처리 : itemProc.str... 외 키보드안올라오게 )

        item.disableEditText(acceptDate)
        item.disableEditText(acceptSeq)
        item.disableEditText(mawb)
        item.disableEditText(hawb)
        item.disableEditText(fltDate)
        item.disableEditText(fltNo)
        item.disableEditText(pcs)
        item.disableEditText(weight)
        item.disableEditText(spc)
        item.strEditText(position)

        //5. 데이터 셋팅

        cargoStockModel.position.observe(viewLifecycleOwner) {
            cargoStockModel.positionStr.value = it?.second ?: ""
        }
        cargoStockModel.rackNo.observe(viewLifecycleOwner) {
            val position = cargoStockModel.rackList.value?.indexOf(it) ?: 0
            if(position >= 0){
                rack.setSelection(position)
            }
        }
        cargoStockModel.spcFlag.observe(viewLifecycleOwner) {
            val position = cargoStockModel.spcFlagList.indexOf(it)
            if(position >= 0){
                spcFlag.setSelection(position)
            }
        }

        //6. 콤보박스, 테이블 종류 UI 구현
        cargoStockModel.positionList.observe(viewLifecycleOwner) {
            action.doneAction(position) {
                getCommonParse(
                    subContent = cargoStockModel.modalTable,
                    body = it,
                    filterStr = cargoStockModel.positionStr.value,
                    filterColumn = emptyList()
                )
            }
        }

        cargoStockModel.rackList.observe(viewLifecycleOwner) {
            item.setSpiner_Str(context = view.context, view = rack, list = it){ str ->
                cargoStockModel.rackNo.postValue(str)
            }
        }

        item.setSpiner_Str(context = view.context,view = spcFlag, list = cargoStockModel.spcFlagList) {
            cargoStockModel.spcFlag.postValue(it)
        }

    }

    override fun onStart() {
        super.onStart()

        //하단 바텀버튼 클릭지정
        val route = Common.route.value

        if(route.params.size > 0){
            val temp1 =
                route.params.find { it.first == "CARGO_ACCEPT_SID" }?.second ?: ""
            val temp2 = route.params.find { it.first == "CARGO_ACCEPT_SEQ" }?.second ?: ""
            val temp3 = route.params.find { it.first == "AUTO_FLAG" }?.second ?: ""
            cargoStockModel.acceptSid = temp1
            cargoStockModel.acceptSeq.value = temp2
            cargoStockModel.autoFlag = temp3
        }
        cargoStockModel.getInfo()

        for(item in route.bottomItems()){
            when(item){
                is BottomItem.Add ->{
                    item.onClick = {
                        if(keboardFlag){
                            keyboardCtl()
                        }
                        cargoStockModel.setCargoIn()
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