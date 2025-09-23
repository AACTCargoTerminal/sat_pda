package com.aact.sat_pda.Biz

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    val api = MainAPI_Impl()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val ret = checkUpdate()  // null이면 실패라고 가정
            return@withContext if (ret == null) {
                Result.failure(workDataOf("reason" to "no_update_info"))
            } else {
                // 예: 버전/메시지 같은 정보 담아서 전달
                Result.success(
                    workDataOf(
                        "VALUE1_CHAR" to ret.row["VALUE1_CHAR"],   // 예시 필드
                        "VALUE2_CHAR" to ret.row["VALUE2_CHAR"],
                        "VALUE3_CHAR" to ret.row["VALUE3_CHAR"]  // 예시 필드
                    )
                )
            }
        } catch (e: Exception) {
            Common.sendError("업데이트 에러 : ${e.message}")
            return@withContext Result.failure(workDataOf("exception" to (e.message ?: "")))
        }
    }
    private suspend fun checkUpdate() : DataRow? {
        val ret = api.getCommonCode("PDAVS", "")
        when (ret) {
            is com.aact.sat_pda.dto.Result.Error -> Common.sendError(ret.message)
            is com.aact.sat_pda.dto.Result.Success<DataTable> -> {
                val data = ret.data.table

                if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                    data[1]?.let { list ->
                        return list.last()
                    }

                } else {
                    Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                }
            }
        }
        return null
    }
}