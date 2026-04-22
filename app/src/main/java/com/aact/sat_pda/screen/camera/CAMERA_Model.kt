package com.aact.sat_pda.screen.camera

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aact.sat_pda.Biz.MainAPI
import com.aact.sat_pda.Biz.MainAPI_Impl
import com.aact.sat_pda.Biz.Util
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataRow
import com.aact.sat_pda.dto.DataTable
import com.aact.sat_pda.dto.HeaderDTO
import com.aact.sat_pda.dto.PictureDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.aact.sat_pda.dto.Result

class CAMERA_Model : ViewModel() {
    val api: MainAPI = MainAPI_Impl()

    var cargoControlSid = ""
    var damageExportSid = ""
    val mawb = MutableLiveData<String>("")

    val cameraList = MutableLiveData<List<DataRow>>(emptyList())
    val cameraHeader = listOf(
        HeaderDTO("EDM_SID", "id", 0),
        HeaderDTO("SOURCE_FILE_NAME", "파일명", 300),
        HeaderDTO("FILE_SIZE", "용량", 100)
    )
    val cameraSelect = MutableLiveData<Map<String, String>>(
        mapOf(
            "EDM_SID" to "",
            "SOURCE_FILE_NAME" to ""
        )
    )

    fun getList() {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])

            var ret : Result<DataTable>

            if(damageExportSid == ""){
                ret = api.getPCM_PDA_M010_001(cargoControlSid)
            }else{
                ret = api.getPWM_CARGO_DAMAGE_M010_009(damageExportSid,"M")
            }

            when (ret) {
                is Result.Error -> Common.sendError(ret.message)
                is Result.Success<DataTable> -> {
                    val data = ret.data.table
                    if (Util.getTableCell(0, data[0], "COL1") == "OK") {

                        data[1]?.let { data1 ->
                            cameraList.value = data1
                        } ?: run {
                            cameraList.value = emptyList()
                        }

                    } else {
                        Common.sendError(Util.getTableCell(0, data[0], "COL2"))
                    }
                }
            }

            Common.loadingOff()
        }
    }

    fun parseAndUpload(context: Context, uri: Uri) {
        viewModelScope.launch {
            Common.loadingOn(coroutineContext[Job])
            val result = parsePicture(context, uri)

            result?.let { cameraRet ->

                var ret: Result<DataTable>
                if (damageExportSid == "") {
                    ret = api.setPCM_PDA_M010_011(
                        cargoControlSid,
                        "Y",
                        cameraRet.data,
                        cameraRet.fileName,
                        cameraRet.fileLength.toString()
                    )
                } else {
                    ret = api.setPWM_CARGO_DAMAGE_M010_016(
                        damageExportSid,
                        "Y",
                        cameraRet.data,
                        cameraRet.fileName,
                        cameraRet.fileLength.toString()
                    )
                }

                when(ret){
                    is Result.Error -> Common.sendError(ret.message)
                    is Result.Success<DataTable> -> {
                        val data = ret.data.table
                        if(Util.getTableCell(0,data[0],"COL1")=="OK"){
                            Common.suc.value = "저장 됐습니다."
                            data[1]?.let { data1 ->
                                cameraList.value = data1
                            }?:run {
                                cameraList.value = emptyList()
                            }
                        }else{
                            Common.sendError(Util.getTableCell(0,data[0],"COL2"))
                        }
                    }
                }

            } ?: run {
                Common.sendError("카메라에러")
            }

            Common.loadingOff()

        }
    }

    suspend fun parsePicture(context: Context, uri: Uri): PictureDTO? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@withContext null
                inputStream.close()

                var name = "IMG_${System.currentTimeMillis()}.jpg"
                var size = bytes.size.toLong()

                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) name = cursor.getString(nameIndex)
                    }
                }

                PictureDTO(
                    data = Base64.encodeToString(bytes, Base64.NO_WRAP),
                    fileName = name,
                    fileLength = size
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}