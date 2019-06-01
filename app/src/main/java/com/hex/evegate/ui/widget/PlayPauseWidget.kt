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
import com.hex.evegate.radio.PlaybackStatus
import com.hex.evegate.radio.RadioManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class PlayPauseWidget : AppWidgetProvider() {
    private val clickListener = "WidgetClickListener"
    private val radioManager = RadioManager.with(AppEx.instance)
    private lateinit var streamURL: String
    private var ctx: Context? = null

    override fun onEnabled(context: Context?) {
        EventBus.getDefault().register(this)

        super.onEnabled(context)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        EventBus.getDefault().register(this)

        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onDisabled(context: Context?) {
        EventBus.getDefault().unregister(this)

        super.onDisabled(context)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        EventBus.getDefault().unregister(this)

        super.onDeleted(context, appWidgetIds)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (context != null && appWidgetManager != null) {
            ctx = context
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
            ctx = context
            streamURL = if (AppEx.instance!!.shpHQ) {
                AppEx.instance!!.resources.getString(R.string.evegateradio_high)
            } else {
                AppEx.instance!!.resources.getString(R.string.evegateradio_low)
            }

            if (clickListener == intent?.action) {
                radioManager.playOrPause(streamURL)
            }
        }
    }


    @Subscribe
    fun onEvent(status: String) {
        val remoteViews = RemoteViews(ctx?.packageName, R.layout.widget)
        if (status == PlaybackStatus.PLAYING) {
            remoteViews.setInt(R.id.widget_ivPlayPause, "setImageResource", android.R.drawable.ic_media_pause)
        } else {
            remoteViews.setInt(R.id.widget_ivPlayPause, "setImageResource", android.R.drawable.ic_media_play)
        }
        when (status) {
            PlaybackStatus.LOADING -> {
                remoteViews.setInt(R.id.widget_ivPlayPause, "setImageResource", R.drawable.ic_cloud_download_white_24dp)
            }
            PlaybackStatus.ERROR -> {}
        }
        ctx?.let {
            val appWidgetManager = AppWidgetManager.getInstance(ctx)
            val widget = ComponentName(ctx!!, PlayPauseWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(widget)
            for (id in allWidgetIds) {
                appWidgetManager.updateAppWidget(id, remoteViews)
            }
        }
    }
}