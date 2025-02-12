package com.jawadjatoi.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.databinding.ItemVideoPreviewBinding
import com.jawadjatoi.statussaver.models.MediaModel
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.utils.isStatusExist
import com.jawadjatoi.statussaver.utils.saveStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoPreviewAdapter(private val list: ArrayList<MediaModel>, private val context: Context) :
    RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var player: ExoPlayer? = null

        fun bind(mediaModel: MediaModel) {
            binding.apply {
                // Initialize ExoPlayer
                player = ExoPlayer.Builder(context).build()
                playerView.player = player
                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)
                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true  // Auto-play video when loaded

                // Check if the status is already saved
                updateDownloadIcon(mediaModel)

                // Set click listeners
                tools.repost.setOnClickListener { repostMedia(mediaModel) }
                tools.share.setOnClickListener { shareMedia(mediaModel) }
                tools.download.setOnClickListener { saveVideo(mediaModel) }
            }
        }

        private fun updateDownloadIcon(mediaModel: MediaModel) {
            mediaModel.isDownloaded = context.isStatusExist(mediaModel.fileName)
            val downloadImage = if (mediaModel.isDownloaded) {
                R.drawable.ic_downloaded
            } else {
                R.drawable.ic_download
            }
            binding.tools.statusDownload.setImageResource(downloadImage)
        }

        private fun saveVideo(mediaModel: MediaModel) {

            CoroutineScope(Dispatchers.IO).launch {
                val isDownloaded = context.saveStatus(mediaModel)
                Handler(Looper.getMainLooper()).post {
                    if (isDownloaded) {
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        updateDownloadIcon(mediaModel)
                    } else {
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                        binding.tools.statusDownload.setImageResource(R.drawable.ic_download)
                    }
                }
            }
        }

        private fun repostMedia(mediaModel: MediaModel) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaModel.pathUri))
                putExtra(Intent.EXTRA_TEXT, "Check out this video status!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Repost via"))
            } else {
                Toast.makeText(context, "No app available to repost", Toast.LENGTH_SHORT).show()
            }
        }

        private fun shareMedia(mediaModel: MediaModel) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_STREAM, Uri.parse(mediaModel.pathUri))
                putExtra(Intent.EXTRA_TEXT, "Check out this video status!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Share via"))
            } else {
                Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show()
            }
        }

        fun releasePlayer() {
            player?.release()
            player = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemVideoPreviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.releasePlayer()
    }
}
