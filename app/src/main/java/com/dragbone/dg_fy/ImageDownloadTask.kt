package com.dragbone.dg_fy

import android.graphics.Bitmap
import android.os.AsyncTask
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import java.net.URL


class ImageDownloadTask(private val imageView: ImageView, private val url: URL) : AsyncTask<Void, Void, Bitmap?>() {
    override fun doInBackground(vararg params: Void?): Bitmap? {
        return try {
            val inputStream = url.openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
            null
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        imageView.setImageBitmap(result ?: return)
    }
}