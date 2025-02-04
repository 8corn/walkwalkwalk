package com.example.walkwalkwalk.auth

import android.app.Application
import com.example.walkwalkwalk.R
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.Kakao_native_app_key))
    }
}