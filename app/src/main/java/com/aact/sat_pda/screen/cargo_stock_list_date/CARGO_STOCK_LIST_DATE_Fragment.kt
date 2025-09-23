package com.aact.sat_pda.screen.cargo_stock_list_date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoScaleBinding
import com.aact.sat_pda.databinding.FragmentStockListDateBinding
import com.aact.sat_pda.screen.BaseFragment
import com.aact.sat_pda.screen.scale.CARGO_SCALE_Model
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.text.isNullOrEmpty

class CARGO_STOCK_LIST_DATE_Fragment : BaseFragment() {
    private var _binding: FragmentStockListDateBinding? = null
    private val binding get() = _binding!!
    private val model: CARGO_STOCK_LIST_DATE_Model by lazy { CARGO_STOCK_LIST_DATE_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockListDateBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.limitEditText(binding.fltDate.editText, 10)

        action.textChange_after(binding.fltDate.editText) { s ->
            if (model.isEditing) return@textChange_after
            model.isEditing = true

            val str = s?.toString() ?: ""
            val result = Util.validDate(str)
            binding.fltDate.editText.setText(result)
            binding.fltDate.editText.setSelection(result.length)



            model.isEditing = false
        }
    }


    override fun onStart() {
        super.onStart()

        val route = Common.route.value

        lifecycleScope.launch {
            Common.loadingOn(coroutineContext[Job])
            getList1()
            getList2("")
            Common.loadingOff()
        }


        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Search -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        lifecycleScope.launch {
                            Common.loadingOn(coroutineContext[Job])
                            getList1()
                            val idx = model.listDateSelect["SCHEDULE_SID"] ?: ""
                            getList2(idx)
                            Common.loadingOff()
                        }

                    }
                }

                else -> null
            }
        }
    }

    private suspend fun getList1() {

        val ret = model.setListDate()

        if (ret != null) {
            binding.dateList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = model.listDateHeader,
                bodyList = ret,
                selectValue = model.listDateSelect,
                height = 260,
            ) {
                model.listDateSelect =
                    Util.validSelectTable(model.listDateSelect, it)
                lifecycleScope.launch {
                    Common.loadingOn(coroutineContext[Job])

                    val idx = model.listDateSelect["SCHEDULE_SID"]

                    if(idx.isNullOrEmpty()){
                        Common.loadingOff()
                        return@launch
                    }

                    getList2(idx)
                    Common.loadingOff()
                }
            }
            binding.dateList.custItem.addView(table)
        }
    }

    private suspend fun getList2(sid:String) {

        val ret = model.setListSch(sid)

        if (ret != null) {
            binding.schList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = model.listSchHeader,
                bodyList = ret,
                selectValue = mapOf("MASTER_AIR_WAY_BILL_NO" to ""),
                height = 260,
            ) {
            }
            binding.schList.custItem.addView(table)
        }

    }

}