package com.hex.evegate.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.hex.evegate.AppEx
import com.hex.evegate.R
import com.hex.evegate.radio.RadioManager

class PlayPauseWidget : AppWidgetProvider() {
    private val clickListener = "WidgetClickListener"
    private val radioManager = RadioManager.with(AppEx.instance)
    private lateinit var streamURL: String

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (context != null && appWidgetManager != null) {
            val widget = ComponentName(context, PlayPauseWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(widget)
            for (id in allWidgetIds) {
                val remoteViews = RemoteViews(context.packageName, R.layout.widget)
                remoteViews.setOnClickPendingIntent(
                        R.id.widget_ivPlayPause, getPendingSelfIntent(context, clickListener))
                streamURL = if (AppEx.instance!!.shpHQ) {
                    AppEx.instance!!.resources.getString(R.string.evegateradio_high)
                } else {
                    AppEx.instance!!.resources.getString(R.string.evegateradio_low)
                }
                if (!radioManager.isPlaying) {
                    radioManager.bind()
                }
                remoteViews.setInt(R.id.widget_ivPlayPause, "setImageResource",
                        if (RadioManager.with(AppEx.instance).isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                })
                appWidgetManager.updateAppWidget(id, remoteViews)
            }
        }
    }

    private fun getPendingSelfIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (context != null) {
            streamURL = if (AppEx.instance!!.shpHQ) {
                AppEx.instance!!.resources.getString(R.string.evegateradio_high)
            } else {
                AppEx.instance!!.resources.getString(R.string.evegateradio_low)
            }

            if (clickListener == intent?.action) {
                radioManager.playOrPause(streamURL)
            }

            val remoteViews = RemoteViews(context.packageName, R.layout.widget)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widget = ComponentName(context, PlayPauseWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(widget)

            for (id in allWidgetIds) {
                remoteViews.setInt(R.id.widget_ivPlayPause, "setImageResource", if (radioManager.isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                })
                appWidgetManager.updateAppWidget(id, remoteViews)
            }
        }
    }
}