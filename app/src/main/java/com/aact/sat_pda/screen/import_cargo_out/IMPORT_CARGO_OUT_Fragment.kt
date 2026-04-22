package com.aact.sat_pda.screen.import_cargo_out

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aact.sat_pda.Adapter.LocationOutAdapter
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentImportCargoOutBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.launch

class IMPORT_CARGO_OUT_Fragment: BaseFragment() {
    private var _binding: FragmentImportCargoOutBinding? = null
    private val binding get() = _binding!!
    private val cargoOutModel: IMPORT_CARGO_OUT_Model by lazy {IMPORT_CARGO_OUT_Model()}
    private var locationAdapter: LocationOutAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImportCargoOutBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = cargoOutModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // EditText 설정
        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.cargoNo.editText, 20)

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.fltDate.editText)
        item.disableEditText(binding.status.editText)
        item.disableEditText(binding.fltNo.editText)

        // 콤보박스 초기화
        cargoOutModel.getInOutKindList()

        //콤보박스 observe
        cargoOutModel.inOutkindNameList.observe(viewLifecycleOwner) { names ->
            if (names.isNotEmpty()) {
                item.setSpiner_Str(
                    context = view.context,
                    view = binding.inOutKindList.combo,
                    list = names
                ) { selectedName ->
                    //선택된 이름으로 index와 code 찾기
                    val index = names.indexOf(selectedName)
                    cargoOutModel.inOutKindIndex.value = index
                    if (index >= 0) {
                        cargoOutModel.inOutKindCode.value =
                            cargoOutModel.inOutKindList[index].row["CODE_CODE"] ?: ""
                    }
                }
            }
        }

        cargoOutModel.cargoNo.observe(viewLifecycleOwner){
            if (it.length == 20) {
                cargoOutModel.setInfoByNo()
            }
        }

        action.doneAction(binding.cargoNo.editText){
            if(keboardFlag) {
                keyboardCtl()
            }

            val cargoNo = cargoOutModel.cargoNo.value ?: ""
            if (cargoNo.isNotEmpty()) {
                cargoOutModel.setInfoByNo()
            }
        }

        val adapter = LocationOutAdapter(cargoOutModel)
        locationAdapter = adapter

        val layoutMgr = LinearLayoutManager(view.context)
        binding.locationList.recyclerView.layoutManager = layoutMgr
        binding.locationList.recyclerView.adapter = adapter

        cargoOutModel.locationList.observe(viewLifecycleOwner) { locations ->
            adapter.updateData(locations)
        }

    }

    override fun onStart() {
        super.onStart()

        val route = Common.route.value ?: run {
            binding.cargoNo.editText.requestFocus()
            return
        }

        val param = route.params
        if (param.size > 0) {
            param.forEach{
                when(it.first) {
                    "CARGO_CONTROL_SID" -> cargoOutModel.cargoSid = it.second
                    "CARGO_CONTROL_NO" -> cargoOutModel.cargoNo.value = it.second
                }
            }
        }

        if (cargoOutModel.cargoSid.isNotEmpty()) {
            cargoOutModel.setInfoByNo()
        } else if (cargoOutModel.cargoNo.value.isNotEmpty()) {
            binding.cargoNo.editText.requestFocus()
        }

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        cargoOutModel.saveClick()
                    }
                }

                else -> null
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationAdapter = null
        _binding = null
    }

}