package com.jawadjatoi.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.databinding.ItemMediaBinding
import com.jawadjatoi.statussaver.models.MEDIA_TYPE_IMAGE
import com.jawadjatoi.statussaver.models.MediaModel
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.utils.saveStatus
import com.jawadjatoi.statussaver.views.activities.ImagesPreview
import com.jawadjatoi.statussaver.views.activities.VideosPreview

class MediaAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(statusImage)
                if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                    statusPlay.visibility = View.GONE
                }

                cardStatus.setOnClickListener {
                    if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                        // goto image preview activity
                        Intent().apply {
                            putExtra(Constants.MEDIA_LIST_KEY,list)
                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context,ImagesPreview::class.java)
                            context.startActivity(this)
                        }
                    } else {
                        // goto video preview activity
                        Intent().apply {
                            putExtra(Constants.MEDIA_LIST_KEY,list)
                            putExtra(Constants.MEDIA_SCROLL_KEY,layoutPosition)
                            setClass(context,VideosPreview::class.java)
                            context.startActivity(this)
                        }
                    }
                }



            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }
}