package com.gdd.presentation

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun phoneFormat(){
        val origin = "01012345678"
        val formatted = "010 1234 5678"
        val format = "${origin.substring(0, 3)} ${origin.substring(3,7)} ${origin.substring(7,11)}"
        assertEquals(format, formatted)
    }
}