package com.example.wara.core.constants

import android.os.Build

object AppConstants {
    const val PRODUCTION_BASE_URL = "https://backendwara-kvnz.onrender.com/"
    const val DEFAULT_BASE_URL = PRODUCTION_BASE_URL

    const val DATABASE_NAME = "wara_local.db"
    const val PREFERENCES_NAME = "wara_preferences"
    const val TOKEN_KEY = "jwt_token"
    const val USERNAME_KEY = "auth_username"
    const val USER_ID_KEY = "auth_user_id"
    const val BASE_URL_KEY = "server_base_url"

    val isEmulator: Boolean
        get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.PRODUCT.contains("sdk")

    fun getDefaultBaseUrl(): String {
        return PRODUCTION_BASE_URL
    }
}
