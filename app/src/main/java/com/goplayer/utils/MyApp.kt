package com.goplayer.utils

import android.app.Application
import android.arch.persistence.room.Room
import android.graphics.Typeface
import android.support.v4.util.LruCache
import com.goplayer.AppDatabase

/**
 * Created by user on 9/18/2017.
 */
class MyApp : Application() {

    companion object {
        var database: AppDatabase? = null
        var mTypeFaceCache: LruCache<String, Typeface>? = null

        fun getTypeFaceCache(): LruCache<String, Typeface>{
            if (mTypeFaceCache == null){
                mTypeFaceCache = LruCache<String, Typeface>(12)
            }
            return mTypeFaceCache as LruCache<String, Typeface>
        }

    }
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "videos-database").build()
    }

}