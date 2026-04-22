package com.aact.sat_pda.Biz

import com.aact.sat_pda.dto.Result
import com.aact.sat_pda.dto.DataTable

interface MainAPI {

    //메뉴리스트 및 권한
    suspend fun getAuthority(): Result<DataTable>

    //공통코드 조회
    suspend fun getCommonCode(classCode: String,searchCode:String = ""): Result<DataTable>

    //로그인
    suspend fun getUserInfoPDA(formdata: String): Result<DataTable>

    //수출 화물 목록
    suspend fun getPWM_CARGO_OPERATION_L010_001(
        I_FLIGHT_DATE: String,
        I_SCHEDULE_SID: String,
        I_MASTER_AIR_WAY_BILL_NO: String
    ): Result<DataTable>

    suspend fun getPWMFlightNo(date1: String): Result<DataTable>

    suspend fun setPCM_PDA_M010_011(
        I_OBJECT_SID: String,
        I_USABLE_FLAG: String,
        data: String,
        I_FILENAME: String,
        I_FILESIZE: String
    ): Result<DataTable>

    //수출 화물 목록
    suspend fun getPWM_CARGO_CONTROL_M010_001_002(
        I_CARGO_CONTROL_SID: String
    ): Result<DataTable>

    suspend fun getPCM_CUSTOMER_P020_001(
        I_CUSTOMER_NAME: String
    ): Result<DataTable>

    suspend fun getPCM_CUSTOMER_P010_001(
        I_CUSTOMER_NAME: String,
        I_REGISTRATION_NO: String,
        I_CUSTOMER_FLAG: String,
        I_VENDOR_FLAG: String,
        I_CARRIER_FLAG: String,
        I_AGENCY_FLAG: String,
        I_CUSTOMS_FLAG: String,
        I_IATA_FLAG: String
    ): Result<DataTable>


