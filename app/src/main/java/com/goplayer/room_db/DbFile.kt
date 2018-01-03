package com.goplayer.room_db

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by user on 9/15/2017.
 */

@Entity(tableName = "videos")
data class Videos(@PrimaryKey(autoGenerate = true) var uId: Int? = null,
             @ColumnInfo(name = "video_name") var videoName: String? = "",
             @ColumnInfo(name = "current_position") var currentPosition: Long? = 0)

@Dao
interface VideoDao{

    @Query("Select * From videos")
    fun getAllVideos(): Flowable<List<Videos>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllVideos(videoList: List<Videos>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(video: Videos)

    @Query("SELECT * FROM videos where video_name LIKE  :arg0")
    fun findByName(videoName: String?): Flowable<Videos>

    @Update
    fun updateVideo(video: Videos)

}

