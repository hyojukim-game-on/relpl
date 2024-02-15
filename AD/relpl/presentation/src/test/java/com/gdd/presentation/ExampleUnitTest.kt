package com.gdd.presentation

import com.naver.maps.geometry.LatLng
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.sqrt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {
    @Test
    fun phoneFormat(){
        val origin = "01012345678"
        val formatted = "010 1234 5678"
        val format = "${origin.substring(0, 3)} ${origin.substring(3,7)} ${origin.substring(7,11)}"
        assertEquals(format, formatted)
    }

    @Test
    fun distanceTest(){
        // 36.102858, 128.419107
        // 36.102858, 128.420107
        // 36.103121, 128.420521

        val point1 = LatLng(36.102858, 128.419107)
        val point2 = LatLng(36.102858, 128.420107)
        val target = LatLng(36.103121, 128.420521)

        println(calculateDistanceToLine(point1,point2,target))
    }

    fun calculateDistanceToLine(marker1: LatLng, marker2: LatLng, myPosition: LatLng): Double {
        val a = marker1.distanceTo(myPosition)
        val b = marker2.distanceTo(myPosition)
        val c = marker1.distanceTo(marker2)
        val s = (a+b+c) / 2.0

        return (2* sqrt(s*(s-a)*(s-b)*(s-c))) / c
    }

    @Test
    fun progressCalc(){
        val total = 1420
        val remain = 832

        println(100 - ((remain.toDouble() / total.toDouble())*100).toInt())
    }

}