package com.aact.sat_pda.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aact.sat_pda.dto.TreeDTO
import kotlin.apply
import kotlin.collections.forEachIndexed
import com.aact.sat_pda.R

class TreeAdapter(
    private val expandable: Boolean = true
) : RecyclerView.Adapter<TreeAdapter.GroupViewHolder>() {

    private val groupList = mutableListOf<TreeDTO.Group>()
    private var onItemCheckListener: ((itemCode: String, checkFlag: String) -> Unit)? = null      //박제훈 추가
    private var onGroupClickListener: ((groupCode: String) -> Unit)? = null

    // 박제훈 추가
    fun setOnItemCheckListener(listener: (itemCode: String, checkFlag: String) -> Unit) {
        onItemCheckListener = listener
    }

    // 박제춘 추가
    fun setOnGroupCheckListener(listener: (groupCode: String) -> Unit) {
        onGroupClickListener = listener
    }

    fun setData(data: List<TreeDTO.Group>) {
        groupList.clear()
        groupList.addAll(data)
        notifyDataSetChanged()
    }
    fun clear() {
        groupList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cust_tree_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groupList[position])
    }

    override fun getItemCount(): Int = groupList.size

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.tvGroup)
        private val childContainer = view.findViewById<LinearLayout>(R.id.childContainer)

        fun bind(group: TreeDTO.Group) {
            text.text = group.value
            childContainer.removeAllViews()

            // 하위 항목들 뷰 추가
            if(expandable) {
                group.itemArray.forEachIndexed { index, item ->
                    val checkBox = CheckBox(itemView.context).apply {
                        text = item.value
                        textSize = 17f
                        isChecked = item.checkFlag == "Y"
                        setOnCheckedChangeListener { _, isChecked ->
                            item.changeFlag = "Y"
                            item.checkFlag = if (isChecked) "Y" else "N"

                            // Model에 변경사항 알림           박제훈 추가
                            onItemCheckListener?.invoke(item.key, item.checkFlag)
                        }
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 10, 0, 10) // 좌우 0, 위아래 5dp
                        }
                    }
                    childContainer.addView(checkBox)
                }

                // 펼쳐짐 상태 반영
                childContainer.visibility = if (group.isExpanded) View.VISIBLE else View.GONE

                // 클릭 시 슬라이드로 열고 닫기
                itemView.setOnClickListener {
                    // 그룹 선택 이벤트 먼저 알림
                    onGroupClickListener?.invoke(group.key)

                    group.isExpanded = !group.isExpanded
                    if (group.isExpanded) {
                        expand(childContainer)
                    } else {
                        collapse(childContainer)
                    }
                }
            } else {
                // expandable = false: 펼침 없이 그룹 클릭 이벤트만
                childContainer.visibility = View.GONE

                itemView.setOnClickListener{
                    onGroupClickListener?.invoke(group.key)
                }
            }
        }

        private fun expand(view: View) {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = view.measuredHeight
            view.layoutParams.height = 0
            view.visibility = View.VISIBLE
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    view.layoutParams.height =
                        if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                        else (targetHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }

                override fun willChangeBounds(): Boolean = true
            }
            animation.duration = (targetHeight / view.context.resources.displayMetrics.density).toLong()
            view.startAnimation(animation)
        }

        private fun collapse(view: View) {
            val initialHeight = view.measuredHeight
            val animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    if (interpolatedTime == 1f) {
                        view.visibility = View.GONE
                    } else {
                        view.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                        view.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean = true
            }
            animation.duration = (initialHeight / view.context.resources.displayMetrics.density).toLong()
            view.startAnimation(animation)
        }
    }
}