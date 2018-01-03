package com.goplayer.custom_views

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.goplayer.utils.MyApp

/**
 * Created by user on 12/21/2017.
 */
class BoldTextView(context: Context?, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    init {
        if (attrs != null) {
            try {
                var mTypeFace = MyApp.getTypeFaceCache().get("QuickSand-Bold")
                if (mTypeFace == null) {
                    mTypeFace = Typeface.createFromAsset(getContext().assets,
                            String.format("fonts/%s.ttf", "QuickSand-Bold"))

                    // Cache the typeface
                    MyApp.getTypeFaceCache().put("QuickSand-Bold", mTypeFace)
                }
                typeface = mTypeFace

                // Note: This flag is required for proper typeface rendering
                paintFlags = paintFlags or Paint.SUBPIXEL_TEXT_FLAG

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}