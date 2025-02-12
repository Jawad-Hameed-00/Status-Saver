package com.jawadjatoi.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jawadjatoi.statussaver.databinding.ActivityVideosPreviewBinding
import com.jawadjatoi.statussaver.models.MediaModel
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.views.adapters.VideoPreviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosPreview : AppCompatActivity() {

    private val TAG = "VideosPreview"
    private val binding by lazy { ActivityVideosPreviewBinding.inflate(layoutInflater) }
    private lateinit var adapter: VideoPreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val list = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as? ArrayList<MediaModel> ?: arrayListOf()
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)

            adapter = VideoPreviewAdapter(list, this@VideosPreview)
            videoRecyclerView.adapter = adapter

            // Snap to the current item
            PagerSnapHelper().attachToRecyclerView(videoRecyclerView)
            videoRecyclerView.scrollToPosition(scrollTo)

            setSupportActionBar(toolBar)
            toolBar.setNavigationOnClickListener {
                finish()
            }

            // Handle RecyclerView scrolling
            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Log.d(TAG, "RecyclerView is being dragged. Stopping all players.")
                        stopAllPlayers()
                    }
                }
            })
        }
    }

    // Stop all video players in RecyclerView
    private fun stopAllPlayers() {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until binding.videoRecyclerView.childCount) {
                val child = binding.videoRecyclerView.getChildAt(i)
                val viewHolder = binding.videoRecyclerView.getChildViewHolder(child)
                if (viewHolder is VideoPreviewAdapter.ViewHolder) {
                    viewHolder.releasePlayer()  // ✅ Fix: Using correct function
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAllPlayers()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllPlayers()
    }
}
