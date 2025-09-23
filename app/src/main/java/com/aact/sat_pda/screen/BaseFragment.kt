package com.aact.sat_pda.screen

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.ApplicationInfo
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.aact.sat_pda.Biz.Action
import com.aact.sat_pda.Biz.Item
import com.aact.sat_pda.MainActivity
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.dto.DataRow
import kotlin.system.exitProcess
import com.aact.sat_pda.R

open class BaseFragment : Fragment() {

    private var _item: Item? = null
    protected val item get() = _item!!
    private var _action: Action? = null
    protected val action get() = _action!!
    protected var keboardFlag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _item = Item()
        _action = Action()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _item = null
        _action = null
    }

    override fun onStart() {
        super.onStart()
        val items = Common.route.value.bottomItems()

        items.forEach {
            when (it) {
                is BottomItem.Login -> {
                    it.onClick = {

                        Common.route.value = Route.Login
                    }
                }

                is BottomItem.Info -> {
                    it.onClick = {
                        Common.route.value = Route.Info
                    }
                }

                is BottomItem.Exit -> {
                    it.onClick = {
                        (context as? Activity)?.finishAffinity()
                        exitProcess(0)
                    }
                }

                is BottomItem.KeyBoard -> {
                    it.onClick = {
                        keyboardCtl()
                    }
                }

                else -> null
            }
        }
    }


    fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    fun showYesNo(title: String, msg: String, onYes: () -> Unit = {}, onNo: () -> Unit = {}) {
        (requireActivity() as? MainActivity)?.showYesNo(title, msg, onYes, onNo)
    }

    fun showYesNoCancel(title: String, msg: String, onYes: () -> Unit = {}, onNo: () -> Unit = {},onCancel:()-> Unit={}){
        (requireActivity() as? MainActivity)?.showYesNoCancel(title, msg, onYes, onNo,onCancel)
    }

    fun getPermission(): Boolean {

        var ret = (requireActivity() as? MainActivity)?.getPermission() ?: false

        return ret
    }

    fun savePref(key: String, value: String) {
        val prefs = requireContext().getSharedPreferences("SAT_PDA", Context.MODE_PRIVATE)

        prefs.edit().putString(key, value).commit()
    }

    fun getPref(key: String): String? {
        val prefs = requireContext().getSharedPreferences("SAT_PDA", Context.MODE_PRIVATE)

        val str = prefs.getString(key, null)
        return str
    }

    fun findScrollViewInView(view: View): ScrollView? {
        if (view is ScrollView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val result = findScrollViewInView(view.getChildAt(i))
                if (result != null) return result
            }
        }
        return null
    }

    fun getCommonParse(
        subContent: SubContent.GetTable,
        body: List<DataRow>,
        filterStr: String,
        filterColumn: List<String>
    ) {
        if (body.size > 0) {
            var filterData: List<DataRow>
            if (filterColumn.isEmpty()) {
                filterData = body
            } else {
                filterData = body.filter { row ->
                    filterColumn.any { key ->
                        row.row[key]?.contains(filterStr, ignoreCase = true) == true
                    }
                }
                if (filterData.size == 0) {
                    filterData = body
                }
            }

            when (filterData.size) {
                1 -> {
                    subContent.onClick(filterData[0].row)
                }

                else -> {
                    subContent.bodylist = filterData
                    (requireActivity() as? MainActivity)?.openModal(subContent)
                }
            }
        }
    }

    fun keyboardCtl() {
        val view = requireActivity().currentFocus
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (view is EditText) {

            if (keboardFlag) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)

            } else {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }else{
            if (keboardFlag) {
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
        }
        keboardFlag = !keboardFlag
    }

    fun debugControl() : Boolean{
        if(ApplicationInfo.FLAG_DEBUGGABLE and requireActivity().applicationInfo.flags == 0) {
            return true
        }else{
            return false
        }
    }

}