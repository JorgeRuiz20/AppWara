package com.example.wara.data.remote.interceptor

import com.example.wara.core.constants.AppConstants
import com.example.wara.data.local.datastore.SessionDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val baseUrlString = runBlocking {
            sessionDataStore.baseUrlFlow.firstOrNull() ?: AppConstants.getDefaultBaseUrl()
        }

        val targetUrl = baseUrlString.toHttpUrlOrNull()
        if (targetUrl != null) {
            val newUrl = request.url.newBuilder()
                .scheme(targetUrl.scheme)
                .host(targetUrl.host)
                .port(targetUrl.port)
                .build()
            request = request.newBuilder().url(newUrl).build()
        }

        return chain.proceed(request)
    }
}
