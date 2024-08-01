package com.vimal.margh.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.PlaybackStateCompat
import com.vimal.margh.util.Constant.ACTION_STATE

class PlayerBroadcast(private val updatePlayPauseIcon: (Int) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getIntExtra(ACTION_STATE, PlaybackStateCompat.STATE_PAUSED)
        updatePlayPauseIcon(state)
    }
}
