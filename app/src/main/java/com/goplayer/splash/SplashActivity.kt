package com.goplayer.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.goplayer.R
import com.goplayer.video_player.VideoListActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
                val i = Intent(this, VideoListActivity::class.java)
                startActivity(i)
                finish()
        }, 1000)

    }
}
