package com.goplayer.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.opengl.Visibility
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_video_list.*

/**
 * Created by user on 12/21/2017.
 */
fun String.showToast(mActivity: Activity) {

    mActivity.runOnUiThread {
        Toast.makeText(mActivity, this, Toast.LENGTH_SHORT)
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun RelativeLayout.showByCircularAnimation() {
    // get the center for the clipping circle
    val cx: Int = this.width
    val cy: Int = this.height / 2
    // get the final radius for the clipping circle
    val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

    // create the animator for this view (the start radius is zero)
    val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)

    // make the view visible and start the animation
    this.visibility = View.VISIBLE
    anim.start()
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun RelativeLayout.hideByCircularAnimation() {

    val view: View = this
    val cx: Int = this.width
    val cy: Int = this.height / 2

    val initialRadius: Float = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

    val anim: Animator = ViewAnimationUtils.createCircularReveal(this, cx, cy, initialRadius, 0F)

    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            view.visibility = View.INVISIBLE
        }
    })
    anim.start()
}