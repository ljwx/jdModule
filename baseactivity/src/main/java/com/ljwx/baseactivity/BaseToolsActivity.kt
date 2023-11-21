package com.ljwx.baseactivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import com.ljwx.baseapp.debug.DebugUtils

open class BaseToolsActivity : AppCompatActivity() {

    private var sensorEventListener: SensorEventListener? = null

    private val screenStatusReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_USER_PRESENT -> {
                    }

                    Intent.ACTION_SCREEN_OFF -> {
                    }

                    Intent.ACTION_SCREEN_ON -> {
                    }
                }
            }
        }
    }

    open fun logCheckDebug() {
        logCheckDebugEx(DebugUtils.isDebug())
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}