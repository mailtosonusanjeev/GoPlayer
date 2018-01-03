package com.goplayer.video_player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.goplayer.R
import com.goplayer.room_db.Videos
import com.goplayer.utils.MyApp
import com.goplayer.utils.PermissionHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video_list.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.view.ViewAnimationUtils
import com.goplayer.utils.hideByCircularAnimation
import com.goplayer.utils.showByCircularAnimation


class VideoListActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private var videoList: ArrayList<String>? = null
    private var videosRCV: RecyclerView? = null
    private var videosListAdapter: VideosListAdapter? = null
    private var permissionHelper = PermissionHelper()
    private val REQUEST_STORAGE_CODE = 200
    private val DEBUG_TAG = "VideoList"
    private var videosFolders: ArrayList<String>? = null
    private var videosMap: HashMap<String, List<String>>? = null
    private var isInside: Boolean = false
    private var defaultPosition: Long = 0
    private var folderPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        initToolbar()

        videoList = ArrayList()
        videosFolders = ArrayList()
        videosMap = HashMap()
        videosRCV = findViewById(R.id.videosRCV)

        swipeLayout.setOnRefreshListener(this)
        swipeLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorAccent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionHelper.requestStoragePermission(this)) {
                Log.d(DEBUG_TAG, "Permissions already granted")

                initialiseRCV()
            }
        } else {
            Log.d(DEBUG_TAG, "Permissions not required")
            initialiseRCV()
        }

        backIV.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.backIV -> searchRL.hideByCircularAnimation()
        }
    }

    private fun initToolbar() {
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onRefresh() {
        if (isInside) {
            if (folderPosition != -1) {
                resetToVideosList(folderPosition)
            }
        } else {
            initialiseRCV()
        }
    }

    private fun initialiseRCV() {
        Log.d(DEBUG_TAG, "RCV init")
        videosFolders?.clear()
        val f = File(Environment.getExternalStorageDirectory().toString())
        val files = f.listFiles()
        files.filter { it.isDirectory }
                .forEach {
                    // is directory
                    val selection = MediaStore.Video.Media.DATA + " like?"
                    val selectionArgs = arrayOf("%" + it.absoluteFile.name + "%")
                    val projection = arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
                    Log.d(DEBUG_TAG, it.absoluteFile.name)
                    val videocursor = applicationContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            projection, selection, selectionArgs, MediaStore.Video.Media.DATE_TAKEN + " DESC")
                    var insideList: ArrayList<String> = ArrayList()
                    try {
                        if (videocursor != null && videocursor.count > 0) {
                            videocursor.moveToFirst()
                            do {
                                insideList.add(videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                                Log.d("Videos", videocursor.getString(videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                            } while (videocursor.moveToNext())
                            videocursor.close()
                            if (insideList != null && insideList!!.isNotEmpty()) {
                                videosFolders?.add(it.absoluteFile.name)
                                videosMap?.put(it.absoluteFile.name, insideList)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

        //Iterate through map and save video names in database
        iterateAndSaveVideos()

        if (swipeLayout.isRefreshing) {
            swipeLayout.isRefreshing = false
        }

        if (videosFolders != null && videosFolders!!.isNotEmpty()) {
            videosListAdapter = VideosListAdapter(this, isInside, videosFolders!!, object : VideosListAdapter.OnFolderSelected {
                override fun onFolderSelected(position: Int) {
                    Log.d(DEBUG_TAG, "Clicked")
                    if (!isInside) {
                        isInside = true
                        resetToVideosList(position)
                    }
                }
            })
            val mLayoutManager = LinearLayoutManager(this)
            videosRCV!!.layoutManager = mLayoutManager
            videosRCV!!.itemAnimator = DefaultItemAnimator()
            videosRCV!!.adapter = videosListAdapter
        }
    }

    private fun iterateAndSaveVideos() {

        if (videosMap != null && videosMap!!.isNotEmpty()) {

            var iterator = videosMap!!.entries.iterator()
            while (iterator.hasNext()) {
                var pair = iterator.next()
                if (pair.value != null && pair.value.isNotEmpty()) {
                    println("DB : " + pair.value.size)
                    pair.value
                            .forEach {
                                println("New")
                                var newVideo = Videos(uId = null, videoName = it, currentPosition = defaultPosition)
                                Observable.fromCallable {
                                    MyApp.database?.videosDao()?.insert(newVideo)
                                }.subscribeOn(Schedulers.io())
                                        ?.subscribe()
                            }
                }
            }
        }
        getAllFromDB()
    }


    private fun getAllFromDB() {
        Log.d(DEBUG_TAG, "GetALL")
        MyApp.database?.videosDao()?.getAllVideos()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.take(1)
                ?.subscribe {
                    println("Result : " + it)
                }
    }

    private fun resetToVideosList(position: Int) {
        Log.d(DEBUG_TAG, "Reseting RCV")
        folderPosition = position
        var newList: ArrayList<String> = ArrayList()
        newList.addAll(videosMap?.get(videosFolders?.get(position))!!)
        videosListAdapter = VideosListAdapter(this, isInside, newList!!, object : VideosListAdapter.OnFolderSelected {
            override fun onFolderSelected(position: Int) {
                Log.d(DEBUG_TAG, "Second click")
                startActivity(Intent(applicationContext, VideoPlayerActivity::class.java)
                        .putExtra("VIDEO_NAME", newList[position]))
            }

        })

        if (swipeLayout.isRefreshing) {
            swipeLayout.isRefreshing = false
        }
        videosRCV!!.adapter = videosListAdapter
        videosListAdapter!!.notifyDataSetChanged()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            REQUEST_STORAGE_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(DEBUG_TAG, "Permissions granted!")
                initialiseRCV()

            } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Log.d(DEBUG_TAG, "Permissions denied by user!!!")
            }

        }
    }


    override fun onBackPressed() {
        if (isInside) {
            resetToInitialList()
            return
        } else {
            super.onBackPressed()
            return
        }
    }

    private fun resetToInitialList() {
        isInside = false
        if (videosFolders != null && videosFolders!!.isNotEmpty()) {
            videosListAdapter = VideosListAdapter(this, isInside, videosFolders!!, object : VideosListAdapter.OnFolderSelected {
                override fun onFolderSelected(position: Int) {
                    Log.d(DEBUG_TAG, "Clicked")
                    if (!isInside) {
                        isInside = true
                        resetToVideosList(position)
                    }
                }
            })
            val mLayoutManager = LinearLayoutManager(this)
            videosRCV!!.layoutManager = mLayoutManager
            videosRCV!!.itemAnimator = DefaultItemAnimator()
            videosRCV!!.adapter = videosListAdapter
        }
    }


    private val allMedia: ArrayList<String>?
        get() {
            val videoItemHashSet = HashSet<String>()
            val projection = arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
            val cursor = applicationContext.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
            try {
                cursor!!.moveToFirst()
                do {
                    videoItemHashSet.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)))
                } while (cursor.moveToNext())

                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ArrayList(videoItemHashSet)
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home ->
                if (isInside) {
                    resetToInitialList()
                } else {
                    finish()
                }

            R.id.actionSearch -> doSearchOnList()
        }
        return super.onOptionsItemSelected(item)

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doSearchOnList() {
        searchRL.showByCircularAnimation()
        if(!isInside){

            searchET.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    var searchList: ArrayList<String> = ArrayList()
                    if (videosFolders != null && videosFolders!!.isNotEmpty()){
                        videosFolders!!.filterTo(searchList) { it.contains(p0.toString()) }
                    }
                }

            })
        }
    }

}
