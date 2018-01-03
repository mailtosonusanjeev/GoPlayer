package com.goplayer.video_player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.goplayer.R
import com.goplayer.utils.MyApp
import com.goplayer.utils.showToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoPlayerActivity : AppCompatActivity() {

    private var simpleExoPlayerView: SimpleExoPlayerView? = null
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private val DEBUG_TAG = "GoPlayer"
    private var videoUri: Uri? = null
    private var videoName: String? = null
    private var resumePosition: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Remove notification bar
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)


        if (intent != null && intent.extras != null) {
            videoName = intent.extras.getString("VIDEO_NAME")
            videoUri = Uri.parse(videoName)

            MyApp.database?.videosDao()?.findByName(videoName)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe {
                        resumePosition = it.currentPosition

                        if (resumePosition != 0L) {
                            resumePosition.toString().showToast(this)
                        }
                    }
        }


        // 1. Create a default TrackSelector
        val mainHandler = Handler()
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        simpleExoPlayerView = findViewById(R.id.videoPlayer) as SimpleExoPlayerView
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext,
                trackSelector)
        simpleExoPlayerView!!.player = simpleExoPlayer

        // Measures bandwidth during playback. Can be null if not required.
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(applicationContext,
                Util.getUserAgent(applicationContext, "yourApplicationName"), defaultBandwidthMeter)
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        // This is the MediaSource representing the media to be played.
        val videoSource = ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null)
        // Prepare the player with the source.
        simpleExoPlayer!!.prepare(videoSource)
        simpleExoPlayer!!.playWhenReady = true

        /*simpleExoPlayer!!.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any) {
                Log.d(DEBUG_TAG, "cccccccccccccccccccc")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                Log.d(DEBUG_TAG, "aaaaaaaaaaaaaaaaaa")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.d(DEBUG_TAG, "sssssssssssssssss")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d(DEBUG_TAG, "ddddddddddddd")
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.d(DEBUG_TAG, "fffffffffffffffffff")
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Log.d(DEBUG_TAG, "hhhhhhhhhhhhhhhhh")
            }

            override fun onPositionDiscontinuity() {
                Log.d(DEBUG_TAG, "jjjjjjjjjjjjjjjjjjjj")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                Log.d(DEBUG_TAG, "xxxxxxxxxxxxxxxxxxxx")
            }
        })*/

        simpleExoPlayer!!.addVideoListener(object : SimpleExoPlayer.VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                Log.d(DEBUG_TAG, width.toString() + "," + height.toString() + "," + unappliedRotationDegrees + "," + pixelWidthHeightRatio)

                requestedOrientation = if (width > height) {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }

            }

            override fun onRenderedFirstFrame() {

            }

        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (simpleExoPlayer != null) {

            MyApp.database?.videosDao()?.findByName(videoName)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe {
                        it.currentPosition = simpleExoPlayer!!.currentPosition
                        Observable.fromCallable {
                            MyApp.database!!.videosDao().updateVideo(it)
                        }.subscribeOn(Schedulers.io()).subscribe()
                    }

            Log.d(DEBUG_TAG, "Position" + simpleExoPlayer!!.currentPosition)
            simpleExoPlayer!!.release()
        }
    }
}
