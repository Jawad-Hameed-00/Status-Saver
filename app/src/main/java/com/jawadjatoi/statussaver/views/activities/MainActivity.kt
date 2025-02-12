package com.jawadjatoi.statussaver.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.databinding.ActivityMainBinding
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.utils.SharedPrefUtils
import com.jawadjatoi.statussaver.utils.replaceFragment
import com.jawadjatoi.statussaver.views.fragments.FragmentSettings
import com.jawadjatoi.statussaver.views.fragments.FragmentStatus

class MainActivity : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(activity)
        binding.apply {

            val fragmentWhatsAppStatus = FragmentStatus()
            val bundle = Bundle()
            bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
            replaceFragment(fragmentWhatsAppStatus, bundle)


            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_status -> {
                        // whatsapp status
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    }

                    R.id.menu_business_status -> {
                        // whatsapp business status
                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.FRAGMENT_TYPE_KEY,
                            Constants.TYPE_WHATSAPP_BUSINESS
                        )
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    }

                    R.id.menu_settings -> {
                        // settings
                        replaceFragment(FragmentSettings())
                    }
                }

                return@setOnItemSelectedListener true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}