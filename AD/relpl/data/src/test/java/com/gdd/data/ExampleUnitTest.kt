package com.gdd.data

import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun retrofit_test_adapter() {
        runBlocking {
            val result = RetrofitManager.getAgaService().getUserInfoAdapter("cjfwn96")
            println(result.toString())
            assertEquals(true,result.isSuccess)
            assertEquals("cjfwn96",result.getOrNull()?.data?.userId?:"")
        }
    }

    @Test
    fun retrofit_test() {
        runBlocking {
            val result = RetrofitManager.getAgaService().getUserInfo("cjfwn96")
            println(result.toString())
            assertEquals(true,result.isSuccessful)
            assertEquals("cjfwn96",result.body()?.data?.userId)
        }
    }
}