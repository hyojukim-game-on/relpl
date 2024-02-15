package com.gdd.presentation.base

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val TAG = "PrefManager_Genseong"
/**
 * Only Use By Hilt Injection
 *
 * 반드시 Hilt 모듈의 @Inject로 주입해 사용하세요
 */
class PrefManager(context: Context) {
    private var pref: SharedPreferences =
        context.getSharedPreferences("relpl_preference", Activity.MODE_PRIVATE)

    init {
        Log.d(TAG, "Preference: ${this.pref.all}")
    }

    /**
     * @param id
     *
     * id is not idInput but server user index id
     *
     * 유저의 입력값이 아닌 서버의 유저 인덱스 입니다.
     */
    fun setUserId(id: Long){
        pref.edit().apply {
            putLong(PREF_USERID, id)
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

    fun setAutoLoginState(flag: Boolean){
        pref.edit().apply{
            putBoolean(PREF_AUTOLOGIN,flag)
            apply()
        }
    }

    fun getAutoLoginState(): Boolean {
        return pref.getBoolean(PREF_AUTOLOGIN,false)
    }

    fun setFcmToken(fcmToken: String){
        pref.edit().apply {
            putString(FCM_TOKEN, fcmToken)
            apply()
        }
    }

    fun getFcmToken(): String?{
        return pref.getString(FCM_TOKEN, null)
    }
    /**
     * must use in RelayingFragment
     */
    fun setRelayingMode(mode: RELAYING_MODE) {
        pref.edit().apply {
            putInt(PREF_RELAYING_MODE,mode.ordinal)
            apply()
        }
    }

    fun getRelayingMode(): RELAYING_MODE {
        return when(pref.getInt(PREF_RELAYING_MODE,-1)){
            RELAYING_MODE.PATH.ordinal->RELAYING_MODE.PATH
            RELAYING_MODE.DISTANCE.ordinal->RELAYING_MODE.DISTANCE
            RELAYING_MODE.NONE.ordinal->RELAYING_MODE.NONE
            else->RELAYING_MODE.NONE
        }
    }

    fun deleteAll(){
        val fcm = getFcmToken()
        pref.edit().let {
            it.clear()
            it.putString(FCM_TOKEN, fcm)
            it.commit()
        }
    }

    enum class RELAYING_MODE{
        DISTANCE, PATH, NONE
    }

    companion object{
        private const val PREF_USERID = "user_id"
        private const val PREF_AUTOLOGIN = "auto_login"
        private const val PREF_ACCESSTOKEN = "access_token"
        private const val PREF_REFRESHTOKEN = "refresh_token"
        private const val FCM_TOKEN = "fcm_token"
        private const val FCM_NEW_TOKEN = "fcm_new_token"
        private const val PREF_RELAYING_MODE = "relaying_mode"
    }
}