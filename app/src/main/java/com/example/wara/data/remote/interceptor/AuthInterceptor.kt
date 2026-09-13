package com.example.wara.data.remote.interceptor

import com.example.wara.data.local.datastore.SessionDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip token injection for public auth endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("/api/auth/login") || path.contains("/api/auth/register")) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            sessionDataStore.tokenFlow.firstOrNull()
        }

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
