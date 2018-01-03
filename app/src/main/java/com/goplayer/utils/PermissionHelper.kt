package com.goplayer.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import java.util.*

/**
 * Created by user on 9/13/2017.
 */
class PermissionHelper {

    companion object {
        val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val REQUEST_STORAGE_CODE = 200
        val DEBUG_TAG = "PermissionHelper"
    }

    fun isRequestPermissionNeeded(): Boolean{

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestStoragePermission(activity: Activity): Boolean {

        if(!isRequestPermissionNeeded()){
           return true
        }else{
            val permissions = ArrayList<String>()
            permissions.add(PermissionHelper.STORAGE_PERMISSION)
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.d(DEBUG_TAG, "Permissions were denied earlier")
                    return false
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    Log.d(DEBUG_TAG, "Request permission")
                    // No explanation needed, we can request the permission.
                    var mStringArray = arrayOfNulls<String>(permissions.size)
                    mStringArray = permissions.toTypedArray()
                    ActivityCompat.requestPermissions(activity,
                            mStringArray, REQUEST_STORAGE_CODE)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                    return false
                }
            }else{
                Log.d(DEBUG_TAG, "Permission already granted!")
                return true
            }
        }
    }
}