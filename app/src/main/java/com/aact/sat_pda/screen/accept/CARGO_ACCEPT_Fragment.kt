package com.aact.sat_pda.screen.accept

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
import com.aact.sat_pda.databinding.FragmentCargoAcceptBinding
import com.aact.sat_pda.screen.BaseFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_ACCEPT_Fragment : BaseFragment() {
    private var _binding: FragmentCargoAcceptBinding? = null
    private val binding get() = _binding!!
    private val acceptModel: CARGO_ACCEPT_Model by lazy { CARGO_ACCEPT_Model() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCargoAcceptBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = acceptModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.limitEditText(binding.mawb.editText,11)
        binding.fltdate.editText.showSoftInputOnFocus = false
        item.strEditText(binding.fltno.editText)
        item.strEditText(binding.fltDest.editText)
        item.strEditText(binding.agentCode.editText)
        item.disableEditText(binding.agentName.editText)
        item.disableEditText(binding.ec.editText)
        binding.mfcsPackage.editText.showSoftInputOnFocus = false
        binding.mfcsWeight.editText.showSoftInputOnFocus = false


        item.setSpiner_Str(view.context, binding.goShow.combo, acceptModel.ynList) {
            acceptModel.goshowSelect.value = it
        }
        item.setSpiner_Str(view.context, binding.trans.combo, acceptModel.ynList) {
            acceptModel.transSelect.value = it
        }
        item.setSpiner_Str(view.context, binding.bup.combo, acceptModel.ynList) {
            acceptModel.bupSelect.value = it
        }

        acceptModel.mawb.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                acceptModel.setClear()
            }else if(it.length == 11){
                acceptModel.getMawbInfo()
            }
        }

        acceptModel.bupSelect.observe(viewLifecycleOwner) {
            val index = acceptModel.ynList.indexOf(it)
            if (index >= 0) {
                binding.bup.combo.setSelection(index)
            }

        }
        acceptModel.goshowSelect.observe(viewLifecycleOwner) {
            val index = acceptModel.ynList.indexOf(it)
            if (index >= 0) {
                binding.goShow.combo.setSelection(index)
            }

        }
        acceptModel.transSelect.observe(viewLifecycleOwner) {
            val index = acceptModel.ynList.indexOf(it)
            if (index >= 0) {
                binding.trans.combo.setSelection(index)
            }

        }

        acceptModel.fltDate.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                acceptModel.fltNo.value = ""
                acceptModel.fltSelect.value = ""
                acceptModel.fltDest.value = ""
            }
        }

        acceptModel.fltNo.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                acceptModel.fltSelect.value = ""
                acceptModel.fltDest.value = ""
            }
        }

        acceptModel.fltDest.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                acceptModel.fltDestSid = ""
            }
        }

        acceptModel.agentCode.observe(viewLifecycleOwner) {
            if (it.length == 0) {
                acceptModel.customerCode = ""
                acceptModel.agentName.value = ""
            }
        }

        acceptModel.fltList.observe(viewLifecycleOwner) {
            getCommonParse(
                subContent = acceptModel.fltModal,
                body = it,
                filterStr = acceptModel.fltNo.value,
                filterColumn = listOf("FLIGHT_NO")
            )
        }

        acceptModel.fltSelect.observe(viewLifecycleOwner) {
            if (it.length > 0) {
                lifecycleScope.launch {
                    Common.loadingOn(coroutineContext[Job])
                    val ret = acceptModel.biz.getOriginDest(it)
                    ret?.row?.let {
                        acceptModel.origin = it["ORIGIN_CODE"] ?: ""
                        acceptModel.fltDest.value = it["DESTINATION_CODE"] ?: ""
                        acceptModel.fltDestSid = it["DESTINATION_NAME"] ?: ""
                    }
                    Common.loadingOff()
                }

            }
        }

        action.doneAction(binding.mawb.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            acceptModel.getMawbInfo()
        }
        action.textChange_after(binding.fltdate.editText) { s ->
            if (acceptModel.isEditing) return@textChange_after
            acceptModel.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltdate.editText.setText(result)
            binding.fltdate.editText.setSelection(result.length)

            acceptModel.isEditing = false
        }
        action.doneAction(binding.fltno.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            val dateParam = Util.formatDate_8(acceptModel.fltDate.value)
            val mawbParam = Util.getStr(acceptModel.mawb.value)
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])
                if (dateParam.replace("-", "").length < 8) {
                    Common.sendError("일자를 확인해주세요")
                    return@launch
                }
                val ret = acceptModel.biz.getPWMFlightNoWithPreFix(dateParam, mawbParam)

                ret?.let {
                    acceptModel.fltList.value = it
                }
                Common.loadingOff()
            }
        }
        action.doneAction(binding.fltDest.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = acceptModel.biz.getCommonCode("APORT", acceptModel.fltDest.value)

                ret?.let {
                    getCommonParse(
                        subContent = acceptModel.destModal,
                        body = it,
                        filterStr = acceptModel.fltDest.value,
                        filterColumn = listOf("CODE_CODE")
                    )
                }

                Common.loadingOff()
            }
        }

        action.doneAction(binding.agentCode.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                val ret = acceptModel.biz.getAgencyCode("AGENCY", acceptModel.agentCode.value)
                if (ret != null) {
                    getCommonParse(
                        subContent = acceptModel.agencyModal,
                        body = ret,
                        filterStr = acceptModel.agentCode.value,
                        filterColumn = listOf("CUSTOMER_CODE", "AGENCY_CODE", "CUSTOMER_NAME")
                    )
                }

                Common.loadingOff()
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val param = Common.route.value.params
        if (param.size > 0) {

            param.forEach {
                when (it.first) {
                    "CARGO_CONTROL_SID" -> acceptModel.cargoControlSid = it.second
                    "MAWB" -> acceptModel.mawb.value = it.second
                    "FLIGHT_NO" -> acceptModel.fltNo.value = it.second
                    "FLIGHT_DATE" -> acceptModel.fltDate.value = it.second
                    "FLIGHT_DEST" -> acceptModel.fltDest.value = it.second
                }
            }
        }

        if (acceptModel.mawb.value.length == 0) {
            binding.mawb.editText.requestFocus()
        }

        val route = Common.route.value.bottomItems()
        for (item in route) {
            when (item) {

                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        acceptModel.setSave()
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