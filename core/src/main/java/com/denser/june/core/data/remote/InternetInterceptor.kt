package com.denser.june.core.data.remote

import com.denser.june.core.domain.preferences.PrivacyPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class InternetInterceptor(
    private val privacyPreferences: PrivacyPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val isInternetAllowed = runBlocking {
            privacyPreferences.getIsInternetAllowedFlow().first()
        }
        if (!isInternetAllowed) {
            throw IOException("Internet access restricted in app settings")
        }
        return chain.proceed(chain.request())
    }
}
