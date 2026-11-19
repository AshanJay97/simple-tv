package com.simpletv.player

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Tiny dependency-free async image loader with an in-memory cache.
 * Keeps the app light (no Glide/Picasso).
 */
object ImageLoader {
    private val cache = object : LruCache<String, Bitmap>(6 * 1024 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun load(url: String?, into: ImageView, placeholder: Int) {
        into.setImageResource(placeholder)
        into.tag = url
        if (url.isNullOrBlank()) return

        cache.get(url)?.let {
            into.setImageBitmap(it)
            return
        }

        scope.launch {
            try {
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    instanceFollowRedirects = true
                }
                val bmp = conn.inputStream.use { BitmapFactory.decodeStream(it) }
                conn.disconnect()
                if (bmp != null) {
                    cache.put(url, bmp)
                    withContext(Dispatchers.Main) {
                        if (into.tag == url) into.setImageBitmap(bmp)
                    }
                }
            } catch (_: Exception) {
                // Leave the placeholder on any failure.
            }
        }
    }
}
