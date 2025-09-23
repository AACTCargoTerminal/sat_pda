package com.aact.sat_pda.screen.menu

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import com.aact.sat_pda.databinding.FragmentMenuBinding
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.screen.BaseFragment
import com.aact.sat_pda.R
import com.aact.sat_pda.appconfig.Common

class MenuFragment : BaseFragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val menuModel: MenuModel by lazy { MenuModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuModel.getMainItem()
        menuModel.mainItem.observe(viewLifecycleOwner) { array->
            array?.forEach { item->
                createMainItem(item)
            }
        }

    }

    fun createMainItem(item: DataRow) {

        val item_button = LayoutInflater.from(context).inflate(R.layout.cust_main_menu,binding.menuMain,false)
        val container = item_button.findViewById<LinearLayout>(R.id.main_button_layout)
        val subContainer =  item_button.findViewById<ScrollView>(R.id.subMenuContainer)
        val subLinear = item_button.findViewById<LinearLayout>(R.id.subMenuView)
        val code = item.row["CODE_CODE"] ?: ""


        val filterSubData = Common.menuList.filter { it.group == code }

        filterSubData.forEach {
            if(it.execute == "Y"){
                val subView = LayoutInflater.from(subContainer.context).inflate(R.layout.cust_sub_menu,subContainer,false)
                val sub = subView.findViewById<LinearLayout>(R.id.main_button_layout)
                sub.setOnClickListener { click ->
                    Common.route.value = it
                }
                val text = subView.findViewById<TextView>(R.id.menu_text)
                text.setText(it.name)
                subLinear.addView(subView)
            }

        }

        container.setOnClickListener {

            val select = menuModel.selectMain.value

            if(select == code){
                menuModel.selectMain.value = ""
            }else{
                menuModel.selectMain.value = code
            }

        }

        val text = item_button.findViewById<TextView>(R.id.menu_text)
        text.setText(item.row["CODE_NAME"] ?: "")
        val endIcon = item_button.findViewById<ImageView>(R.id.end_icon)

        menuModel.selectMain.observe(viewLifecycleOwner) { main ->
            if(code==main){
                endIcon.setImageResource(R.drawable.keyboard_arrow_up)
                slide_height_down(subContainer,200)
            }else{
                endIcon.setImageResource(R.drawable.keyboard_arrow_down)
                slide_height_up(subContainer,200)
            }
        }



        binding.menuMain.addView(item_button)

    }

    fun slide_height_down(view: View, duration: Long) {

        val wasGone = view.visibility == View.GONE
        if (wasGone) view.visibility = View.INVISIBLE

        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = view.measuredHeight

        if (wasGone) view.visibility = View.GONE

        if (targetHeight == 0) return

        // 초기 높이를 0으로 설정
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.duration = duration
        animator.start()
    }

    fun slide_height_up(view: View, duration: Long) {
        val initialHeight = view.height
        if (initialHeight == 0) return // 이미 접힌 상태면 애니메이션 생략

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            view.layoutParams.height = animation.animatedValue as Int
            view.requestLayout()
        }
        animator.duration = duration
        animator.start()

        animator.doOnEnd {
            view.visibility = View.GONE
        }
    }
}