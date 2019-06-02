package com.hex.evegate.radio

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadata
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hex.evegate.R

import org.greenrobot.eventbus.EventBus
import java.util.*

class RadioService : Service(), Player.EventListener, AudioManager.OnAudioFocusChangeListener {
    private val iBinder = LocalBinder()
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    lateinit var exoPlayer: SimpleExoPlayer
    var mediaSession: MediaSessionCompat? = null
        private set
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var onGoingCall = false
    private var telephonyManager: TelephonyManager? = null
    private var wifiLock: WifiManager.WifiLock? = null
    private var audioManager: AudioManager? = null
    private var notificationManager: MediaNotificationManager? = null
    var status: String? = null
        private set
    private var strAppName: String? = null
    private var strLiveBroadcast: String? = null
    private var streamUrl: String? = null

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            pause()
        }
    }

    private val phoneStateListener = object : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
                if (!isPlaying) return
                onGoingCall = true
                stop()
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (!onGoingCall) return
                onGoingCall = false
                resume()
            }
        }
    }

    val isPlaying: Boolean
        get() = this.status == PlaybackStatus.PLAYING

    private val userAgent: String
        get() = Util.getUserAgent(this, javaClass.simpleName)

    inner class LocalBinder : Binder() {
        val service: RadioService
            get() = this@RadioService
    }

    override fun onBind(intent: Intent): IBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        strAppName = resources.getString(R.string.app_name)
        strLiveBroadcast = resources.getString(R.string.live_broadcast)
        onGoingCall = false
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = MediaNotificationManager(this)
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSession = MediaSessionCompat(this, javaClass.simpleName)
            transportControls = mediaSession!!.controller.transportControls
            mediaSession!!.isActive = true
            mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            mediaSession!!.setMetadata(MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, "...")
                    .putString(MediaMetadata.METADATA_KEY_ALBUM, strAppName)
                    .putString(MediaMetadata.METADATA_KEY_TITLE, strLiveBroadcast)
                    .build())
            mediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPause() {
                    super.onPause()
                    pause()
                }

                override fun onStop() {
                    super.onStop()
                    stop()
                    notificationManager?.cancelNotify()
                }

                override fun onPlay() {
                    super.onPlay()
                    resume()
                }
            })
        }
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector)
        exoPlayer.addListener(this)
        registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        status = PlaybackStatus.IDLE
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (TextUtils.isEmpty(action))
            return Service.START_NOT_STICKY
        val result = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop()
            return Service.START_NOT_STICKY
        }
        if (action!!.equals(ACTION_PLAY, ignoreCase = true)) {
            resume()
        } else if (action.equals(ACTION_PAUSE, ignoreCase = true)) {
            pause()
        } else if (action.equals(ACTION_STOP, ignoreCase = true)) {
            stop()
        }
        return Service.START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        if (status == PlaybackStatus.IDLE)
            stopSelf()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent) {}

    override fun onDestroy() {
        pause()
        exoPlayer.release()
        exoPlayer.removeListener(this)
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        notificationManager?.cancelNotify()
        if (mediaSession?.isActive == true) {
            mediaSession!!.release()
        }
        unregisterReceiver(becomingNoisyReceiver)
        super.onDestroy()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = 0.8f
                resume()
            }
            AudioManager.AUDIOFOCUS_LOSS -> stop()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (isPlaying) pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> if (isPlaying)
                exoPlayer.volume = 0.1f
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        status = when (playbackState) {
            Player.STATE_BUFFERING -> PlaybackStatus.LOADING
            Player.STATE_ENDED -> PlaybackStatus.STOPPED
            Player.STATE_IDLE -> PlaybackStatus.IDLE
            Player.STATE_READY -> if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            else -> PlaybackStatus.IDLE
        }
        if (status != PlaybackStatus.IDLE)
            notificationManager?.startNotify(status!!)
        EventBus.getDefault().post(status)
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerError(error: ExoPlaybackException) {
        EventBus.getDefault().post(PlaybackStatus.ERROR)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

    override fun onSeekProcessed() {}

    fun play(streamUrl: String) {
        this.streamUrl = streamUrl
        if (wifiLock != null && !wifiLock!!.isHeld) {
            wifiLock!!.acquire()
        }
        val dataSourceFactory = DefaultDataSourceFactory(this, userAgent, BANDWIDTH_METER)

        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(DefaultExtractorsFactory())
                .createMediaSource(Uri.parse(streamUrl))
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = true
    }

    fun resume() {
        if (streamUrl != null)
            play(streamUrl!!)
    }

    fun pause() {
        exoPlayer.playWhenReady = false
        audioManager?.abandonAudioFocus(this)
        wifiLockRelease()
    }

    fun stop() {
        exoPlayer.stop()
        audioManager?.abandonAudioFocus(this)
        wifiLockRelease()
        notificationManager?.cancelNotify()
    }

    fun playOrPause(url: String) {
        if (streamUrl != null && streamUrl == url) {
            if (!isPlaying) {
                play(streamUrl!!)
            } else {
                pause()
            }
        } else {
            if (isPlaying) {
                pause()
            }
            play(url)
        }
    }

    private fun wifiLockRelease() {
        if (wifiLock?.isHeld == true) {
            wifiLock!!.release()
        }
    }

    fun onTrackUpdated(song: String, artist: String) {
        status?.let {
            notificationManager?.onTrackUpdated(song, artist, it)
        }
    }

    companion object {
        const val ACTION_PLAY = "com.hex.evegate.radio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.hex.evegate.radio.ACTION_PAUSE"
        const val ACTION_STOP = "com.hex.evegate.radio.ACTION_STOP"
    }
}
