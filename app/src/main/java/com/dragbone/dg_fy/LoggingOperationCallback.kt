package com.dragbone.dg_fy

import android.util.Log
import com.spotify.sdk.android.player.Error
import com.spotify.sdk.android.player.Player

class LoggingOperationCallback(val tag: String) : Player.OperationCallback {
    override fun onError(e: Error?) {
        Log.w("OperationCallback:$tag", "Error: $e")
    }

    override fun onSuccess() {
        Log.i("OperationCallback:$tag", "Success")
    }
}