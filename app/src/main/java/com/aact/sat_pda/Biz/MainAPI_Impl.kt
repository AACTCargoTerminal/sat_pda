package com.aact.sat_pda.Biz

import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.appconfig.Common
import com.aact.sat_pda.dto.DataTable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.to

class MainAPI_Impl : MainAPI {

    override suspend fun getAuthority(): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_USER_ID" to Common.userId.value,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                )

                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getAuthority"
                )
                when (result) {
                    is Result.Success -> Result.Success(result.data)

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getAuthority")
            }

        }
    }

    override suspend fun getCommonCode(
        classCode: String,
        searchCode: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CLASS_CODE" to classCode,
                    "I_CODE_NAME" to searchCode,
                    "I_VALUE1_CHAR" to "",
                    "I_VALUE2_CHAR" to "",
                    "I_VALUE3_CHAR" to "",
                    "I_VALUE4_CHAR" to "",
                    "I_VALUE5_CHAR" to "",
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getCommonCode"
                )
                when (result) {
                    is Result.Success -> Result.Success(result.data)

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getCommonCode")
            }

        }
    }

    override suspend fun getUserInfoPDA(formdata: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "id" to formdata
                )

                when (val result = Util.sendAPI(reqData = reqData, methodName = "getUserInfoPDA")) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> {
                        Result.Error(result.message)
                    }
                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "ERROR")
            }
        }
    }

    override suspend fun getPWM_CARGO_OPERATION_L010_001(
        I_FLIGHT_DATE: String,
        I_SCHEDULE_SID: String,
        I_MASTER_AIR_WAY_BILL_NO: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_FLIGHT_DATE" to I_FLIGHT_DATE,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_OPERATION_L010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_OPERATION_L010_001")
            }

        }
    }

    override suspend fun getPWMFlightNo(date1: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "date1" to date1
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWMFlightNo"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_OPERATION_L010_001")
            }

        }
    }

    override suspend fun setPCM_PDA_M010_011(
        I_OBJECT_SID: String,
        I_USABLE_FLAG: String,
        data: String,
        I_FILENAME: String,
        I_FILESIZE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OBJECT_SID" to I_OBJECT_SID,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "data" to data,
                    "I_FILENAME" to I_FILENAME,
                    "I_FILESIZE" to I_FILESIZE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPCM_PDA_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPCM_PDA_M010_011")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M010_001_002(I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M010_001_002"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_001_002")
            }

        }
    }

    override suspend fun getPCM_CUSTOMER_P020_001(I_CUSTOMER_NAME: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CUSTOMER_NAME" to I_CUSTOMER_NAME,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPCM_CUSTOMER_P020_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_001_002")
            }

        }
    }

    override suspend fun getPCM_CUSTOMER_P010_001(
        I_CUSTOMER_NAME: String,
        I_REGISTRATION_NO: String,
        I_CUSTOMER_FLAG: String,
        I_VENDOR_FLAG: String,
        I_CARRIER_FLAG: String,
        I_AGENCY_FLAG: String,
        I_CUSTOMS_FLAG: String,
        I_IATA_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CUSTOMER_NAME" to I_CUSTOMER_NAME,
                    "I_REGISTRATION_NO" to I_REGISTRATION_NO,
                    "I_CUSTOMER_FLAG" to I_CUSTOMER_FLAG,
                    "I_VENDOR_FLAG" to I_VENDOR_FLAG,
                    "I_CARRIER_FLAG" to I_CARRIER_FLAG,
                    "I_AGENCY_FLAG" to I_AGENCY_FLAG,
                    "I_CUSTOMS_FLAG" to I_CUSTOMS_FLAG,
                    "I_IATA_FLAG" to I_IATA_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPCM_CUSTOMER_P010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_001_002")
            }

        }
    }

    override suspend fun setPWM_CARGO_CONTROL_M010_032_DEST_AGENCY(
        I_CARGO_CONTROL_SID: String,
        I_SCHEDULE_SID: String,
        I_DESTINATION_CODE: String,
        I_CUSTOMER_CODE: String,
        I_CUSTOMER_NAME: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_CUSTOMER_CODE" to I_CUSTOMER_CODE,
                    "I_CUSTOMER_NAME" to I_CUSTOMER_NAME,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_CONTROL_M010_032_DEST_AGENCY"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_CONTROL_M010_032_DEST_AGENCY")
            }

        }
    }

    override suspend fun setPWM_CARGO_SCALE_PRINT_MAWB(
        I_CARGO_CONTROL_SID: String,
        I_IP: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_IP" to I_IP,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_SCALE_PRINT_MAWB"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_SCALE_PRINT_MAWB")
            }

        }
    }

    override suspend fun setPWM_CARGO_VOLUME_PRINT_MAWB(
        I_CARGO_CONTROL_SID: String,
        I_IP: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_IP" to I_IP,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_VOLUME_PRINT_MAWB"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_VOLUME_PRINT_MAWB")
            }

        }
    }

    override suspend fun getPWM_CARGO_LOCATION_M010_001_002(I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_LOCATION_M010_001_002"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)
                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_VOLUME_PRINT_MAWB")
            }

        }
    }

    override suspend fun getPWM_SCHEDULE_M010_001(I_SCHEDULE_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_SCHEDULE_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_SCHEDULE_M010_001")
            }

        }
    }

    override suspend fun getCurDateTime(): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>()
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getCurDateTime"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_SCHEDULE_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_LOCATION_M010_011(
        I_CARGO_CONTROL_SID: String,
        I_LOCATION_DATE: String,
        I_LOCATION_TIME: String,
        I_FROM_LOCATION_CODE: String,
        I_FROM_ULD_FLAG: String,
        I_TO_LOCATION_CODE: String,
        I_TO_ULD_FLAG: String,
        I_TO_RACK_NO: String,
        I_TO_NO_OF_PACKAGE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LOCATION_DATE" to I_LOCATION_DATE,
                    "I_LOCATION_TIME" to I_LOCATION_TIME,
                    "I_FROM_LOCATION_CODE" to I_FROM_LOCATION_CODE,
                    "I_FROM_ULD_FLAG" to I_FROM_ULD_FLAG,
                    "I_TO_LOCATION_CODE" to I_TO_LOCATION_CODE,
                    "I_TO_ULD_FLAG" to I_TO_ULD_FLAG,
                    "I_TO_RACK_NO" to I_TO_RACK_NO,
                    "I_TO_NO_OF_PACKAGE" to I_TO_NO_OF_PACKAGE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_LOCATION_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_LOCATION_M010_011")
            }

        }
    }

    override suspend fun getCommonCodeWHLOC(
        I_CLASS_CODE: String,
        I_GUBN: String,
        I_USABLE_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CLASS_CODE" to I_CLASS_CODE,
                    "I_GUBN" to I_GUBN,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getCommonCodeWHLOC"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_LOCATION_M010_011")
            }

        }
    }

    override suspend fun getPGetPWM_CARGO_STOCK_M010_001(I_CARGO_ACCEPT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPGetPWM_CARGO_STOCK_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_STOCK_M010_011(
        I_CARGO_ACCEPT_SID: String,
        I_STOCK_DATE: String,
        I_STOCK_TIME: String,
        I_LOCATION_CODE: String,
        I_RACK_NO: String,
        I_SP_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_STOCK_DATE" to I_STOCK_DATE,
                    "I_STOCK_TIME" to I_STOCK_TIME,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_RACK_NO" to I_RACK_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_STOCK_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_STOCK_M015_011(
        I_CARGO_ACCEPT_SID: String,
        I_STOCK_DATE: String,
        I_STOCK_TIME: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_STOCK_DATE" to I_STOCK_DATE,
                    "I_STOCK_TIME" to I_STOCK_TIME,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_STOCK_M015_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_STOCK_M015_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_STOCK_M010_021(I_CARGO_ACCEPT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_STOCK_M010_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_STOCK_M015_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_STOCK_M015_021(I_CARGO_ACCEPT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_STOCK_M015_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_STOCK_M015_011")
            }

        }
    }

    override suspend fun getScaleWeight(ip0: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "ip0" to ip0
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getScaleWeight"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_STOCK_M015_011")
            }

        }
    }

    override suspend fun getPCM_GET_CARGO_ACCEPT_BY_MAWB(I_MASTER_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPCM_GET_CARGO_ACCEPT_BY_MAWB"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPCM_GET_CARGO_ACCEPT_BY_MAWB")
            }

        }
    }

    override suspend fun getPWM_SCHEDULE_L020_002(
        I_INOUT_FLAG: String,
        I_FLIGHT_DATE: String,
        I_AIRLINE_PREFIX: String,
        I_USABLE_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_INOUT_FLAG" to I_INOUT_FLAG,
                    "I_FLIGHT_DATE" to I_FLIGHT_DATE,
                    "I_AIRLINE_PREFIX" to I_AIRLINE_PREFIX,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_SCHEDULE_L020_002"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPCM_GET_CARGO_ACCEPT_BY_MAWB")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M010_001_VWT(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M010_001_VWT"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_001_VWT")
            }

        }
    }

    override suspend fun getPWM_EC_XML_L010_001(I_MASTER_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_EC_XML_L010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_EC_XML_L010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_ACCEPT_M010_011(
        I_CARGO_ACCEPT_SID: String,
        I_MASTER_AIR_WAY_BILL_NO: String,
        I_HOUSE_AIR_WAY_BILL_NO: String,
        I_SCHEDULE_SID: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_MFST_NO_OF_PACKAGE: String,
        I_MFST_NET_WEIGHT: String,
        I_AGENT_CUSTOMER_CODE: String,
        I_AGENT_CUSTOMER_NAME: String,
        I_GOSHOW_FLAG: String,
        I_FROM_WAREHOUSE_FLAG: String,
        I_BUP_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_HOUSE_AIR_WAY_BILL_NO" to I_HOUSE_AIR_WAY_BILL_NO,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_ORIGIN_CODE" to I_ORIGIN_CODE,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_MFST_NO_OF_PACKAGE" to I_MFST_NO_OF_PACKAGE,
                    "I_MFST_NET_WEIGHT" to I_MFST_NET_WEIGHT,
                    "I_AGENT_CUSTOMER_CODE" to I_AGENT_CUSTOMER_CODE,
                    "I_AGENT_CUSTOMER_NAME" to I_AGENT_CUSTOMER_NAME,
                    "I_GOSHOW_FLAG" to I_GOSHOW_FLAG,
                    "I_FROM_WAREHOUSE_FLAG" to I_FROM_WAREHOUSE_FLAG,
                    "I_BUP_FLAG" to I_BUP_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_ACCEPT_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_ACCEPT_M010_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_SCALE_M010_011(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_NO: String,
        I_SCALE_USER_ID: String,
        I_SCALE_SEQ: String,
        I_SCALE_DATE: String,
        I_SCALE_TIME: String,
        I_SPECIAL_CARGO_CODE: String,
        I_NO_OF_PACKAGE: String,
        I_GROSS_WEIGHT: String,
        I_PALLET_CODE: String,
        I_NO_OF_PALLET: String,
        I_TARE_WEIGHT: String,
        I_NET_WEIGHT: String,
        I_DESTINATION_CODE: String,
        I_CUSTOMER_CODE: String,
        I_CUSTOMER_NAME: String,
        I_MFST_PCS: String,
        I_MFST_WT: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_SCALE_NO" to I_SCALE_NO,
                    "I_SCALE_USER_ID" to I_SCALE_USER_ID,
                    "I_SCALE_SEQ" to I_SCALE_SEQ,
                    "I_SCALE_DATE" to I_SCALE_DATE,
                    "I_SCALE_TIME" to I_SCALE_TIME,
                    "I_SPECIAL_CARGO_CODE" to I_SPECIAL_CARGO_CODE,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_GROSS_WEIGHT" to I_GROSS_WEIGHT,
                    "I_PALLET_CODE" to I_PALLET_CODE,
                    "I_NO_OF_PALLET" to I_NO_OF_PALLET,
                    "I_TARE_WEIGHT" to I_TARE_WEIGHT,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_PALLET_CODE" to I_PALLET_CODE,
                    "I_CUSTOMER_CODE" to I_CUSTOMER_CODE,
                    "I_MFST_PCS" to I_MFST_PCS,
                    "I_MFST_WT" to I_MFST_WT,
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_SCALE_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_SCALE_M010_011")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M010_003(I_CARGO_ACCEPT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M010_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_003")
            }

        }
    }

    override suspend fun getPWM_CARGO_SCALE_M010_001(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_SCALE_SEQ" to I_SCALE_SEQ,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_SCALE_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_SCALE_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_SCALE_M010_011_UPDATE(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String,
        I_NO_OF_PACKAGE: String,
        I_GROSS_WEIGHT: String,
        I_SPECIAL_CARGO_CODE: String,
        I_PALLET_CODE: String,
        I_NO_OF_PALLET: String,
        I_TARE_WEIGHT: String,
        I_NET_WEIGHT: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_SCALE_SEQ" to I_SCALE_SEQ,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_GROSS_WEIGHT" to I_GROSS_WEIGHT,
                    "I_SPECIAL_CARGO_CODE" to I_SPECIAL_CARGO_CODE,
                    "I_PALLET_CODE" to I_PALLET_CODE,
                    "I_NO_OF_PALLET" to I_NO_OF_PALLET,
                    "I_TARE_WEIGHT" to I_TARE_WEIGHT,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_SCALE_M010_011_UPDATE"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_SCALE_M010_011_UPDATE")
            }

        }
    }

    override suspend fun setPWM_CARGO_SCALE_M010_021(
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String,
        I_PERMANENT_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_SCALE_SEQ" to I_SCALE_SEQ,
                    "I_PERMANENT_FLAG" to I_PERMANENT_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_SCALE_M010_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_SCALE_M010_021")
            }

        }
    }

    override suspend fun getPWM_CARGO_VOLUME_M010_002(I_CARGO_ACCEPT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_VOLUME_M010_002"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_VOLUME_M010_002")
            }

        }
    }

    override suspend fun setPWM_CARGO_VOLUME_M010_011(
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String,
        I_NO_OF_PACKAGE: String,
        I_DIMENSION_WIDTH: String,
        I_DIMENSION_HEIGHT: String,
        I_DIMENSION_LENGTH: String,
        I_VOLUME: String,
        I_VOLUME_WEIGHT: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_VOLUME_SEQ" to I_VOLUME_SEQ,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_DIMENSION_WIDTH" to I_DIMENSION_WIDTH,
                    "I_DIMENSION_HEIGHT" to I_DIMENSION_HEIGHT,
                    "I_DIMENSION_LENGTH" to I_DIMENSION_LENGTH,
                    "I_VOLUME" to I_VOLUME,
                    "I_VOLUME_WEIGHT" to I_VOLUME_WEIGHT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_VOLUME_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_VOLUME_M010_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_VOLUME_M010_021(
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String,
        I_PERMANENT_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_VOLUME_SEQ" to I_VOLUME_SEQ,
                    "I_PERMANENT_FLAG" to I_PERMANENT_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_VOLUME_M010_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_VOLUME_M010_011")
            }

        }
    }

    override suspend fun getPWM_CARGO_VOLUME_M010_001(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_CARGO_ACCEPT_SID" to I_CARGO_ACCEPT_SID,
                    "I_VOLUME_SEQ" to I_VOLUME_SEQ,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_VOLUME_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_VOLUME_M010_001")
            }

        }
    }

    override suspend fun getPWM_CARGO_ACCEPT_M010_001(
        cargoAcceptSid: String,
        mawbNo: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "cargoAcceptSid" to cargoAcceptSid,
                    "mawbNo" to mawbNo,
                    "userLang" to Common.langCode.value,
                    "guId" to Util.getGUID(),
                    "userId" to Common.userId.value,
                    "userIp" to Common.userTerminalIp.value,
                    "pgmId" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_ACCEPT_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_ACCEPT_M010_001")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_003(I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_003")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_007(
        I_DAMAGE_EXPORT_SID: String,
        I_TYPE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_TYPE" to I_TYPE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_007"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_007")
            }

        }
    }

    override suspend fun getSqlDataSet(sql: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "sql" to sql,
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getSqlDataSet"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getSqlDataSet")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_006(I_MASTER_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_006"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_006")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_008(I_DAMAGE_EXPORT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_008"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_008")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M010_001(I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M010_001")
            }

        }
    }

    override suspend fun getPCM_PDA_M010_001(I_OBJECT_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_OBJECT_SID" to I_OBJECT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPCM_PDA_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPCM_PDA_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_DAMAGE_M010_016(
        I_DAMAGE_EXPORT_SID: String,
        I_USABLE_FLAG: String,
        data: String,
        I_FILENAME: String,
        I_FILESIZE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "data" to data,
                    "I_FILENAME" to I_FILENAME,
                    "I_FILESIZE" to I_FILESIZE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_DAMAGE_M010_016"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_DAMAGE_M010_016")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_009(
        I_DAMAGE_EXPORT_SID: String,
        I_TYPE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_TYPE" to I_TYPE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_009"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_009")
            }

        }
    }

    override suspend fun setPWM_CARGO_DAMAGE_M010_014_015(
        I_DAMAGE_EXPORT_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_SCHEDULE_SID: String,
        I_ACCEPT_TYPE: String,
        I_MASTER_AIR_WAY_BILL_NO: String,
        I_HOUSE_AIR_WAY_BILL_NO: String,
        I_DAMAGE_USER_ID: String,
        I_NO_OF_PACKAGE: String,
        I_NO_OF_PACKAGE_DAMAGE: String,
        I_GOODS: String,
        I_REMARK: String,
        I_ITEM_DATA: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_ACCEPT_TYPE" to I_ACCEPT_TYPE,
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_HOUSE_AIR_WAY_BILL_NO" to I_HOUSE_AIR_WAY_BILL_NO,
                    "I_DAMAGE_USER_ID" to I_DAMAGE_USER_ID,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NO_OF_PACKAGE_DAMAGE" to I_NO_OF_PACKAGE_DAMAGE,
                    "I_GOODS" to I_GOODS,
                    "I_REMARK" to I_REMARK,
                    "I_ITEM_DATA" to I_ITEM_DATA,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_DAMAGE_M010_014_015"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_DAMAGE_M010_014_015")
            }

        }
    }

    override suspend fun setPWM_CARGO_DAMAGE_EXPORT_PRINT(
        I_IP: String,
        I_DAMAGE_EXPORT_SID: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_IP" to I_IP,
                    "I_DAMAGE_EXPORT_SID" to I_DAMAGE_EXPORT_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_DAMAGE_EXPORT_PRINT"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_DAMAGE_EXPORT_PRINT")
            }

        }
    }

    override suspend fun getPWM_OPERATION_ULD_L010_001(
        I_FLIGHT_DATE: String,
        I_FLIGHT_NO: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_FLIGHT_DATE" to I_FLIGHT_DATE,
                    "I_FLIGHT_NO" to I_FLIGHT_NO,
                    "I_ORIGIN_CODE" to I_ORIGIN_CODE,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_USABLE_FLAG" to "Y",
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_OPERATION_ULD_L010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_OPERATION_ULD_L010_001")
            }

        }
    }

    override suspend fun getPWM_OPERATION_ULD_M010_001(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_OPERATION_ULD_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_OPERATION_ULD_M010_001")
            }

        }
    }

    override suspend fun getPWM_ULD_INVENTORY_P020_001(
        I_OWNER_CARRIER_CODE: String,
        I_ULD_TYPE_CODE: String,
        I_ULD_NO: String,
        I_USABLE_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OWNER_CARRIER_CODE" to I_OWNER_CARRIER_CODE,
                    "I_ULD_TYPE_CODE" to I_ULD_TYPE_CODE,
                    "I_ULD_NO" to I_ULD_NO,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_INVENTORY_P020_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_INVENTORY_P020_001")
            }

        }
    }

    override suspend fun setPWM_ULD_STOCK_M010_011(
        I_ULD_SID: String,
        I_ULD_NO: String,
        I_ULD_TYPE_CODE: String,
        I_ULD_SERIAL_NO: String,
        I_ULD_CARRIER_CODE: String,
        I_CONTOUR_CODE: String,
        I_CONTOUR_NO: String,
        I_OWNER_CARRIER_CODE: String,
        I_ULD_STATUS_CODE: String,
        I_BULK_FLAG: String,
        I_DAMAGED_FLAG: String,
        I_RACK_NO: String,
        I_MASTER_ULD_NO: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_ULD_SID" to I_ULD_SID,
                    "I_ULD_NO" to I_ULD_NO,
                    "I_ULD_TYPE_CODE" to I_ULD_TYPE_CODE,
                    "I_ULD_SERIAL_NO" to I_ULD_SERIAL_NO,
                    "I_ULD_CARRIER_CODE" to I_ULD_CARRIER_CODE,
                    "I_CONTOUR_CODE" to I_CONTOUR_CODE,
                    "I_CONTOUR_NO" to I_CONTOUR_NO,
                    "I_OWNER_CARRIER_CODE" to I_OWNER_CARRIER_CODE,
                    "I_ULD_STATUS_CODE" to I_ULD_STATUS_CODE,
                    "I_BULK_FLAG" to I_BULK_FLAG,
                    "I_DAMAGED_FLAG" to I_DAMAGED_FLAG,
                    "I_RACK_NO" to I_RACK_NO,
                    "I_MASTER_ULD_NO" to I_MASTER_ULD_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_STOCK_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_STOCK_M010_011")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_011_T2A(
        I_OPERATION_ULD_SID: String,
        I_ULD_NO: String,
        I_CONTOUR_CODE: String,
        I_CONTOUR_NO: String,
        I_SCHEDULE_SID: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_LOCATION_CODE: String,
        I_BUP_FLAG: String,
        I_BULK_FLAG: String,
        I_THRU_FLAG: String,
        I_REMARKS: String,
        I_SCALED_WEIGHT: String,
        I_AGENT_CUSTOMER_CODE: String,
        I_AGENT_CUSTOMER_NAME: String,
        I_TRANSFERAT: String,
        I_TRANSFERFLT: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_ULD_NO" to I_ULD_NO,
                    "I_CONTOUR_CODE" to I_CONTOUR_CODE,
                    "I_CONTOUR_NO" to I_CONTOUR_NO,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_ORIGIN_CODE" to I_ORIGIN_CODE,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_BUP_FLAG" to I_BUP_FLAG,
                    "I_BULK_FLAG" to I_BULK_FLAG,
                    "I_THRU_FLAG" to I_THRU_FLAG,
                    "I_REMARKS" to I_REMARKS,
                    "I_SCALED_WEIGHT" to I_SCALED_WEIGHT,
                    "I_AGENT_CUSTOMER_CODE" to I_AGENT_CUSTOMER_CODE,
                    "I_AGENT_CUSTOMER_NAME" to I_AGENT_CUSTOMER_NAME,
                    "I_TRANSFERAT" to I_TRANSFERAT,
                    "I_TRANSFERFLT" to I_TRANSFERFLT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_011_T2A"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_011_T2A")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_021(
        I_OPERATION_ULD_SID: String,
        I_PERMANENT_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_PERMANENT_FLAG" to I_PERMANENT_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_021")
            }

        }
    }

    override suspend fun getPWM_OPERATION_ULD_M010_003(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_OPERATION_ULD_M010_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_OPERATION_ULD_M010_003")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_032(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_032"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_032")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_034(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_034"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_034")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_031(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_031"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_031")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_012(
        I_OPERATION_ULD_SID: String,
        I_MATERIAL_CODE: String,
        I_MATERIAL_QTY: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_MATERIAL_CODE" to I_MATERIAL_CODE,
                    "I_MATERIAL_QTY" to I_MATERIAL_QTY,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_012"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_012")
            }

        }
    }

    override suspend fun getPWM_ULD_SCALE_MASTER_INFO(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_SCALE_MASTER_INFO"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_SCALE_MASTER_INFO")
            }

        }
    }

    override suspend fun getPWM_ULD_SCALE_M010_001(
        I_FLIGHT_DATE: String,
        I_SCALED_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_FLIGHT_DATE" to I_FLIGHT_DATE,
                    "I_SCALED_FLAG" to I_SCALED_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_SCALE_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_SCALE_M010_001")
            }

        }
    }

    override suspend fun setPWM_ULD_SCALE_M010_011(
        I_OPERATION_ULD_SID: String,
        I_SCALE_NO: String,
        I_SCALED_WEIGHT: String,
        I_TARE_WEIGHT: String,
        I_SCALE_USER_ID: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_SCALE_NO" to I_SCALE_NO,
                    "I_SCALED_WEIGHT" to I_SCALED_WEIGHT,
                    "I_TARE_WEIGHT" to I_TARE_WEIGHT,
                    "I_SCALE_USER_ID" to I_SCALE_USER_ID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_SCALE_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_SCALE_M010_011")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_033(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_033"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_033")
            }

        }
    }

    override suspend fun setPWM_ULD_SCALE_PRINT(
        I_ULDNO: String,
        I_TYPE: String,
        I_FLTDT: String,
        I_FLTNO: String,
        I_DEST: String,
        I_KG: String,
        I_USER: String,
        I_WT: String,
        I_WT_DOLLY: String,
        I_WT_TOTAL: String,
        I_IP: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_ULDNO" to I_ULDNO,
                    "I_TYPE" to I_TYPE,
                    "I_FLTDT" to I_FLTDT,
                    "I_FLTNO" to I_FLTNO,
                    "I_DEST" to I_DEST,
                    "I_KG" to I_KG,
                    "I_USER" to I_USER,
                    "I_WT" to I_WT,
                    "I_WT_DOLLY" to I_WT_DOLLY,
                    "I_WT_TOTAL" to I_WT_TOTAL,
                    "I_IP" to I_IP,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_SCALE_PRINT"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_SCALE_PRINT")
            }

        }
    }

    override suspend fun setPWM_ULD_SCALE_SLIP_ONLY_PRINT(
        I_ULDNO: String,
        I_TYPE: String,
        I_FLTDT: String,
        I_FLTNO: String,
        I_DEST: String,
        I_KG: String,
        I_USER: String,
        I_IP: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_ULDNO" to I_ULDNO,
                    "I_TYPE" to I_TYPE,
                    "I_FLTDT" to I_FLTDT,
                    "I_FLTNO" to I_FLTNO,
                    "I_DEST" to I_DEST,
                    "I_KG" to I_KG,
                    "I_USER" to I_USER,
                    "I_IP" to I_IP,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_SCALE_SLIP_ONLY_PRINT"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_SCALE_SLIP_ONLY_PRINT")
            }

        }
    }

    override suspend fun getPWM_ULD_BUILD_UP_M010_004(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BUILD_UP_M010_004"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BUILD_UP_M010_004")
            }

        }
    }

    override suspend fun getPWM_ULD_BUILD_UP_M010_001_002_003(I_OPERATION_ULD_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BUILD_UP_M010_001_002_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BUILD_UP_M010_001_002_003")
            }

        }
    }

    override suspend fun getPWM_ULD_BUILD_UP_M010_002_003(
        I_SCHEDULE_SID: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_MASTER_AIR_WAY_BILL_NO: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_ORIGIN_CODE" to I_ORIGIN_CODE,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BUILD_UP_M010_002_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BUILD_UP_M010_002_003")
            }

        }
    }

    override suspend fun getPWM_ULD_BUILD_UP_M010_003(I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BUILD_UP_M010_003"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BUILD_UP_M010_003")
            }

        }
    }

    override suspend fun setPWM_ULD_BUILD_UP_M010_011(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_LOCATION_CODE: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_MFST_WEIGHT: String,
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_MFST_WEIGHT" to I_MFST_WEIGHT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_BUILD_UP_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_BUILD_UP_M010_011")
            }

        }
    }

    override suspend fun setPWM_OPERATION_ULD_M010_011(
        I_OPERATION_ULD_SID: String,
        I_ULD_NO: String,
        I_CONTOUR_CODE: String,
        I_CONTOUR_NO: String,
        I_SCHEDULE_SID: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_LOCATION_CODE: String,
        I_BUP_FLAG: String,
        I_BULK_FLAG: String,
        I_THRU_FLAG: String,
        I_REMARKS: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_ULD_NO" to I_ULD_NO,
                    "I_CONTOUR_CODE" to I_CONTOUR_CODE,
                    "I_CONTOUR_NO" to I_CONTOUR_NO,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_ORIGIN_CODE" to I_ORIGIN_CODE,
                    "I_DESTINATION_CODE" to I_DESTINATION_CODE,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_BUP_FLAG" to I_BUP_FLAG,
                    "I_BULK_FLAG" to I_BULK_FLAG,
                    "I_THRU_FLAG" to I_THRU_FLAG,
                    "I_REMARKS" to I_REMARKS,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_OPERATION_ULD_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_OPERATION_ULD_M010_011")
            }

        }
    }

    override suspend fun setPWM_ULD_BUILD_UP_M010_014(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_LOCATION_CODE: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_MFST_WEIGHT: String,
        I_SPECIAL_CARGO_CODE: String,
        I_SPECIAL_CARGO_REMARK: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_MFST_WEIGHT" to I_MFST_WEIGHT,
                    "I_SPECIAL_CARGO_CODE" to I_SPECIAL_CARGO_CODE,
                    "I_SPECIAL_CARGO_REMARK" to I_SPECIAL_CARGO_REMARK,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_BUILD_UP_M010_014"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_BUILD_UP_M010_014")
            }

        }
    }

    override suspend fun setPWM_ULD_BUILD_UP_M010_021(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_PERMANENT_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_PERMANENT_FLAG" to I_PERMANENT_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_BUILD_UP_M010_021"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_BUILD_UP_M010_021")
            }

        }
    }

    override suspend fun getPWM_CARGO_STOCK_L010_001(I_STOCK_DATE: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_STOCK_DATE" to I_STOCK_DATE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_STOCK_L010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_L010_001")
            }

        }
    }

    override suspend fun getPWM_CARGO_STOCK_L010_002(
        I_STOCK_DATE: String,
        I_SCHEDULE_SID: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_STOCK_DATE" to I_STOCK_DATE,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_STOCK_L010_002"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_L010_002")
            }

        }
    }

    override suspend fun getPWM_XRAY_L010_007(
        I_FROM_DATE: String,
        I_TO_DATE: String,
        I_MAWB: String,
        I_TERMINAL_FLAG: String,
        I_RELEASE_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_FROM_DATE" to I_FROM_DATE,
                    "I_TO_DATE" to I_TO_DATE,
                    "I_MAWB" to I_MAWB,
                    "I_TERMINAL_FLAG" to I_TERMINAL_FLAG,
                    "I_RELEASE_FLAG" to I_RELEASE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_XRAY_L010_007"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_XRAY_L010_007")
            }

        }
    }

    override suspend fun setPWM_XRAY_L010_014(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SEQ: String,
        I_COMPANY_SID: String,
        I_RELEASE_FLAG: String,
        I_USER: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_CARGO_ACCEPT_SEQ" to I_CARGO_ACCEPT_SEQ,
                    "I_COMPANY_SID" to I_COMPANY_SID,
                    "I_RELEASE_FLAG" to I_RELEASE_FLAG,
                    "I_USER" to I_USER,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_XRAY_L010_014"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_XRAY_L010_014")
            }

        }
    }

    override suspend fun getPWM_CARGO_STOCK_L020_001(): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_STOCK_L020_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_L020_001")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M020_002_BY_HAWB(I_HOUSE_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_HOUSE_AIR_WAY_BILL_NO" to I_HOUSE_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M020_002_BY_HAWB"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M020_002_BY_HAWB")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M020_002_BY_MAWB(I_MASTER_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M020_002_BY_MAWB"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M020_002_BY_MAWB")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M020_002_BY_NO(I_CARGO_CONTROL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M020_002_BY_NO"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M020_002_BY_NO")
            }

        }
    }

    override suspend fun getPWM_ULD_BREAK_DOWN_M020_004(I_MASTER_AIR_WAY_BILL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_MASTER_AIR_WAY_BILL_NO" to I_MASTER_AIR_WAY_BILL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BREAK_DOWN_M020_004"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BREAK_DOWN_M020_004")
            }

        }
    }

    override suspend fun getPWM_CARGO_CONTROL_M020_002_011_BY_NO(I_CARGO_CONTROL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_CONTROL_M020_002_011_BY_NO"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_CONTROL_M020_002_011_BY_NO")
            }

        }
    }

    override suspend fun getPWM_CARGO_HOLD_M010_001(
        I_CARGO_CONTROL_NO: String,
        I_HOLD_TYPE_CODE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                    "I_HOLD_TYPE_CODE" to I_HOLD_TYPE_CODE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_HOLD_M010_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_HOLD_M010_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_HOLD_M010_011(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String,
        I_HOLD_USER_ID: String,
        I_HOLD_DATE: String,
        I_HOLD_TIME: String,
        I_HOLD_REMARKS: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_SP_FLAG: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_HOLD_TYPE_CODE" to I_HOLD_TYPE_CODE,
                    "I_HOLD_USER_ID" to I_HOLD_USER_ID,
                    "I_HOLD_DATE" to I_HOLD_DATE,
                    "I_HOLD_TIME" to I_HOLD_TIME,
                    "I_HOLD_REMARKS" to I_HOLD_REMARKS,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_SP_FLAG" to I_SP_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_HOLD_M010_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_HOLD_M010_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_HOLD_M010_011A(
        I_CARGO_CONTROL_SID: String,
        I_IRR_NO_OF_PACKAGE: String
    ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_IRR_NO_OF_PACKAGE" to I_IRR_NO_OF_PACKAGE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "I_PROGRESS_GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_HOLD_M010_011A"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_HOLD_M010_011A")
            }

        }
    }

    override suspend fun getPWM_ULD_BREAK_DOWN_M020_001(I_FLIGHT_DATE: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_FLIGHT_DATE" to I_FLIGHT_DATE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BREAK_DOWN_M020_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BREAK_DOWN_M020_001")
            }

        }
    }

    override suspend fun getPWM_CARGO_OPERATION_L020_063(
        I_SCHEDULE_SID: String,
        I_USABLE_FLAG: String,): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_OPERATION_L020_063"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_OPERATION_L020_063")
            }

        }
    }


    override suspend fun getPWM_ULD_BREAK_DOWN_M020_005(I_CARGO_CONTROL_SID: String, ): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_ULD_BREAK_DOWN_M020_005"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_ULD_BREAK_DOWN_M020_005")
            }

        }
    }

    override suspend fun setPWM_ULD_BREAK_DOWN_M020_013(
        I_CARGO_BREAKDOWN_SID: String,
        I_SCHEDULE_SID: String,
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_LOCATION_CODE: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_BREAKDOWN_SID" to I_CARGO_BREAKDOWN_SID,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_BREAK_DOWN_M020_013"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_BREAK_DOWN_M020_013")
            }

        }
    }

    override suspend fun getPWM_CARGO_OPERATION_L020_061(
        I_SCHEDULE_SID: String,
        I_USABLE_FLAG: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_USABLE_FLAG" to I_USABLE_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_OPERATION_L020_061"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_OPERATION_L020_061")
            }

        }
    }

    override suspend fun getPWM_CARGO_STOCK_M020_001(
        I_CARGO_CONTROL_SID: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_OPERATION_L020_061"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_OPERATION_L020_061")
            }

        }
    }

    override suspend fun getPWM_CARGO_STOCK_M020_001_BY_NO(
        I_CARGO_CONTROL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_STOCK_M020_001_BY_NO"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_STOCK_M020_001_BY_NO")
            }

        }
    }

    override suspend fun setPWM_ULD_BREAK_DOWN_M020_011(
        I_CARGO_BREAKDOWN_SID: String,
        I_SCHEDULE_SID: String,
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_BREAKDOWN_SID" to I_CARGO_BREAKDOWN_SID,
                    "I_SCHEDULE_SID" to I_SCHEDULE_SID,
                    "I_OPERATION_ULD_SID" to I_OPERATION_ULD_SID,
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                    "I_NET_WEIGHT" to I_NET_WEIGHT,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_ULD_BREAK_DOWN_M020_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_ULD_BREAK_DOWN_M020_011")
            }

        }
    }

    override suspend fun setPWM_CARGO_STOCK_M020_011(
        I_CARGO_CONTROL_SID: String,
        I_STOCK_DATE: String,
        I_STOCK_TIME: String,
        I_MUST_DECLARE_FLAG: String,
        I_LOCATION_CODE: String,
        I_RACK_NO: String,
        I_SP_FLAG: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_STOCK_DATE" to I_STOCK_DATE,
                    "I_STOCK_TIME" to I_STOCK_TIME,
                    "I_MUST_DECLARE_FLAG" to I_MUST_DECLARE_FLAG,
                    "I_LOCATION_CODE" to I_LOCATION_CODE,
                    "I_RACK_NO" to I_RACK_NO,
                    "I_SP_FLAG" to I_SP_FLAG,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_STOCK_M020_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_STOCK_M020_011")
            }

        }
    }

    override suspend fun getPWM_CARGO_DAMAGE_M010_002_003_004_BY_NO(
        I_CARGO_CONTROL_NO: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_DAMAGE_M010_002_003_004_BY_NO"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_DAMAGE_M010_002_003_004_BY_NO")
            }

        }
    }

    override suspend fun setPWM_CARGO_DAMAGE_M010_013(
        I_CARGO_CONTROL_SID: String,
        I_DAMAGE_GROUP_CODE: String,
        I_DAMAGE_ITEM_CODE: String,
        I_CHECK_FLAG: String,
        I_NO_OF_DAMAGE: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_DAMAGE_GROUP_CODE" to I_DAMAGE_GROUP_CODE,
                    "I_DAMAGE_ITEM_CODE" to I_DAMAGE_ITEM_CODE,
                    "I_CHECK_FLAG" to I_CHECK_FLAG,
                    "I_NO_OF_DAMAGE" to I_NO_OF_DAMAGE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_DAMAGE_M010_013"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_DAMAGE_M010_013")
            }

        }
    }


    override suspend fun setPWM_CARGO_DAMAGE_M010_012(
        I_CARGO_CONTROL_SID: String,
        I_DAMAGED_HOLD_FLAG: String,
        I_DAMAGED_NO_OF_PACKAGE: String,
        I_DAMAGED_DESCRIPTION: String,
        I_DAMAGED_USER_ID: String,
        I_DAMAGED_DATE: String,
        I_DAMAGED_TIME: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_DAMAGED_HOLD_FLAG" to I_DAMAGED_HOLD_FLAG,
                    "I_DAMAGED_NO_OF_PACKAGE" to I_DAMAGED_NO_OF_PACKAGE,
                    "I_DAMAGED_DESCRIPTION" to I_DAMAGED_DESCRIPTION,
                    "I_DAMAGED_USER_ID" to I_DAMAGED_USER_ID,
                    "I_DAMAGED_DATE" to I_DAMAGED_DATE,
                    "I_DAMAGED_TIME" to I_DAMAGED_TIME,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_DAMAGE_M010_012"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_DAMAGE_M010_012")
            }

        }
    }

    override suspend fun getPWM_CARGO_HOLD_M020_001(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_HOLD_TYPE_CODE" to I_HOLD_TYPE_CODE,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "getPWM_CARGO_HOLD_M020_001"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.getPWM_CARGO_HOLD_M020_001")
            }

        }
    }

    override suspend fun setPWM_CARGO_HOLD_M020_011(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String,
        I_RELEASE_TYPE_CODE: String,
        I_RELEASE_DATE: String,
        I_RELEASE_TIME: String,
        I_RELEASE_REMARKS: String): Result<DataTable> {
        return withContext(Dispatchers.IO) {
            try {
                val reqData = mapOf<String, String>(
                    "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                    "I_HOLD_TYPE_CODE" to I_HOLD_TYPE_CODE,
                    "I_RELEASE_TYPE_CODE" to I_RELEASE_TYPE_CODE,
                    "I_RELEASE_DATE" to I_RELEASE_DATE,
                    "I_RELEASE_TIME" to I_RELEASE_TIME,
                    "I_RELEASE_REMARKS" to I_RELEASE_REMARKS,
                    "I_LANGUAGE_CODE" to Common.langCode.value,
                    "GUID" to Util.getGUID(),
                    "I_REQUEST_USER_ID" to Common.userId.value,
                    "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                    "I_REQUEST_PROGRAM_ID" to Common.programId
                )
                val result = Util.sendAPI(
                    reqData = reqData,
                    methodName = "setPWM_CARGO_HOLD_M020_011"
                )
                when (result) {
                    is Result.Success -> {
                        Result.Success(result.data)
                    }

                    is Result.Error -> Result.Error(result.message)


                }

            } catch (e: CancellationException) {
                Result.Error("작업이 취소되었습니다.")
            } catch (e: Exception) {
                Result.Error(e.message ?: "API.setPWM_CARGO_HOLD_M020_011")
            }

        }
    }


    override suspend fun setPWM_CARGO_RELEASE_M020_011(
        I_CARGO_CONTROL_SID: String,
        I_INOUT_KIND: String,
        I_RELEASE_DATE: String,
        I_RELEASE_TIME: String,
        I_CUSTOMS_IDENTIFY_NO: String,
        I_APPROVE_DATE: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        GRID_LOCATION_CODE: String,
        GRID_NO_OF_PACKAGE: String
    ): Result<DataTable> = withContext(Dispatchers.IO) {
        try {
            val reqData = mapOf<String, String>(
                "I_CARGO_CONTROL_SID" to I_CARGO_CONTROL_SID,
                "I_INOUT_KIND" to I_INOUT_KIND,
                "I_RELEASE_DATE" to I_RELEASE_DATE,
                "I_RELEASE_TIME" to I_RELEASE_TIME,
                "I_CUSTOMS_IDENTIFY_NO" to I_CUSTOMS_IDENTIFY_NO,
                "I_APPROVE_DATE" to I_APPROVE_DATE,
                "I_NO_OF_PACKAGE" to I_NO_OF_PACKAGE,
                "I_NET_WEIGHT" to I_NET_WEIGHT,
                "GRID_LOCATION_CODE" to GRID_LOCATION_CODE,
                "GRID_NO_OF_PACKAGE" to GRID_NO_OF_PACKAGE,
                "I_LANGUAGE_CODE" to Common.langCode.value,
                "GUID" to Util.getGUID(),
                "I_REQUEST_USER_ID" to Common.userId.value,
                "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                "I_REQUEST_PROGRAM_ID" to Common.programId
            )

            val result = Util.sendAPI(
                reqData = reqData,
                methodName = "setPWM_CARGO_RELEASE_M020_011"
            )

            when (result) {
                is Result.Success -> {
                    Result.Success(result.data)
                }

                is Result.Error -> Result.Error(result.message)


            }
        } catch (e: CancellationException) {
            Result.Error("작업이 취소되었습니다.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "API.setPWM_CARGO_HOLD_M020_011")
        }
    }

    override suspend fun getPWM_CARGO_RELEASE_M020_002_BY_NO(
        I_CARGO_CONTROL_NO: String
    ): Result<DataTable> = withContext(Dispatchers.IO) {
        try {
            val reqData = mapOf<String, String>(
                "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                "I_LANGUAGE_CODE" to Common.langCode.value,
                "GUID" to Util.getGUID(),
                "I_REQUEST_USER_ID" to Common.userId.value,
                "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                "I_REQUEST_PROGRAM_ID" to Common.programId
            )

            val result = Util.sendAPI(
                reqData = reqData,
                methodName = "getPWM_CARGO_RELEASE_M020_002_BY_NO"
            )

            when (result) {
                is Result.Success -> {
                    Result.Success(result.data)
                }

                is Result.Error -> Result.Error(result.message)


            }
        } catch (e: CancellationException) {
            Result.Error("작업이 취소되었습니다.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "API.getPWM_CARGO_RELEASE_M020_002_BY_NO")
        }
    }

    override suspend fun getPWM_CARGO_RELEASE_M020_001_BY_NO(
        I_CARGO_CONTROL_NO: String
    ): Result<DataTable> = withContext(Dispatchers.IO) {
        try {
            val reqData = mapOf<String, String>(
                "I_CARGO_CONTROL_NO" to I_CARGO_CONTROL_NO,
                "I_LANGUAGE_CODE" to Common.langCode.value,
                "GUID" to Util.getGUID(),
                "I_REQUEST_USER_ID" to Common.userId.value,
                "I_REQUEST_IP_ADDRESS" to Common.userTerminalIp.value,
                "I_REQUEST_PROGRAM_ID" to Common.programId
            )

            val result = Util.sendAPI(
                reqData = reqData,
                methodName = "getPWM_CARGO_RELEASE_M020_001_BY_NO"
            )

            when (result) {
                is Result.Success -> {
                    Result.Success(result.data)
                }

                is Result.Error -> Result.Error(result.message)


            }
        } catch (e: CancellationException) {
            Result.Error("작업이 취소되었습니다.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "API.getPWM_CARGO_RELEASE_M020_001_BY_NO")
        }
    }




}