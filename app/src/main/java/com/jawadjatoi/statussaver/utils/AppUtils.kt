package com.jawadjatoi.statussaver.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.jawadjatoi.statussaver.R

fun Activity.replaceFragment(fragment: Fragment, args: Bundle? = null) {
    val fragmentActivity = this as FragmentActivity
    fragmentActivity.supportFragmentManager.beginTransaction().apply {
        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        args?.let {
            fragment.arguments = it
        }
        replace(R.id.fragment_container, fragment)
    }.commit()
}

fun isAppInstalled(packageName: String, context: Context): Boolean {
    return try {
        // Get the PackageManager
        val packageManager = context.packageManager

        // Try to get the app information for the given package
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)

        // If no exception is thrown, the app is installed
        true
    } catch (e: PackageManager.NameNotFoundException) {
        // If the package is not found, the app is not installed
        false
    }
}

