package com.gdd.presentation

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Only Use By Hilt Injection
 *
 * 반드시 Hilt 모듈의 @Inject로 주입해 사용하세요
 */
class PrefManager(context: Context) {
    private var pref: SharedPreferences =
        context.getSharedPreferences("relpl_preference", Activity.MODE_PRIVATE)

    /**
     * @param id
     *
     * id is not idInput but server user index id
     *
     * 유저의 입력값이 아닌 서버의 유저 인덱스 입니다.
     */
    fun setUserId(id: Long){
        pref.edit().apply {
            putLong(PREF_ACCESSTOKEN, id)
            apply()
        }
    }

    /**
     * @return if there is no id return -1
     */
    fun getUserId(): Long{
        return pref.getLong(PREF_USERID,-1)
    }

    fun setAccessToken(accessToken: String){
        pref.edit().apply {
            putString(PREF_ACCESSTOKEN, accessToken)
            apply()
        }
    }

    fun getAccessToken(): String? {
        return pref.getString(PREF_ACCESSTOKEN, null)
    }

    fun setRefreshToken(refreshToken: String){
        pref.edit().apply {
            putString(PREF_REFRESHTOKEN, refreshToken)
            apply()
        }
    }

    fun getRefreshToken(): String? {
        return pref.getString(PREF_REFRESHTOKEN, null)
    }

    fun deleteAll(){
        pref.edit().let {
            it.clear()
            it.commit()
        }
    }

    companion object{
        private const val PREF_USERID = "user_id"
        private const val PREF_ACCESSTOKEN = "access_token"
        private const val PREF_REFRESHTOKEN = "refresh_token"
    }
}