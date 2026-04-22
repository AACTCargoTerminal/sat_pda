package com.aact.sat_pda

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aact.sat_pda.Adapter.BottomAdapter
import com.aact.sat_pda.Biz.Action
import com.aact.sat_pda.Biz.Item
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.UpdateCheckWorker
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.appconfig.Route
import com.aact.sat_pda.appconfig.SubContent
import com.aact.sat_pda.databinding.ActivityMainLayoutBinding
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.dto.SoundDTO
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Stack
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    protected lateinit var binding: ActivityMainLayoutBinding
    var calculFlag = false
    lateinit var soundPool: SoundPool
    var soundList: List<SoundDTO> = emptyList()
    lateinit var  audioManager : AudioManager

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Common.permissionFlag.value = true
            } else {
                Common.permissionFlag.value = false
            }
        }

    private val unknownSourcesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // 돌아왔을 때 권한 다시 확인
            if (packageManager.canRequestPackageInstalls()) {
                downloadAndInstallApk()
            } else {
                Common.sendError("알 수 없는 앱 설치 권한이 필요합니다.")
            }
        }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN) {
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
            when (event.keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    val view = this.currentFocus
                    if (view is EditText) {
                        view.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    }
                    return true
                }

                KeyEvent.KEYCODE_ESCAPE -> {
                    val view = this.currentFocus
                    if (view is EditText) {
                        val txt = view.text.toString().trim()

                        if (txt.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            // yyyy-MM-dd 형태의 날짜
                            val formatter =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val date = java.time.LocalDate.now()
                            view.setText(date.format(formatter))

                        } else {
                            view.text.clear()
                        }


                    }
                    return true
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    val view = this.currentFocus
                    if (view is EditText) {
                        val txt = view.text.toString().trim()

                        if (txt.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            // yyyy-MM-dd 형태의 날짜
                            val formatter =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val date = java.time.LocalDate.parse(txt, formatter)
                            val newDate = date.minusDays(1)  // -1일
                            view.setText(newDate.format(formatter))

                        } else if (txt.matches(Regex("[-+]?\\d+(\\.\\d+)?"))) {
                            // 숫자 (음수/양수/소수 포함)
                            val num = txt.toBigDecimal()
                            val newNum = num - java.math.BigDecimal.ONE  // -1
                            view.setText(newNum.stripTrailingZeros().toPlainString())
                        }

                    }
                    return true
                }

                KeyEvent.KEYCODE_DPAD_UP -> {
                    val view = this.currentFocus
                    if (view is EditText) {

                        val txt = view.text.toString().trim()

                        if (txt.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                            // yyyy-MM-dd 형태의 날짜
                            val formatter =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val date = java.time.LocalDate.parse(txt, formatter)
                            val newDate = date.plusDays(1)  // -1일
                            view.setText(newDate.format(formatter))

                        } else if (txt.matches(Regex("[-+]?\\d+(\\.\\d+)?"))) {
                            // 숫자 (음수/양수/소수 포함)
                            val num = txt.toBigDecimal()
                            val newNum = num + java.math.BigDecimal.ONE  // -1
                            view.setText(newNum.stripTrailingZeros().toPlainString())
                        }

                    }
                    return true
                }

            }
        } else {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 요청
        requestPermissionsIfNeeded()

        if(ApplicationInfo.FLAG_DEBUGGABLE and this.applicationInfo.flags == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!packageManager.canRequestPackageInstalls()) {
                    // 알 수 없는 소스 설치 권한 요청
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    unknownSourcesLauncher.launch(intent)
                }
            }
            startUpdateWorker(this)
        }
        startSound()
        audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        binding.content.backNavIcon.setOnClickListener {
            Common.getStack()
        }
        binding.content.headerTxt.setOnClickListener {
            Common.backList = Stack()
            Common.route.value = Route.Menu
        }

        binding.content.calculate.setOnClickListener {
            openModal(SubContent.Calculate)
        }
        val bottomView = binding.content.bottomTab
        bottomView.layoutParams = bottomView.layoutParams.apply {
            this.height = dpToPx(52)
        }
        bottomView.layoutManager =
            object : LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean = false
            }

        Common.sound.observe(this) {
            playClick(it)
        }


        Common.selectServer.observe(this) {
            val ret = startProc()
            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                else -> null
            }
        }

        Common.permissionFlag.observe(this) { value ->
            if (value == false) {
                showYes("권한", "권한을 확인하여주세요") {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${this@MainActivity.packageName}")
                    }
                    startActivity(intent)
                }
            }
        }
        Common.suc.observe(this) { message ->
            if (message.isNotEmpty()) {
                Common.sound.value = "SUC"
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_SHORT
                )
                val snackbarView = snackbar.view
                val params = snackbarView.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP   // 위쪽으로 붙이기
                snackbarView.layoutParams = params
                snackbarView.setBackgroundColor(Color.parseColor("#0C4876"))
                val textView =
                    snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                textView.textSize = 18f
                textView.setText(message)

                val icon =
                    ContextCompat.getDrawable(this, R.drawable.icon_aact_white) // <- 원하는 아이콘 리소스
                icon?.setBounds(0, 0, 32, 32)
                textView.setCompoundDrawables(icon, null, null, null)
                textView.compoundDrawablePadding = 16 // 아이콘과 텍스트 사이 간격(px)

                snackbar.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    snackbar.dismiss()
                }, 1500)
                Common.suc.postValue("")
            }
        }
        Common.err.observe(this) { value ->
            if (value.first) {
                Common.sound.value = "ERR"
                showYes("ERR", value.second ?: "알수없는오류") {}
            }
        }
        Common.route.observe(this) { route ->
            changeRoute(route)
        }
        Common.loading.observe(this) { (key, value) ->
            binding.loading.cancelBtn.visibility =
                if (value?.isActive == true) View.VISIBLE else View.GONE
            binding.loading.loadingLayout.visibility = if (key) View.VISIBLE else View.GONE
        }
        Common.updateFlag.observe(this) {
            if (it) {
                binding.update.updateLayout.visibility = View.VISIBLE
                checkAndRequestInstallPermission()
            }
        }
        Common.updateMsg.observe(this) {
            binding.update.updateTxt.setText(it)
        }

        binding.loading.cancelBtn.setOnClickListener {
            val job = Common.loading.value.second
            if (job != null && job.isActive) {
                job.cancel()
            }
            Common.loadingOff()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startSound() {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(attrs)
            .build()

        soundList = listOf(
            SoundDTO("SUC", soundPool.load(this, R.raw.success, 1)),
            SoundDTO("ERR", soundPool.load(this, R.raw.error, 1))
        )

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            val tempSound = soundList.find { dto -> dto.soundId == sampleId }
            if (status == 0 && tempSound != null) {
                tempSound.soundFlag = true
            }
        }
    }

    fun vibrateOnce() {

        @Suppress("DEPRECATION")
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 50ms 정도 짧게 진동
        val effect = VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }

    fun playClick(str: String) {

        val findData = soundList.find { it.key == str }

        if (findData == null || !findData.soundFlag) return   // 아직 로드 안됐으면 스킵(또는 큐잉)

        soundPool.play(findData.soundId, 1f, 1f, 1, 0, 1f)
        vibrateOnce()

    }

    fun changeRoute(route: Route) {

        val trans = supportFragmentManager.beginTransaction()
        trans.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        trans.replace(binding.content.mainContent.id, route.fragment())
        binding.content.headerTxt.text = route.name

        val stack = Common.backList

        if (stack.size > 0) {
            binding.content.backNavIcon.visibility = View.VISIBLE
        } else {
            binding.content.backNavIcon.visibility = View.GONE
        }

        val bottomView = binding.content.bottomTab
        setDefault(bottomView, route.bottomItems().size)
        bottomView.adapter = BottomAdapter(route.bottomItems())
        bottomView.visibility = View.VISIBLE


        trans.commit()

    }

    fun setDefault(recyclerView: RecyclerView, size: Int) {
        recyclerView.layoutManager = when {
            size > 5 -> object : LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
                override fun canScrollHorizontally() = true
            }

            else -> object : LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
                override fun canScrollHorizontally() = false
            }
        }
    }

    fun openModal(content: SubContent) {
        val action = Action()
        val item = Item()
        when (content) {
            is SubContent.ContentNone -> action.slide_TransY(
                view = binding.modal.subContent,
                duration = 300,
                transY = 1000f,
                startAction = {},
                endAction = { binding.modal.subContent.visibility = View.GONE }
            )

            is SubContent.GetTable -> {
                binding.modal.modalContent.removeAllViews()
                val table = item.getTable_Click(
                    context = this,
                    headerList = content.headerList,
                    bodyList = content.bodylist,
                    selectValue = content.selectValue,
                ) {
                    content.onClick(it);
                    openModal(SubContent.ContentNone)
                }

                binding.modal.modalContent.addView(table)

                createModalAnim() {
                    content.onClick(content.selectValue)
                }

                action.slide_TransY(
                    view = binding.modal.subContent,
                    transY = 0f,
                    duration = 300,
                    endAction = { },
                    startAction = { binding.modal.subContent.visibility = View.VISIBLE })
            }

            is SubContent.Calculate -> {
                binding.modal.modalContent.removeAllViews()
                val inflater = LayoutInflater.from(this)
                val calculatorView =
                    inflater.inflate(R.layout.cust_calculate, binding.modal.modalContent, false)
                setNumber(calculatorView)
                spcBtn(calculatorView)
                binding.modal.modalContent.addView(calculatorView)
                createModalAnim {

                }
                action.slide_TransY(
                    view = binding.modal.subContent,
                    transY = 0f,
                    duration = 300,
                    endAction = { },
                    startAction = { binding.modal.subContent.visibility = View.VISIBLE })
            }
        }
    }

    fun spcBtn(view: View) {
        val tv = view.findViewById<TextView>(R.id.tvDisplay)
        val subTv = view.findViewById<TextView>(R.id.subDisplay)
        view.findViewById<Button>(R.id.btnC).setOnClickListener {
            Common.calculList.clear()
            tv.text = "0"
            subTv.text = "0"
            calculFlag = false
        }
        view.findViewById<Button>(R.id.btnDel).setOnClickListener {
            var dropStr = tv.text.toString()
            if (dropStr.isNotEmpty()) dropStr = dropStr.dropLast(1)
            if (dropStr.isEmpty()) dropStr = "0"
            tv.text = dropStr
        }
        view.findViewById<Button>(R.id.btnEq).setOnClickListener {
            calculate(view, "=")
        }
        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            calculate(view, "+")
        }
        view.findViewById<Button>(R.id.btnMul).setOnClickListener {
            calculate(view, "*")
        }
        view.findViewById<Button>(R.id.btnSub).setOnClickListener {
            calculate(view, "-")
        }
        view.findViewById<Button>(R.id.btnDiv).setOnClickListener {
            calculate(view, "/")
        }
    }

    fun setNumber(view: View) {
        val digitIds = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2", R.id.btn3 to "3",
            R.id.btn4 to "4", R.id.btn5 to "5", R.id.btn6 to "6",
            R.id.btn7 to "7", R.id.btn8 to "8", R.id.btn9 to "9",
            R.id.btnDot to "."
        )
        val tv = view.findViewById<TextView>(R.id.tvDisplay)
        val subTv = view.findViewById<TextView>(R.id.subDisplay)
        digitIds.forEach { (id, s) ->
            view.findViewById<Button>(id).setOnClickListener {

                var str = tv.text.toString()


                if (Common.calculList.size > 0 && Common.calculList.last() == "=") {
                    str = ""
                    Common.calculList.clear()
                    subTv.text = "0"
                    calculFlag = false
                }
                if (s.first().isDigit() && calculFlag) { // 계산 직후 숫자 입력 시 새로 입력
                    str = ""
                    calculFlag = false
                }
                if (str == "0" && s.first().isDigit()) {
                    str = ""
                }

                if (s == ".") {
                    // 이미 소수점이 들어있으면 입력 안 함
                    if (str.contains(".")) {
                        return@setOnClickListener
                    } else {
                        if (str == "") {
                            str = "0"
                        }
                    }
                }

                str += s
                tv.text = str

            }
        }
    }

    fun calculate(view: View, sign: String) {
        val tv = view.findViewById<TextView>(R.id.tvDisplay)
        val subTv = view.findViewById<TextView>(R.id.subDisplay)

        if (Common.calculList.size > 0) {
            if ("+-*/=".contains(Common.calculList.last()) && calculFlag) {
                Common.calculList[Common.calculList.lastIndex] = sign
            } else {
                Common.calculList.add(tv.text.toString())
                Common.calculList.add(sign)
            }
        } else {
            Common.calculList.add(tv.text.toString())
            Common.calculList.add(sign)
        }
        calculFlag = true
        var str = ""
        Common.calculList.forEachIndexed { index, cal ->
            if (index == 0) {
                when (cal) {
                    "*" -> {
                        str += "×"
                    }

                    "/" -> {
                        str += "÷"
                    }

                    else -> {
                        str += cal
                    }
                }
            } else {
                when (cal) {
                    "*" -> {
                        str += " " + "×"
                    }

                    "/" -> {
                        str += " " + "÷"
                    }

                    else -> {

                        str += " " + cal
                    }
                }
            }
        }

        if (Common.calculList.size > 2) {
            tv.text = calSymbol()
        }
        subTv.text = str
    }

    fun parseNumber(str: String): Number {
        return try {
            if (str.contains(".")) {
                BigDecimal(str)   // 소수점 포함 → Double
            } else {
                BigInteger(str)    // 소수점 없음 → Int
            }
        } catch (e: NumberFormatException) {
            BigInteger.ZERO// 숫자가 아니면 null 반환
        }
    }

    private fun toBigDecimal(n: Number): BigDecimal = when (n) {
        is BigDecimal -> n
        is BigInteger -> BigDecimal(n)
        else -> BigDecimal(n.toString())
    }

    fun setCal(sym: String, left: Number, right: Number): String {
        var ret = ""
        when (sym) {
            "+" -> {
                if (left is BigInteger && right is BigInteger) {
                    ret = (left + right).toString()
                } else {
                    ret = toBigDecimal(left).add(toBigDecimal(right)).stripTrailingZeros()
                        .toPlainString()
                }
            }

            "*" -> {
                if (left is BigInteger && right is BigInteger) {
                    ret = (left * right).toString()
                } else {
                    ret = toBigDecimal(left).multiply(toBigDecimal(right)).stripTrailingZeros()
                        .toPlainString()
                }
            }

            "-" -> {
                if (left is BigInteger && right is BigInteger) {
                    ret = (left - right).toString()
                } else {
                    ret = toBigDecimal(left).subtract(toBigDecimal(right)).stripTrailingZeros()
                        .toPlainString()
                }
            }

            "/" -> {
                if (left is BigInteger && right is BigInteger) {
                    ret = toBigDecimal(left)
                        .divide(toBigDecimal(right), 10, RoundingMode.HALF_UP)
                        .stripTrailingZeros().toPlainString()
                } else {
                    ret = toBigDecimal(left)
                        .divide(toBigDecimal(right), 10, RoundingMode.HALF_UP)
                        .stripTrailingZeros().toPlainString()
                }
            }
        }
        return ret
    }

    fun calSymbol(): String {
        val tokens = Common.calculList
        if (tokens.isEmpty()) return "0"

        // 1. 곱셈, 나눗셈 먼저 처리
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/") {
                if (i == 0 || i == tokens.lastIndex) {
                    i++    // 다음 토큰으로 이동
                    continue
                }
                val left = parseNumber(tokens[i - 1])
                val right = parseNumber(tokens[i + 1])
                val result = setCal(token, left, right)

                // [좌, 연산자, 우] → result 로 교체
                tokens[i - 1] = result
                tokens.removeAt(i)     // 연산자 제거
                tokens.removeAt(i)     // 피연산자 제거 (원래 i+1 이었음)
                i -= 1                 // 인덱스 조정
            } else {
                i++
            }
        }

        // 2. 덧셈, 뺄셈 처리
        i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "+" || token == "-") {
                if (i == 0 || i == tokens.lastIndex) {
                    i++    // 다음 토큰으로 이동
                    continue
                }

                val left = parseNumber(tokens[i - 1])
                val right = parseNumber(tokens[i + 1])
                val result = setCal(token, left, right)

                tokens[i - 1] = result
                tokens.removeAt(i)
                tokens.removeAt(i)
                i -= 1
            } else {
                i++
            }
        }

        return tokens[0]
    }

    fun createModalAnim(action: () -> Unit) {
        val action = Action()
        binding.modal.modalCloseArea.setOnClickListener {
            action.slide_TransY(
                view = binding.modal.subContent,
                duration = 300,
                transY = 1000f,
                startAction = { action() },
                endAction = { binding.modal.subContent.visibility = View.GONE })
        }

        var initialY = 0f
        var isDragging = false

        action.touch_Event(
            view = binding.modal.dragHandle,
            actionStart = { event, _ -> initialY = event.rawY; isDragging = true },
            actionMove = { event, _ ->
                if (isDragging) {
                    val offsetY = event.rawY - initialY
                    if (offsetY > 0) {
                        binding.modal.subContent.translationY = offsetY
                    }
                }
            },
            actionEnd = { event, _ ->
                isDragging = false
                val offsetY = event.rawY - initialY
                if (offsetY > 150) { // 일정 거리 이상이면 닫기
                    binding.modal.subContent.animate()
                        .translationY(binding.modal.subContent.height.toFloat())
                        .setDuration(200)
                        .withEndAction {
                            action()
                            binding.modal.subContent.visibility = View.GONE
                        }
                        .start()
                } else {
                    // 다시 제자리로
                    binding.modal.subContent.animate()
                        .translationY(0f)
                        .setDuration(200)
                        .start()
                }
            },
            actionCancel = { event, _ ->
                isDragging = false
                val offsetY = event.rawY - initialY
                if (offsetY > 150) { // 일정 거리 이상이면 닫기
                    binding.modal.subContent.animate()
                        .translationY(binding.modal.subContent.height.toFloat())
                        .setDuration(200)
                        .withEndAction {
                            action()
                            binding.modal.subContent.visibility = View.GONE
                        }
                        .start()
                } else {
                    // 다시 제자리로
                    binding.modal.subContent.animate()
                        .translationY(0f)
                        .setDuration(200)
                        .start()
                }
            }

        )


    }

    fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    fun showYes(title: String, message: String, onYes: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onYes() }
            .show()
    }

    fun showYesNo(
        title: String,
        message: String,
        onYes: () -> Unit,
        onNo: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onYes() }
            .setNegativeButton("취소") { _, _ -> onNo() }
            .show()
    }

    fun showYesNoCancel(title: String,
                        message: String,
                        onYes: () -> Unit,
                        onNo: () -> Unit,
                        onCancel:() -> Unit){
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("예") { _, _ -> onYes() }
            .setNegativeButton("아니오") { _, _ -> onNo() }
            .setNeutralButton("취소") { _, _ ->
                onCancel()
            }.show()
    }

    fun getPermission(): Boolean {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA
        )

        // Android 버전별 저장소 권한 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // 권한 체크
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA
        )

        // Android 버전별 저장소 권한 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // 권한이 없는 것만 필터링
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isNotEmpty()) {
            permissionLauncher.launch(deniedPermissions.toTypedArray())
        } else {
            Common.permissionFlag.value = true
        }
    }

    fun startUpdateWorker(context: Context) {
        val appCtx = context.applicationContext
        val wm = WorkManager.getInstance(appCtx)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // 1) 즉시 1회
        val once = OneTimeWorkRequestBuilder<UpdateCheckWorker>()
            .setConstraints(constraints)
            // .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORKER) // 원하면 즉시성↑
            .addTag("updateCheck") // 관찰 편의용 태그
            .build()

        wm.enqueueUniqueWork(
            "updateCheckWork.once",
            ExistingWorkPolicy.KEEP,
            once
        )

        // ─ 결과 관찰(OneTime) ─
        wm.getWorkInfoByIdLiveData(once.id).observe(this) { info ->
            if (info != null) {
                when (info.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Common.loadingOff()
                        val out = info.outputData
                        val version = out.getString("VALUE1_CHAR") ?: ""
                        val filename = out.getString("VALUE2_CHAR") ?: ""
                        val memo = out.getString("VALUE3_CHAR") ?: ""
                        // UI반영/토스트 등
                        Common.memo.value = memo
                        Common.filename.value = filename
                        if(version == Common.version.value){
                            Common.updateFlag.value = false
                        }else{
                            Common.updateFlag.value = true
                        }
                    }
                    WorkInfo.State.FAILED -> {
                        Common.loadingOff()
                        val reason = info.outputData.getString("reason") ?: ""
                        val ex = info.outputData.getString("exception") ?: ""
                        Common.sendError(reason+" : "+ ex)
                    }
                    WorkInfo.State.CANCELLED,
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING,
                    WorkInfo.State.BLOCKED -> {
                        Common.loading.value = Pair(true,null)
                    }
                }
            }
        }

        // 2) 30분 주기
        val periodic = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
            30, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("updateCheck") // 태그로도 관찰 가능
            .build()

        wm.enqueueUniquePeriodicWork(
            "updateCheckWork.periodic",
            ExistingPeriodicWorkPolicy.KEEP,
            periodic
        )

        // ─ 결과 관찰(Periodic) ─
        // 주기 작업은 실행마다 다른 id가 생기므로 '유니크 이름'으로 묶어서 관찰
        wm.getWorkInfosForUniqueWorkLiveData("updateCheckWork.periodic")
            .observe(this) { infos ->
                // 여러 Work가 있을 수 있음 → 최신 상태 하나를 골라서 확인
                val latest = infos.maxByOrNull { it.runAttemptCount } ?: infos.lastOrNull()
                latest?.let { info ->
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val out = info.outputData
                            val version = out.getString("VALUE1_CHAR") ?: ""
                            val filename = out.getString("VALUE2_CHAR") ?: ""
                            val memo = out.getString("VALUE3_CHAR") ?: ""
                            // UI반영/토스트 등
                            Common.memo.value = memo
                            Common.filename.value = filename
                            if(version == Common.version.value){
                                Common.updateFlag.value = false
                            }else{
                                Common.updateFlag.value = true
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val reason = info.outputData.getString("reason") ?: ""
                            val ex = info.outputData.getString("exception") ?: ""
                            Common.sendError(reason+" : "+ ex)
                        }
                        else -> { }
                    }
                }
            }
    }

    fun startProc(): Result<Unit> {
        return try {

            Common.permissionFlag.value = getPermission()

            when (val result = Util.getLocalIp()) {
                is Result.Success<String> -> {
                    Common.userIp.value = result.data
                }

                is Result.Error -> {
                    return Result.Error(result.message)
                }
            }

            val splitIp = Common.userIp.value.split(".").get(2).trim()
            val api: MainAPI = MainAPI_Impl()

            lifecycleScope.launch {
                Common.loadingOn(coroutineContext[Job])

                try {
                    val result = api.getCommonCode("TRMCD")
                    when (result) {
                        is Result.Success<DataTable> -> {
                            val findObj =
                                result.data.table[1]?.find { it.row["VALUE1_NUMBER"] == splitIp }

                            if (findObj != null) {
                                Common.terminalCode.postValue(findObj.row["CODE_CODE"] ?: "T1")
                                Common.terminalName.postValue(
                                    findObj.row["CODE_NAME"] ?: "Terminal1"
                                )
                            } else {
                                Common.terminalCode.postValue("T1")
                                Common.terminalName.postValue("Terminal1")
                            }
                        }

                        is Result.Error -> Common.err.postValue(Pair(true, result.message))
                    }
                } catch (e: CancellationException) {
                    Common.err.postValue(Pair(true, "작업이 취소되었습니다."))
                } finally {
                    Common.loading.value = Pair(false, null)
                }

                Common.loadingOff()
            }


            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "MainModel.startProc")
        }
    }

    private fun checkAndRequestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                // 알 수 없는 소스 설치 권한 요청
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                unknownSourcesLauncher.launch(intent)
            } else {
                downloadAndInstallApk()
            }
        } else {
            downloadAndInstallApk()
        }
    }

    private fun downloadAndInstallApk() {
        Common.updateMsg.value = "다운로드 중 ..."
        val apkUrl = "http://${Common.selectServer.value}/content/pda/${Common.filename.value}"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apkFile = downloadApk(apkUrl)
                withContext(Dispatchers.Main) {
                    installApk(apkFile)
                }
            } catch (e: Exception) {
                Common.updateMsg.value = "다운로드 실패 : ${e.message}"
            }
        }
    }

    private suspend fun downloadApk(url: String): File {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                throw Exception("다운로드 실패: ${response.code}")
            }

            val apkFile = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")

            response.body?.let { body ->
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(apkFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } ?: throw Exception("응답 본문이 비어있습니다")

            apkFile
        }
    }

    private fun installApk(apkFile: File) {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Method 1: FileProvider 사용
                val authority = "${packageName}.fileprovider"
                val apkUri = FileProvider.getUriForFile(this, authority, apkFile)

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(apkUri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(installIntent)

            } else {
                // Method 2: 구버전 Android 처리
                val apkUri = Uri.fromFile(apkFile)
                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(apkUri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(installIntent)
            }
        } catch (e: Exception) {
            installApkFromInternalStorage(apkFile)
        }
    }

    private fun installApkFromInternalStorage(originalFile: File) {
        try {
            // 앱 내부 저장소로 파일 복사
            val internalFile = File(filesDir, "update.apk")
            originalFile.copyTo(internalFile, overwrite = true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    internalFile
                )

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(apkUri, "application/vnd.android.package-archive")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // 권한 부여
                val resInfoList = packageManager.queryIntentActivities(installIntent, 0)
                for (resolveInfo in resInfoList) {
                    grantUriPermission(
                        resolveInfo.activityInfo.packageName,
                        apkUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                startActivity(installIntent)
            }
        } catch (e: Exception) {
            Common.updateMsg.value = "업데이트에 실패했습니다. 관리자에게 문의하세요"
        }
    }
}
