package com.gdd.data.api

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.UserIdRequest
import com.gdd.data.model.report.ReportRecordResponse
import com.gdd.data.model.report.ReportRegistRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("report/regist")
    suspend fun registReport(
        @Body reportRegistRequest: ReportRegistRequest
    ):Result<DefaultResponse<Boolean>>

    @POST("report/list")
    suspend fun getReportRecordList(
        @Body userIdRequest: UserIdRequest
    ): Result<DefaultResponse<List<ReportRecordResponse>>>
}