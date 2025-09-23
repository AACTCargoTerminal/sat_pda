package com.aact.sat_pda.appconfig

import androidx.lifecycle.MutableLiveData
import com.aact.sat_pda.dto.DataRow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Stack

object Common {

    val serverIp: List<DataRow> = listOf(
        DataRow(linkedMapOf("ip" to "localhost:60330", "name" to "개발환경1")),
        DataRow(linkedMapOf("ip" to "192.168.200.179", "name" to "개발환경2")),
        DataRow(linkedMapOf("ip" to "192.168.0.57", "name" to "운영환경")),
    )

    val serverKey = "aactsharpt2lkoalaicnkorea"
    val timeout: Long = 20L
    val programId: String = "PDA Android"
    val selectServer = MutableLiveData("192.168.0.57")


    val userId = MutableLiveData<String>("")
    val userName = MutableLiveData("")
    val departCode = MutableLiveData("")
    val departName = MutableLiveData("")
    val companyCode = MutableLiveData("")
    val companyName = MutableLiveData("")
    val branchCode = MutableLiveData("")
    val branchName = MutableLiveData("")
    val langName = MutableLiveData("")
    val langCode = MutableLiveData("ENG")
    val dateTime = MutableLiveData("")
    val terminalCode = MutableLiveData("")
    val terminalName = MutableLiveData("")
    val userIp = MutableLiveData("")
    val memo = MutableLiveData("")
    val version = MutableLiveData("2.01")
    val filename = MutableLiveData("")
    val updateFlag = MutableLiveData<Boolean>(false)
    val updateMsg = MutableLiveData<String>("업데이트중...")
    val userSid = MutableLiveData(0L)
    val userAuth_In_Cancel_Yn = MutableLiveData("")
    val userTerminalIp = MutableLiveData("")
    val permissionFlag = MutableLiveData(true)
    var menuList = listOf<Route>()

    val route = MutableLiveData<Route>(Route.Login)
    var backList: Stack<Route> = Stack()
    val loading = MutableLiveData<Pair<Boolean, Job?>>(Pair(false, null))
    val err = MutableLiveData<Pair<Boolean, String>>(Pair(false, ""))
    val suc = MutableLiveData<String>("")
    val calculList = mutableListOf<String>()
    val sound = MutableLiveData<String>("")


    fun loadingOn(job: Job?) {
        loading.value = Pair(true, job)
    }

    fun loadingOff() {
        loading.value = Pair(false, null)
    }

    fun sendError(message: String = "") {
        err.value = Pair(true, message)
    }

    fun addNavigate(rt: Route) {
        val tmpRt = route.value
        val stack = backList
        if (stack.size >= 5) {
            stack.pop()
        }
        if (stack.contains(tmpRt)) {
            stack.remove(tmpRt)
        }

        stack.push(tmpRt)

        route.value = rt
    }

    fun getStack() {
        val stack = backList
        stack.let {
            if (it.isNotEmpty()) {
                val popped = it.pop()
                backList = stack
                route.value = popped
            }
        }
    }
}