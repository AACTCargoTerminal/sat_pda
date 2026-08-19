package com.aact.sat_pda.screen.import_cargo_damage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.Adapter.RowAdapter
import com.aact.sat_pda.Adapter.TreeAdapter
import com.aact.sat_pda.Biz.GroupDividerDecoration
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.databinding.FragmentCargoDamageBinding
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.screen.BaseFragment

class IMPORT_CARGO_DAMAGE_Fragment: BaseFragment() {
    private var _binding: FragmentCargoDamageBinding? = null
    private val binding get() = _binding!!
    private val damageModel: IMPORT_CARGO_DAMAGE_Model by lazy {IMPORT_CARGO_DAMAGE_Model()}

    private var dmgListTable: View? = null
    private var dmgListAdapter: RowAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCargoDamageBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = damageModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item.strEditText(binding.cargoNo.editText)
        item.limitEditText(binding.cargoNo.editText, 20)

        item.disableEditText(binding.mawb.editText)
        item.disableEditText(binding.hawb.editText)

        item.strEditText(binding.dmgPcs.editText)
        item.strEditText(binding.remark.editText)

        damageModel.cargoNo.observe(viewLifecycleOwner) { value ->
            val cargoNo = value.orEmpty()

            if (cargoNo.length != 19) {
                damageModel.lastSearchedCargoNo = ""
                return@observe
            }

            if (cargoNo == damageModel.lastSearchedCargoNo) {
                return@observe
            }

            damageModel.lastSearchedCargoNo = cargoNo
            damageModel.setInfo()
        }

        action.doneAction(binding.cargoNo.editText) {
            if (keboardFlag) {
                keyboardCtl()
            }
            damageModel.setInfo()
        }

        val adapter = TreeAdapter(expandable = false)
        val divider = GroupDividerDecoration(2, Color.WHITE)
        binding.dmgItem.recyclerView.addItemDecoration(divider)
        binding.dmgItem.recyclerView.adapter = adapter
        binding.dmgItem.recyclerView.layoutManager = LinearLayoutManager(view.context)

        adapter.setOnItemCheckListener { itemCode, checkFlag ->
            damageModel.updateItem(itemCode, checkFlag)
        }

        adapter.setOnGroupCheckListener { groupCode ->
            damageModel.dmgItemSelect["DAMAGE_GROUP_CODE"] = groupCode
            damageModel.loadItems(groupCode)
        }

        damageModel.groups.observe(viewLifecycleOwner) { groups ->
            adapter.setData(groups)
        }

        damageModel.dmgList.observe(viewLifecycleOwner) { items ->
            damageModel.dmgSelect["DAMAGE_ITEM_CODE"] =
                if (items.isNotEmpty()) Util.getStr(items[0].row["DAMAGE_ITEM_CODE"]) else ""

            updateDmgListTable(items)
        }
    }

    private fun updateDmgListTable(items: List<DataRow>) {
        if (dmgListAdapter == null) {
            binding.dmgList.custItem.removeAllViews()
            val table = item.getTable_Click(
                context = binding.root.context,
                headerList = damageModel.dmgListHeader,
                bodyList = items,
                selectValue = damageModel.dmgSelect,
                height = 240
            ) { params ->
                damageModel.dmgSelect =
                    Util.validSelectTable(damageModel.dmgSelect, params).toMutableMap()
            }
            binding.dmgList.custItem.addView(table)
            dmgListTable = table

            dmgListAdapter = findRecyclerViewInTable(table)?.adapter as? RowAdapter
        } else {
            dmgListAdapter?.updateData(items, damageModel.dmgSelect)
        }
    }

    private fun findRecyclerViewInTable(view: View): RecyclerView? {
        if (view is RecyclerView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findRecyclerViewInTable(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }



    override fun onStart() {
        super.onStart()
        val route = Common.route.value ?: run {
            binding.cargoNo.editText.requestFocus()
            return
        }

        if (damageModel.cargoNo.value.isNullOrEmpty()) {
            if (route.params.size > 0) {
                val temp =
                    route.params.find { it.first == "CARGO_CONTROL_NO" }?.second ?: ""
                val temp2 =
                    route.params.find { it.first == "CARGO_CONTROL_SID" }?.second ?: ""
                damageModel.cargoNo.value = temp
                damageModel.cargoSid = temp2
            }
        }

        if (damageModel.cargoNo.value.isNullOrEmpty()) {
            binding.cargoNo.editText.requestFocus()
        }

        for (item in route.bottomItems()) {
            when(item) {
                is BottomItem.Save -> {
                    item.onClick = {
                        if (keboardFlag) {
                            keyboardCtl()
                        }
                        damageModel.saveClick()
                    }
                }

                is BottomItem.Camera -> {
                    item.onClick = fun() {
                        if (keboardFlag) {
                            keyboardCtl()
                        }

                        if (damageModel.cargoSid.isEmpty()) {
                            Common.sendError("화물번호가 없습니다.")
                            return
                        }

                        route.params = listOf(
                            Pair("CARGO_CONTROL_NO", damageModel.cargoNo.value.orEmpty()),
                            Pair("CARGO_CONTROL_SID", damageModel.cargoSid)
                        )

                        val params = listOf(
                            Pair("CARGO_CONTROL_SID", damageModel.cargoSid),
                            Pair("MAWB", damageModel.mawb.value)
                        )
                        val camera = Route.Camera
                        camera.params = params
                        Common.addNavigate(camera)
                    }
                }

                else -> null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dmgListTable = null
        dmgListAdapter = null
        _binding = null
    }

}