    suspend fun setPWM_CARGO_CONTROL_M010_032_DEST_AGENCY(
        I_CARGO_CONTROL_SID: String,
        I_SCHEDULE_SID: String,
        I_DESTINATION_CODE: String,
        I_CUSTOMER_CODE: String,
        I_CUSTOMER_NAME: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_SCALE_PRINT_MAWB(
        I_CARGO_CONTROL_SID: String,
        I_IP: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_VOLUME_PRINT_MAWB(
        I_CARGO_CONTROL_SID: String,
        I_IP: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_LOCATION_M010_001_002(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_SCHEDULE_M010_001(
        I_SCHEDULE_SID: String,
    ): Result<DataTable>

    suspend fun getCurDateTime(
    ): Result<DataTable>

    suspend fun setPWM_CARGO_LOCATION_M010_011(
        I_CARGO_CONTROL_SID:String,
        I_LOCATION_DATE:String,
        I_LOCATION_TIME:String,
        I_FROM_LOCATION_CODE:String,
        I_FROM_ULD_FLAG:String,
        I_TO_LOCATION_CODE:String,
        I_TO_ULD_FLAG:String,
        I_TO_RACK_NO:String,
        I_TO_NO_OF_PACKAGE:String
    ): Result<DataTable>

    suspend fun getCommonCodeWHLOC(
        I_CLASS_CODE: String,
        I_GUBN:String,
        I_USABLE_FLAG:String
    ): Result<DataTable>

    suspend fun getPGetPWM_CARGO_STOCK_M010_001(
        I_CARGO_ACCEPT_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_STOCK_M010_011(
        I_CARGO_ACCEPT_SID: String,
        I_STOCK_DATE : String,
        I_STOCK_TIME : String,
        I_LOCATION_CODE : String,
        I_RACK_NO : String,
        I_SP_FLAG: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_STOCK_M015_011(
        I_CARGO_ACCEPT_SID: String,
        I_STOCK_DATE:String,
        I_STOCK_TIME:String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_STOCK_M010_021(
        I_CARGO_ACCEPT_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_STOCK_M015_021(
        I_CARGO_ACCEPT_SID: String,
    ): Result<DataTable>

    suspend fun getScaleWeight(
        ip0: String,
    ): Result<DataTable>

    suspend fun getPCM_GET_CARGO_ACCEPT_BY_MAWB(
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>

    suspend fun getPWM_SCHEDULE_L020_002(
        I_INOUT_FLAG: String,
        I_FLIGHT_DATE: String,
        I_AIRLINE_PREFIX: String,
        I_USABLE_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_CONTROL_M010_001_VWT(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_EC_XML_L010_001(
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_ACCEPT_M010_011(
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
    ): Result<DataTable>

    suspend fun setPWM_CARGO_SCALE_M010_011(
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
    ): Result<DataTable>

    suspend fun getPWM_CARGO_CONTROL_M010_003(
        I_CARGO_ACCEPT_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_SCALE_M010_001(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_SCALE_M010_011_UPDATE(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String,
        I_NO_OF_PACKAGE: String,
        I_GROSS_WEIGHT: String,
        I_SPECIAL_CARGO_CODE: String,
        I_PALLET_CODE: String,
        I_NO_OF_PALLET: String,
        I_TARE_WEIGHT: String,
        I_NET_WEIGHT: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_SCALE_M010_021(
        I_CARGO_ACCEPT_SID: String,
        I_SCALE_SEQ: String,
        I_PERMANENT_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_VOLUME_M010_002(
        I_CARGO_ACCEPT_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_VOLUME_M010_011(
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String,
        I_NO_OF_PACKAGE: String,
        I_DIMENSION_WIDTH: String,
        I_DIMENSION_HEIGHT: String,
        I_DIMENSION_LENGTH: String,
        I_VOLUME: String,
        I_VOLUME_WEIGHT: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_VOLUME_M010_021(
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String,
        I_PERMANENT_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_VOLUME_M010_001(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SID: String,
        I_VOLUME_SEQ: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_ACCEPT_M010_001(
        cargoAcceptSid: String,
        mawbNo: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_003(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_007(
        I_DAMAGE_EXPORT_SID: String,
        I_TYPE: String,
    ): Result<DataTable>

    suspend fun getSqlDataSet(
        sql: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_006(
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_008(
        I_DAMAGE_EXPORT_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_CONTROL_M010_001(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun getPCM_PDA_M010_001(
        I_OBJECT_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_DAMAGE_M010_016(
        I_DAMAGE_EXPORT_SID: String,
        I_USABLE_FLAG: String,
        data: String,
        I_FILENAME: String,
        I_FILESIZE: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_009(
        I_DAMAGE_EXPORT_SID: String,
        I_TYPE: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_DAMAGE_M010_014_015(
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
        I_ITEM_DATA: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_DAMAGE_EXPORT_PRINT(
        I_IP: String,
        I_DAMAGE_EXPORT_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_OPERATION_ULD_L010_001(
        I_FLIGHT_DATE: String,
        I_FLIGHT_NO: String,
        I_ORIGIN_CODE:String,
        I_DESTINATION_CODE:String
    ): Result<DataTable>

    suspend fun getPWM_OPERATION_ULD_M010_001(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_INVENTORY_P020_001(
        I_OWNER_CARRIER_CODE: String,
        I_ULD_TYPE_CODE: String,
        I_ULD_NO: String,
        I_USABLE_FLAG: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_STOCK_M010_011(
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
        I_MASTER_ULD_NO: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_011_T2A(
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
        I_TRANSFERFLT: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_011(
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
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_021(
        I_OPERATION_ULD_SID: String,
        I_PERMANENT_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_OPERATION_ULD_M010_003(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_031(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_032(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_012(
        I_OPERATION_ULD_SID: String,
        I_MATERIAL_CODE:String,
        I_MATERIAL_QTY:String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_034(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_OPERATION_ULD_M010_033(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_SCALE_MASTER_INFO(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_SCALE_M010_001(
        I_FLIGHT_DATE: String,
        I_SCALED_FLAG: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_SCALE_M010_011(
        I_OPERATION_ULD_SID: String,
        I_SCALE_NO: String,
        I_SCALED_WEIGHT: String,
        I_TARE_WEIGHT: String,
        I_SCALE_USER_ID: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_SCALE_PRINT(
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
        I_IP: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_SCALE_SLIP_ONLY_PRINT(
        I_ULDNO: String,
        I_TYPE: String,
        I_FLTDT: String,
        I_FLTNO: String,
        I_DEST: String,
        I_KG: String,
        I_USER: String,
        I_IP: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BUILD_UP_M010_004(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BUILD_UP_M010_001_002_003(
        I_OPERATION_ULD_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BUILD_UP_M010_002_003(
        I_SCHEDULE_SID: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BUILD_UP_M010_003(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_BUILD_UP_M010_011(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_LOCATION_CODE: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_MFST_WEIGHT: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_BUILD_UP_M010_014(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_LOCATION_CODE: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_MFST_WEIGHT: String,
        I_SPECIAL_CARGO_CODE: String,
        I_SPECIAL_CARGO_REMARK: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_BUILD_UP_M010_021(
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_PERMANENT_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_STOCK_L010_001(
        I_STOCK_DATE: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_STOCK_L010_002(
        I_STOCK_DATE: String,
        I_SCHEDULE_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_XRAY_L010_007(
        I_FROM_DATE: String,
        I_TO_DATE: String,
        I_MAWB: String,
        I_TERMINAL_FLAG: String,
        I_RELEASE_FLAG: String,
    ): Result<DataTable>

    suspend fun setPWM_XRAY_L010_014(
        I_CARGO_CONTROL_SID: String,
        I_CARGO_ACCEPT_SEQ: String,
        I_COMPANY_SID: String,
        I_RELEASE_FLAG: String,
        I_USER: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_STOCK_L020_001(
    ): Result<DataTable>
    suspend fun getPWM_CARGO_CONTROL_M020_002_BY_NO(
        I_CARGO_CONTROL_NO: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BREAK_DOWN_M020_004(
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_CONTROL_M020_002_BY_MAWB(
        I_MASTER_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_CONTROL_M020_002_BY_HAWB(
        I_HOUSE_AIR_WAY_BILL_NO: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_CONTROL_M020_002_011_BY_NO(
        I_CARGO_CONTROL_NO: String,
    ): Result<DataTable>
    suspend fun getPWM_CARGO_HOLD_M010_001(
        I_CARGO_CONTROL_NO: String,
        I_HOLD_TYPE_CODE: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_HOLD_M010_011(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String,
        I_HOLD_USER_ID: String,
        I_HOLD_DATE: String,
        I_HOLD_TIME: String,
        I_HOLD_REMARKS: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_SP_FLAG: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_HOLD_M010_011A(
        I_CARGO_CONTROL_SID: String,
        I_IRR_NO_OF_PACKAGE: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BREAK_DOWN_M020_001(
        I_FLIGHT_DATE: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BREAK_DOWN_M020_002(
        I_SCHEDULE_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BREAK_DOWN_M020_003(
        I_SCHEDULE_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_OPERATION_L020_063(
        I_SCHEDULE_SID: String,
        I_USABLE_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_ULD_BREAK_DOWN_M020_005(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_BREAK_DOWN_M020_013(
        I_CARGO_BREAKDOWN_SID: String,
        I_SCHEDULE_SID: String,
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
        I_LOCATION_CODE: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_OPERATION_L020_061(
        I_SCHEDULE_SID: String,
        I_USABLE_FLAG: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_STOCK_M020_001(
        I_CARGO_CONTROL_SID: String,
    ): Result<DataTable>

    suspend fun getPWM_CARGO_STOCK_M020_001_BY_NO(
        I_CARGO_CONTROL_NO: String,
    ): Result<DataTable>

    suspend fun setPWM_ULD_BREAK_DOWN_M020_011(
        I_CARGO_BREAKDOWN_SID: String,
        I_SCHEDULE_SID: String,
        I_OPERATION_ULD_SID: String,
        I_CARGO_CONTROL_SID: String,
        I_NO_OF_PACKAGE: String,
        I_NET_WEIGHT: String,
    ): Result<DataTable>

    suspend fun setPWM_CARGO_STOCK_M020_011(
        I_CARGO_CONTROL_SID: String,
        I_STOCK_DATE: String,
        I_STOCK_TIME: String,
        I_MUST_DECLARE_FLAG: String,
        I_LOCATION_CODE: String,
        I_RACK_NO: String,
        I_SP_FLAG: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_DAMAGE_M010_002_003_004_BY_NO(
        I_CARGO_CONTROL_NO: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_DAMAGE_M010_013(
        I_CARGO_CONTROL_SID: String,
        I_DAMAGE_GROUP_CODE: String,
        I_DAMAGE_ITEM_CODE: String,
        I_CHECK_FLAG: String,
        I_NO_OF_DAMAGE: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_DAMAGE_M010_012(
        I_CARGO_CONTROL_SID: String,
        I_DAMAGED_HOLD_FLAG: String,
        I_DAMAGED_NO_OF_PACKAGE: String,
        I_DAMAGED_DESCRIPTION: String,
        I_DAMAGED_USER_ID: String,
        I_DAMAGED_DATE: String,
        I_DAMAGED_TIME: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_HOLD_M020_001(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_HOLD_M020_011(
        I_CARGO_CONTROL_SID: String,
        I_HOLD_TYPE_CODE: String,
        I_RELEASE_TYPE_CODE: String,
        I_RELEASE_DATE: String,
        I_RELEASE_TIME: String,
        I_RELEASE_REMARKS: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_RELEASE_M020_011(
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
    ): Result<DataTable>

    suspend fun getPWM_CARGO_RELEASE_M020_001_BY_NO(
        I_CARGO_CONTROL_SID: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_RELEASE_M020_002_BY_NO(
        I_CARGO_CONTROL_NO: String
    ): Result<DataTable>

    suspend fun getPWM_CARGO_CONTROL_M040_001(
        I_SCHEDULE_SID: String
    ): Result<DataTable>

    suspend fun setPWM_CARGO_CONTROL_M040_011(
        I_CARGO_CONTROL_SID: String,
        I_SCHEDULE_SID: String,
        I_MANIFEST_REFERENCE_NO: String,
        I_MASTER_BL_SEQUENCE_NO: String,
        I_HOUSE_BL_SEQUENCE_NO: String,
        I_MASTER_AIR_WAY_BILL_NO: String,
        I_HOUSE_AIR_WAY_BILL_NO: String,
        I_ORIGIN_CODE: String,
        I_DESTINATION_CODE: String,
        I_ASSIGN_CODE: String,
        I_MFST_NO_OF_PACKAGE: String,
        I_MFST_NET_WEIGHT: String,
        I_ACCEPTED_NO_OF_PACKAGE: String,
        I_ACCEPTED_NET_WEIGHT: String,
        I_HOLD_TYPE_CODE: String,
        I_HOLD_USER_ID: String,
        I_HOLD_DATE: String,
        I_HOLD_TIME: String,
        I_HOLD_REMARKS: String
    ): Result<DataTable>
}