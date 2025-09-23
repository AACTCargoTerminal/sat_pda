package com.aact.sat_pda.screen.stock_list_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentStockListAllBinding
import com.aact.sat_pda.databinding.FragmentStockListDateBinding
import com.aact.sat_pda.screen.BaseFragment
import com.aact.sat_pda.screen.cargo_stock_list_date.CARGO_STOCK_LIST_DATE_Model
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.text.isNullOrEmpty

class STOCK_LIST_ALL_Fragment : BaseFragment() {

    private var _binding: FragmentStockListAllBinding? = null
    private val binding get() = _binding!!
    private val model: STOCK_LIST_ALL_Model by lazy { STOCK_LIST_ALL_Model() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockListAllBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        start()

        val route = Common.route.value

        for (item in route.bottomItems()) {
            when (item) {
                is BottomItem.Search -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        start()
                    }
                }

                else -> null
            }
        }
    }


    private fun start(){
        model.getInfo{
            binding.stockList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = model.stockHeader,
                bodyList = it,
                selectValue = mapOf("MASTER_AIR_WAY_BILL_NO" to ""),
                height = 400,
            ) {
            }
            binding.stockList.custItem.addView(table)
        }
    }
}