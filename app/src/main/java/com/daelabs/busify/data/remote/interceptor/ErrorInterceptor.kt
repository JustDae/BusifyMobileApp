package com.daelabs.busify.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!response.isSuccessful) {
            val code = response.code
            val url = request.url.toString()
            val method = request.method
            val errorBody = response.peekBody(1024 * 1024).string()
            Log.e("ErrorInterceptor", "API Error: $code | $method $url | Body: $errorBody")
        }

        return response
    }
}
