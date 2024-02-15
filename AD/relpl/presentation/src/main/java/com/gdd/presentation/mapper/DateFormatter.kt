package com.gdd.presentation.mapper

import java.text.SimpleDateFormat
import java.util.Locale

object DateFormatter {
    private val longDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
    private val koreanDateFormat = SimpleDateFormat("yyyy년MM월dd일", Locale.KOREA)
    private val shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val koreanDateToMinuteFormat = SimpleDateFormat("yyyy년 MM월 dd일 aa kk시 mm분", Locale.KOREA)
    private val historyDateFormat = SimpleDateFormat("yyyy.MM.dd.", Locale.KOREA)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)

    /**
     * @param dateString "yyyy-MM-dd HH:mm"
     * @return "yyyy년MM월dd일" or if format exception ""
     */
    fun recordResponseFormatToUi(dateString: String): String{
        return try {
            koreanDateFormat.format(shortDateFormat.parse(dateString)!!)
        } catch (t: Throwable){
            ""
        }
    }

    fun getReportDateFormatString(curMillis: Long): String{
        return shortDateFormat.format(curMillis)
    }

    fun koreanToShortFormat(korean: String): String{
        return try {
            shortDateFormat.format(koreanDateFormat.parse(korean)!!)
        }catch (t: Throwable){
            ""
        }
    }

    fun curMsToShorFormat(): String{
        return shortDateFormat.format(System.currentTimeMillis())
    }

    fun getRelayStartTimeString(millis: Long): String{
        return koreanDateToMinuteFormat.format(millis)
    }

    fun longToHistoryFormat(longFormat: String): String{
        return try {
            historyDateFormat.format(longDateFormat.parse(longFormat)!!)
        }catch (t: Throwable){
            longFormat
        }
    }

    fun longToTimeFormat(long: String): String{
        return  try {
            timeFormat.format(longDateFormat.parse(long)!!)
        }catch (t: Throwable){
            long
        }
    }

    fun millisToLongFormat(millis: Long): String {
        return longDateFormat.format(millis)
    }
}