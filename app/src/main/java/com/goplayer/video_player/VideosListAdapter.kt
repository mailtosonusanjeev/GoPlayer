package com.goplayer.video_player

import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.goplayer.R
import java.io.File


/**
 * Created by user on 9/12/2017.
 */

class VideosListAdapter(private val context: Context, private val isInside: Boolean, private val videosList: List<String>,
                        private val folderSelected: OnFolderSelected)
    : RecyclerView.Adapter<VideosListAdapter.VideoViewHolder>() {

    companion object {
        var onFolderSelect: OnFolderSelected? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosListAdapter.VideoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_card, parent, false)

        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideosListAdapter.VideoViewHolder, position: Int) {

        onFolderSelect = folderSelected

        val file = File(videosList[position])

        holder.videoNameTV.text = file.absoluteFile.name

        if(isInside){
            /*val thumb = ThumbnailUtils.createVideoThumbnail(videosList[position],
                    MediaStore.Images.Thumbnails.MINI_KIND)
            holder.videoThumbIV.setImageBitmap(thumb)*/
            Glide.with(context).load(Uri.fromFile(file)).into(holder.videoThumbIV)
        }else{
            holder.videoThumbIV.setImageResource(R.drawable.ic_folder)
        }

        holder.videoCard.setOnClickListener {
            if(onFolderSelect != null){
                onFolderSelect?.onFolderSelected(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return videosList.size
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val videoNameTV: TextView = itemView.findViewById(R.id.videoNameTV) as TextView
        val videoCard: CardView = itemView.findViewById(R.id.videoCard) as CardView
        val videoThumbIV: ImageView = itemView.findViewById(R.id.videoThumbIV) as ImageView
    }

    interface OnFolderSelected{

        fun onFolderSelected(position: Int)
    }
}
