package com.aact.sat_pda.Biz

import com.aact.sat_pda.dto.HeaderDTO

class Header {

    val fltHeader = listOf<HeaderDTO>(
        HeaderDTO("SCHEDULE_SID", "id"),
        HeaderDTO("FLIGHT_NO", "편번", 140),
        HeaderDTO("TERMINAL_CODE", "터미널", 100)
    )

    val commonCodeHeader = listOf(
            HeaderDTO("CODE_SID", "id"),
            HeaderDTO("CODE_CODE", "Code", 180),
            HeaderDTO("CODE_NAME", "Name", 300)
        )

    val agencyCodeHeader = listOf(
            HeaderDTO("CUSTOMER_SID", "id"),
            HeaderDTO("CUSTOMER_CODE", "Code", 150),
            HeaderDTO("AGENCY_CODE", "Agency", 130),
            HeaderDTO("CUSTOMER_NAME", "Name", 300)
        )

    val crrcdHeader = listOf(
        HeaderDTO("CODE_SID", "id"),
        HeaderDTO("CODE_CODE", "Code", 180),
        HeaderDTO("CODE_NAME1", "Name", 300)
    )

    val uldScaleHeader = listOf(
        HeaderDTO("ULD_NO","ULD No",220),
        HeaderDTO("FLIGHT_NO","편번",140),
        HeaderDTO("DEPARTURE_TIME","일자",180),
        HeaderDTO("CONTOUR_CODE","Type",100),
        HeaderDTO("DESTINATION_CODE","도착지",100),
    )

}