package com.aact.sat_pda.screen.doubt_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentCargoDoubtListBinding
import com.aact.sat_pda.databinding.FragmentStockListDateBinding
import com.aact.sat_pda.screen.BaseFragment
import com.aact.sat_pda.screen.cargo_stock_list_date.CARGO_STOCK_LIST_DATE_Model
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CARGO_DOUBT_LIST_Fragment : BaseFragment() {

    private var _binding: FragmentCargoDoubtListBinding? = null
    private val binding get() = _binding!!
    private val model: CARGO_DOUBT_LIST_Model by lazy { CARGO_DOUBT_LIST_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCargoDoubtListBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item.limitEditText(binding.mawb.editText,11)
        item.strEditText(binding.custCode.editText)

        model.doubtList.observe(viewLifecycleOwner) {
            var select = mapOf(
                "CARGO_CONTROL_SID" to "",
                "CARGO_ACCEPT_SEQ" to ""
            )
            if(it.size> 0){
                select  = mapOf(
                    "CARGO_CONTROL_SID" to Util.getStr(it[0].row["CARGO_CONTROL_SID"]),
                    "CARGO_ACCEPT_SEQ" to Util.getStr(it[0].row["CARGO_ACCEPT_SEQ"])
                )
                model.doubtClick(select)
            }else{
                binding.mawb.editText.requestFocus()
            }
            binding.doubtList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = model.doubtHeader,
                bodyList = it,
                selectValue = select,
                height = 260,
            ) {
                model.doubtSelect.value =
                    Util.validSelectTable(model.doubtSelect.value, it)
            }
            binding.doubtList.custItem.addView(table)
        }

        model.doubtSelect.observe(viewLifecycleOwner) {
            if(!it?.values.isNullOrEmpty()){
                model.doubtClick(it)
            }
        }

        action.doneAction(binding.mawb.editText) {
            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                model.getInfo()

                Common.loadingOff()
            }

        }

        action.doneAction(binding.custCode.editText) {
            getCommonParse(
                subContent = model.custTable,
                body = model.custList,
                filterStr = model.custCode.value,
                filterColumn = listOf("VALUE1_CHAR","CODE_NAME")
            )
        }

    }

    override fun onStart() {
        super.onStart()

        model.setLoad()

        val route = Common.route.value

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Search -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        lifecycleScope.launch {
                            Common.loadingOn(coroutineContext[Job])

                            model.getInfo()

                            Common.loadingOff()
                        }

                    }
                }

                is BottomItem.Tmp01 ->{
                    item.onClick={
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        model.noticeClick()
                    }
                }
                is BottomItem.Tmp02 ->{
                    item.onClick={
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        model.releaseClick()
                    }
                }

                else -> null
            }
        }
    }

}