package com.jeffcarey.android.pcapblog.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.net.URL

object PhotoDownloader {
    suspend fun getPhoto(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val input: InputStream = url.openConnection().getInputStream()
        val bitmap: Bitmap = BitmapFactory.decodeStream(input)
        return bitmap
    }
}