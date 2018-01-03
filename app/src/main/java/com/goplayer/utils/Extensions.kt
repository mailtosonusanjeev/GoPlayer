package com.goplayer.utils

import android.app.Activity
import android.widget.Toast

/**
 * Created by user on 12/21/2017.
 */
fun String.showToast(mActivity: Activity){

    mActivity.runOnUiThread {
        Toast.makeText(mActivity, this, Toast.LENGTH_SHORT)
    }
}