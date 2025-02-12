package com.jawadjatoi.statussaver.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.data.StatusRepo
import com.jawadjatoi.statussaver.databinding.FragmentStatusBinding
import com.jawadjatoi.statussaver.utils.Constants
import com.jawadjatoi.statussaver.utils.SharedPrefKeys
import com.jawadjatoi.statussaver.utils.SharedPrefUtils
import com.jawadjatoi.statussaver.utils.getFolderPermissions
import com.jawadjatoi.statussaver.utils.isAppInstalled
import com.jawadjatoi.statussaver.viewmodels.StatusViewModel
import com.jawadjatoi.statussaver.viewmodels.factories.StatusViewModelFactory
import com.jawadjatoi.statussaver.views.adapters.MediaViewPagerAdapter

class FragmentStatus : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!  // Safe binding access
    private lateinit var type: String
    private val WHATSAPP_REQUEST_CODE = 101
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102
    private val viewPagerTitles = listOf("Images", "Videos")
    private lateinit var viewModel: StatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            type = it.getString(Constants.FRAGMENT_TYPE_KEY, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)

        val repo = StatusRepo(requireActivity())
        viewModel = ViewModelProvider(
            requireActivity(),
            StatusViewModelFactory(repo)
        )[StatusViewModel::class.java]

        setupUI()
        return binding.root
    }

    private fun setupUI() {
        // Set up swipe-to-refresh functionality
        binding.swipeRefreshLayout.setOnRefreshListener {
            when (type) {
                Constants.TYPE_WHATSAPP_MAIN -> refreshWhatsAppStatuses()
                Constants.TYPE_WHATSAPP_BUSINESS -> refreshWhatsAppBusinessStatuses()
                else -> Log.e("FragmentStatus", "Unknown fragment type: $type")
            }
        }

        when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> setupWhatsAppUI()
            Constants.TYPE_WHATSAPP_BUSINESS -> setupWhatsAppBusinessUI()
            else -> Log.e("FragmentStatus", "Unknown fragment type: $type")
        }
    }

    private fun setupWhatsAppUI() {
        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, false
        )

        if (isPermissionGranted) {
            getWhatsAppStatuses()
        } else {

            binding.permissionLayout.btnPermission.setOnClickListener {
                isAppInstalled(Constants.TYPE_WHATSAPP_MAIN, requireContext()).apply {
                    if (this) {
                        getFolderPermissions(
                            requireContext(),
                            WHATSAPP_REQUEST_CODE,
                            Constants.getWhatsappUri()
                        )

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "WhatsApp is not Installed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }

        setupViewPager()
    }

    private fun setupWhatsAppBusinessUI() {
        val isPermissionGranted = SharedPrefUtils.getPrefBoolean(
            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, false
        )

        if (isPermissionGranted) {
            getWhatsAppBusinessStatuses()
        } else {

            binding.permissionLayout.btnPermission.setOnClickListener {
                isAppInstalled(Constants.TYPE_WHATSAPP_BUSINESS, requireContext()).apply {
                    if (this) {
                        getFolderPermissions(
                            requireContext(),
                            WHATSAPP_BUSINESS_REQUEST_CODE,
                            Constants.getWhatsappBusinessUri()
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "WhatsApp Business is not Installed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        }

        setupViewPager(isBusiness = true)
    }

    private fun setupViewPager(isBusiness: Boolean = false) {
        val adapter = if (isBusiness) {
            MediaViewPagerAdapter(
                requireActivity(),
                Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
            )
        } else {
            MediaViewPagerAdapter(requireActivity())
        }

        binding.statusViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
            tab.text = viewPagerTitles.getOrNull(pos) ?: "Unknown"
        }.attach()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data?.data != null) {
            val treeUri = data.data!!

            runCatching {
                val resolver = requireActivity().contentResolver
                val hasPermission = resolver.persistedUriPermissions.any {
                    it.uri == treeUri && it.isReadPermission
                }

                if (!hasPermission) {
                    resolver.takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                when (requestCode) {
                    WHATSAPP_REQUEST_CODE -> {
                        SharedPrefUtils.putPrefString(
                            SharedPrefKeys.PREF_KEY_WP_TREE_URI, treeUri.toString()
                        )
                        SharedPrefUtils.putPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED, true
                        )
                        fetchStatuses { getWhatsAppStatuses() }
                    }

                    WHATSAPP_BUSINESS_REQUEST_CODE -> {
                        SharedPrefUtils.putPrefString(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, treeUri.toString()
                        )
                        SharedPrefUtils.putPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED, true
                        )
                        fetchStatuses { getWhatsAppBusinessStatuses() }
                    }
                }
            }.onFailure { e ->
                Log.e("FragmentStatus", "Error granting persistable permission: ${e.message}")
            }
        }
    }

    private fun fetchStatuses(action: () -> Unit) {
        requireActivity().runOnUiThread(action)
    }

    private fun getWhatsAppStatuses() {
        binding.permissionLayoutHolder.visibility = View.GONE
        viewModel.getWhatsAppStatuses()
    }

    private fun getWhatsAppBusinessStatuses() {
        binding.permissionLayoutHolder.visibility = View.GONE
        viewModel.getWhatsAppBusinessStatuses()
    }

    private fun refreshWhatsAppStatuses() {
        binding.swipeRefreshLayout.isRefreshing = true
        fetchStatuses {
            getWhatsAppStatuses()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshWhatsAppBusinessStatuses() {
        binding.swipeRefreshLayout.isRefreshing = true
        fetchStatuses {
            getWhatsAppBusinessStatuses()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Prevent memory leaks
    }
}
