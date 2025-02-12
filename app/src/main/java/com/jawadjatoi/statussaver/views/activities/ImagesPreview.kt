package com.jawadjatoi.statussaver.views.activities

import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.models.MediaModel
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.views.adapters.ImagePreviewAdapter
import com.jawadjatoi.statussaver.databinding.ActivityImagesPreviewBinding

class ImagesPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: ImagePreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            adapter = ImagePreviewAdapter(list, activity)
            imagesViewPager.adapter = adapter
            imagesViewPager.currentItem = scrollTo


            setSupportActionBar(toolBar)
            toolBar.setNavigationOnClickListener {
                finish()
            }
        }

    }
}