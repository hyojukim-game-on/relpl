package com.gdd.presentation.mapper

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    private val recordResponseDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
    private val recoedUiDateFormat = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA)
    private val reportDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    /**
     * @param dateString "yyyy-MM-dd HH:mm"
     * @return "yyyy년MM월dd일" or if format exception ""
     */
    fun recordResponseFormatToUi(dateString: String): String{
        return try {
            recoedUiDateFormat.format(recordResponseDateFormat.parse(dateString)!!)
        } catch (t: Throwable){
            ""
        }
    }

    fun getReportDateFormatString(curMillis: Long): String{
        return reportDateFormat.format(curMillis)
    }
}