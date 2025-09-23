package com.aact.sat_pda.Biz

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aact.sat_pda.Adapter.RowAdapter
import com.aact.sat_pda.Adapter.TableView
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.R
import com.aact.sat_pda.appconfig.Common
import java.util.Locale

class Item {
    fun disableEditText(view: EditText) {

        val parent = view.parent as? LinearLayout
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(0xFFF5F5F5.toInt())
            setStroke(3, 0xFFA5A5A5.toInt())
            cornerRadius = 3f * view.resources.displayMetrics.density
        }
        parent?.background = drawable

        view.apply {
            isFocusable = false
            isClickable = false
            isCursorVisible = false

            inputType = InputType.TYPE_NULL
            showSoftInputOnFocus = false
        }
    }
    fun strEditText(view: EditText) {
        view.apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            imeOptions = EditorInfo.IME_ACTION_DONE
            showSoftInputOnFocus = false
            filters = arrayOf(InputFilter.AllCaps(Locale.getDefault()))
        }
    }

    fun limitEditText(view: EditText,length: Int){
        view.apply {
            showSoftInputOnFocus = false
            filters = arrayOf(InputFilter.LengthFilter(length))
        }
    }

    fun numDefEditText(view: EditText){
        val parent = view.parent as? LinearLayout
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(0xFFFFFFFF.toInt())
            setStroke(3, 0xFFBFBFBF.toInt())
            cornerRadius = 3f * view.resources.displayMetrics.density
        }
        parent?.background = drawable

        // 속성 설정
        view.apply {
            background = null
            isCursorVisible = true
            isFocusable = true
            isFocusableInTouchMode = true
            showSoftInputOnFocus = false
            imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_DONE
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setTextIsSelectable(true)
            textSize = 18f
            setText("")
        }
    }

    fun getTableRowChk(context: Context,
                       value: Boolean,
                       backColor: String = "#00FFFFFF",
                       w: Int,
                       onClick:(flag: Boolean)-> Unit): LinearLayout{
        val params = LinearLayout.LayoutParams(
            w,
            50
        )
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(backColor))
            layoutParams = params
        }

        val chkBox = CheckBox(context).apply {
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            isChecked = value
        }
        layout.isSoundEffectsEnabled = false
        layout.setOnClickListener {
            chkBox.performClick()
        }

        chkBox.setOnCheckedChangeListener { _,isChecked->
            onClick(isChecked)
        }

        layout.addView(chkBox)
        return layout
    }

    fun getTableRow(
        context: Context,
        value: String,
        backColor: String = "#00FFFFFF",//"#00FFFFFF"
        textColor: String = "#000000",
        w: Int
    ): LinearLayout {

        val params = LinearLayout.LayoutParams(
            w,
            50
        )
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(backColor))
            layoutParams = params
        }

        val textView = TextView(context).apply {
            text = value
            setTextColor(Color.parseColor(textColor))
            textSize = 17f
            gravity = Gravity.CENTER
            isSingleLine = true
            setPadding(20, 10, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        layout.addView(textView)
        return layout
    }

    fun getTableRowEdit(
        context: Context,
        value: String,
        backColor: String = "#00FFFFFF",
        textColor: String = "#000000",
        w: Int,
        textChange:(str:String)-> Unit
    ): LinearLayout {

        val params = LinearLayout.LayoutParams(
            w,
            50
        )
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor(backColor))
            layoutParams = params
        }

        val textView = EditText(context).apply {
            setText(value)
            setTextColor(Color.parseColor(textColor))
            textSize = 17f
            gravity = Gravity.CENTER
            isSingleLine = true
            background = null
            setPadding(20, 10, 20, 10)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        Item().strEditText(textView)

        Action().textChange_after(textView){
            textChange(it.toString())
        }

        layout.addView(textView)
        return layout
    }

    fun getHeaderRow(
        context: Context,
        headerList: List<HeaderDTO>
    ): TableLayout {
        val tableLayout = TableLayout(context).apply {
            layoutParams = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(Color.parseColor("#1F1F2B"))
            gravity = Gravity.CENTER
        }
        val headerRow = TableRow(context).apply {
            gravity = Gravity.CENTER
            layoutParams = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    2,
                    0,
                    2,
                    0
                )
            }
        }
        headerList.forEachIndexed { index, value ->
            if (value.value != "id") {

                val textView = TextView(context).apply {
                    text = value.value
                    textSize = 17f
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                    isSingleLine = true
                    setPadding(20, 10, 20, 10)
                    setTextColor(Color.parseColor("#FFFFFF"))
                    layoutParams =
                        TableRow.LayoutParams(value.avg, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
                headerRow.addView(textView)
                if (index != headerList.lastIndex) {
                    val dividerDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(Color.parseColor("#DDDDDD")) // 회색
                        cornerRadius = 4f // 끝 둥글게
                    }

                    val divider = View(context).apply {
                        layoutParams =
                            TableRow.LayoutParams(
                                3,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            ).apply {
                                setMargins(0, 6, 0, 6)
                            }
                        background = dividerDrawable
                    }

                    headerRow.addView(divider)

                }
            }

        }
        tableLayout.addView(headerRow)

        return tableLayout
    }

    fun getTable_Click(
        context: Context,
        headerList: List<HeaderDTO>,
        bodyList: List<DataRow>,
        selectValue: Map<String, String>,
        selectList: MutableMap<Int, DataRow> = mutableMapOf(),
        height: Int? = null,
        onClick: (value: Map<String, String>) -> Unit
    ): View {
        val borderDrawable = object : Drawable() {
            private val paint = Paint().apply {
                color = Color.parseColor("#1F1F2B")
                strokeWidth = 4f
            }

            override fun draw(canvas: Canvas) {
                val bounds = bounds
                // 왼쪽 선
                canvas.drawLine(
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    bounds.left.toFloat(),
                    bounds.bottom.toFloat(),
                    paint
                )
                // 오른쪽 선
                canvas.drawLine(
                    bounds.right.toFloat(),
                    bounds.top.toFloat(),
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat(),
                    paint
                )
                // 아래쪽 선
                canvas.drawLine(
                    bounds.left.toFloat(),
                    bounds.bottom.toFloat(),
                    bounds.right.toFloat(),
                    bounds.bottom.toFloat(),
                    paint
                )
            }

            override fun setAlpha(alpha: Int) {
                paint.alpha = alpha
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {
                paint.colorFilter = colorFilter
            }

            override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
        }
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }
        val scrollLayout = HorizontalScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            background = borderDrawable
            isFillViewport = true
        }
        val verticalContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                if (height == null) ViewGroup.LayoutParams.WRAP_CONTENT else height
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }

        }
        val headerTable = getHeaderRow(context = context, headerList = headerList)


        val recyclerView =  RecyclerView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f
            )
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        val recyclerTable = RowAdapter(bodyList,headerList,selectValue,selectList ,onClick = onClick)
        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        val adapter =  recyclerTable
        recyclerView.adapter = adapter

        verticalContainer.addView(headerTable)
        if(bodyList.size >0){
            verticalContainer.addView(recyclerView)
        }
        scrollLayout.addView(verticalContainer)
        container.addView(scrollLayout) //scrollLayout
        return container //container
    }

    fun setSpiner_Str(context: Context,view: Spinner, list: List<String>, onClick: (String) -> Unit) {
        val adapter =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.adapter = adapter

        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                onClick(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


}