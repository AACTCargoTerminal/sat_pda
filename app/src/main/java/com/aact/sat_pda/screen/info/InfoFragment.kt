package com.aact.sat_pda.screen.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aact.sat_pda.MainActivity
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.databinding.FragmentInfoBinding
import com.aact.sat_pda.screen.BaseFragment
import com.aact.sat_pda.R
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent

class InfoFragment : BaseFragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private val infoModel: InfoModel by lazy { InfoModel() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDefault()

        Common.selectServer.observe(viewLifecycleOwner) { value ->
            binding.btn.text = value
        }
        binding.serverBtnGroup.setOnClickListener {

            binding.endIcon.setImageResource(R.drawable.keyboard_arrow_up)

            (activity as? MainActivity)?.openModal(
                SubContent.GetTable(
                    headerList = infoModel.headerList,
                    bodylist = Common.serverIp,
                    selectValue = mapOf("ip" to Common.selectServer.value),
                    endClick = {
                        binding.endIcon.setImageResource(
                            R.drawable.keyboard_arrow_down
                        )
                    },
                    onClick = {
                        Common.selectServer.value = it["ip"]
                        binding.endIcon.setImageResource(
                            R.drawable.keyboard_arrow_down
                        )
                        (activity as? MainActivity)?.openModal(SubContent.ContentNone)
                    }
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setDefault(){
        val parent = binding.infoGrid

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is ViewGroup) {
                for (j in 0 until child.childCount) {
                    val include = child.getChildAt(0)
                    if (include is ViewGroup) {
                        if (include.id != View.NO_ID) {
                            val idName = resources.getResourceEntryName(include.id)
                            val infoItem = infoModel.infoList[idName]
                            if (infoItem != null) {
                                val keyView = include.getChildAt(0)
                                val valueView = include.getChildAt(1)
                                if (keyView is TextView) {
                                    keyView.text = infoItem.name
                                }
                                if (valueView is TextView) {
                                    infoItem.data.observe(viewLifecycleOwner) { itemValue ->
                                        when (itemValue) {
                                            is String -> {
                                                valueView.text = itemValue
                                            }

                                            is Boolean -> {
                                                valueView.text = if (itemValue) "완료" else "권한요청"
                                            }

                                            is Long -> {
                                                valueView.text = itemValue.toString() + "초"
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }
    }
}