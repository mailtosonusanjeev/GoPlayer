package com.goplayer

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.goplayer.room_db.VideoDao
import com.goplayer.room_db.Videos

/**
 * Created by user on 9/18/2017.
 */
@Database(entities = arrayOf(Videos::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videosDao(): VideoDao

}