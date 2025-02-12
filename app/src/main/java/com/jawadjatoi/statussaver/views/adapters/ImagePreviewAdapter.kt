package com.jawadjatoi.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.databinding.ItemImagePreviewBinding
import com.jawadjatoi.statussaver.models.MEDIA_TYPE_IMAGE
import com.jawadjatoi.statussaver.models.MEDIA_TYPE_VIDEO
import com.jawadjatoi.statussaver.models.MediaModel
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.utils.isStatusExist
import com.jawadjatoi.statussaver.utils.saveStatus

class ImagePreviewAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                // Load the image using Glide
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(zoomableImageView)

                // Check if status is already saved
                mediaModel.isDownloaded = context.isStatusExist(mediaModel.fileName)

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                // Repost button click event
                tools.repost.setOnClickListener {
                    repostMedia(mediaModel)
                }

                // Share button click event
                tools.share.setOnClickListener {
                    shareMedia(mediaModel)
                }

                // Download button click event
                tools.download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Repost Media (Sharing it with the same app or another)
        private fun repostMedia(mediaModel: MediaModel) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                `package` = Constants.TYPE_WHATSAPP_MAIN
                type = when (mediaModel.type) {
                    MEDIA_TYPE_IMAGE -> "image/*"
                    MEDIA_TYPE_VIDEO -> "video/*"
                    else -> "image/*"
                }
                putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaModel.pathUri))
                putExtra(Intent.EXTRA_TEXT, "Check out this status!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Repost via"))
            } else {
                Toast.makeText(context, "No app available to repost", Toast.LENGTH_SHORT).show()
            }
        }

        // Share Media (Sharing it to another platform like social media)
        private fun shareMedia(mediaModel: MediaModel) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when (mediaModel.type) {
                    MEDIA_TYPE_IMAGE -> "image/*"
                    MEDIA_TYPE_VIDEO -> "video/*"
                    else -> "image/*"
                }
                putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaModel.pathUri))
                putExtra(Intent.EXTRA_TEXT, "Check out this status!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Share via"))
            } else {
                Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemImagePreviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
}
