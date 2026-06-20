package com.denser.june.core.data.remote

import com.jayway.jsonpath.JsonPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ItunesFetcher(private val client: OkHttpClient) {

    suspend fun fetchPreviewUrl(trackId: String): String? = withContext(Dispatchers.IO) {
        val url = "https://itunes.apple.com/lookup?id=$trackId"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null

                val body = response.body?.string() ?: return@withContext null
                try {
                    JsonPath.read<String>(body, "$.results[0].previewUrl")
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
