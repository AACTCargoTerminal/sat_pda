package com.aact.sat_pda.Adapter

import android.graphics.Color
import android.graphics.Rect
import android.media.AudioManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.appconfig.BottomItem
import com.aact.sat_pda.R
import com.aact.sat_pda.appconfig.Common

class BottomAdapter(private val array: List<BottomItem>) :
    RecyclerView.Adapter<BottomAdapter.RowViewHolder>() {

    inner class RowViewHolder(val rowLayout: View) : RecyclerView.ViewHolder(rowLayout)

    override fun getItemCount(): Int = array.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_tab_item, parent, false)
        val params = RecyclerView.LayoutParams(
            if(array.size >5 ) 96 else parent.measuredWidth / array.size,  // 균등 너비
            RecyclerView.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = params
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val rowData = array[position]
        val tabIcon = holder.itemView.findViewById<ImageView>(R.id.tabIcon)
        val tabText = holder.itemView.findViewById<TextView>(R.id.tabText)
        val container = holder.itemView.findViewById<LinearLayout>(R.id.tabLayout)

        tabText.text = rowData.name
        if (rowData.icon != 0) {
            tabIcon.apply {
                setImageResource(rowData.icon)
                visibility = View.VISIBLE       // 재활용 대비: 다시 보이게
                alpha = 1f
            }
        } else {
            tabIcon.apply {
                setImageDrawable(null)          // 이미지 제거
                visibility = View.GONE     // 👈 공간은 유지, 보이진 않음
                alpha = 0f                      // (선택) 혹시 INVISIBLE 대신 쓰고 싶으면
            }
        }

        val start: () -> Unit = {
            tabText.setTextColor(Color.parseColor("#000000"))
            tabIcon.setColorFilter(Color.parseColor("#000000"))
            container.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        val end: () -> Unit = {
            tabText.setTextColor(Color.parseColor("#FFFFFF"))
            tabIcon.setColorFilter(Color.parseColor("#FFFFFF"))
            container.setBackgroundColor(Color.parseColor("#1F1F2B"))
        }
        var flag = false
        container.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    flag = true
                    start()
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val rect = Rect()
                    v.getGlobalVisibleRect(rect)
                    val x = event.rawX.toInt()
                    val y = event.rawY.toInt()
                    if (!rect.contains(x, y)) {
                        flag = false
                        end()
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if(flag){
                        end()
                        container.playSoundEffect(SoundEffectConstants.CLICK)
                        rowData.onClick()
                    }
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    flag = false
                    end()
                    true
                }

                else -> false
            }
        }
    }


}