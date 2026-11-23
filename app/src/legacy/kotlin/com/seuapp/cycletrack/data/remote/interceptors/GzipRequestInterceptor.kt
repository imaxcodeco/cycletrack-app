package com.seuapp.cycletrack.data.remote.interceptors

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.GzipSink
import java.io.IOException

/**
 * Interceptor para compressão GZIP do body da requisição.
 * Clean-room - reduz payload em até 80%.
 */
class GzipRequestInterceptor @Inject constructor() : Interceptor {

    private val JSON = MediaType.get("application/json; charset=utf-8")
    private val FORM = MediaType.get("application/x-www-form-urlencoded")

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Só comprime se tiver body e for JSON ou Form
        if (originalRequest.body == null) {
            return chain.proceed(originalRequest)
        }

        val contentType = originalRequest.body?.contentType()
        if (contentType != JSON && contentType != FORM) {
            return chain.proceed(originalRequest)
        }

        // Evita comprimir se já tiver encoding
        if (originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest)
        }

        // Comprime o body
        val gzippedBody = gzip(originalRequest.body!!)
        
        val compressedRequest = originalRequest.newBuilder()
            .header("Content-Encoding", "gzip")
            .header("Content-Length", gzippedBody.contentLength().toString())
            .method(originalRequest.method, gzippedBody)
            .build()

        return chain.proceed(compressedRequest)
    }

    private fun gzip(body: RequestBody): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? = body.contentType()

            override fun writeTo(sink: okio.BufferedSink) {
                val buffer = Buffer()
                body.writeTo(buffer)
                
                val gzipSink = GzipSink(sink)
                gzipSink.writeAll(buffer)
                gzipSink.close()
            }
        }
    }
}