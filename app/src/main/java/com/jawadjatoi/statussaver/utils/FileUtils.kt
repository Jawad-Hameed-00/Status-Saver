package com.jawadjatoi.statussaver.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.toRawFile
import com.jawadjatoi.statussaver.R
import com.jawadjatoi.statussaver.models.MediaModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Context.isStatusExist(fileName: String): Boolean {
    val downloadsDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        getString(R.string.app_name)
    )
    val file = File(downloadsDir, fileName)
    return file.exists()
}

fun getFileExtension(fileName: String): String {
    val lastDotIndex = fileName.lastIndexOf(".")
    return if (lastDotIndex >= 0 && lastDotIndex < fileName.length - 1) {
        fileName.substring(lastDotIndex + 1)
    } else {
        ""
    }
}

fun Context.saveStatus(model: MediaModel): Boolean {
    if (isStatusExist(model.fileName)) {
        return true
    }

    val extension = getFileExtension(model.fileName)
    val mimeType = when (extension.lowercase()) {
        "jpg", "jpeg", "png", "gif" -> "image/$extension"
        "mp4", "avi", "mkv" -> "video/$extension"
        else -> "application/octet-stream"
    }

    val inputStream = contentResolver.openInputStream(model.pathUri.toUri()) ?: return false

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.DISPLAY_NAME, model.fileName)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + getString(R.string.app_name))
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return false

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        } else {
            saveStatusBeforeQ(this, model.pathUri.toUri())
        }
        inputStream.close()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun saveStatusBeforeQ(context: Context, uri: Uri): Boolean {
    return try {
        val sourceFile = DocumentFile.fromSingleUri(context, uri)?.toRawFile(context)?.takeIf { it.canRead() }
        val downloadsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))

        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val destinationFile = File(downloadsDir, sourceFile?.name ?: "unknown_file")

        sourceFile?.let { sourceF ->
            FileInputStream(sourceF).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            return true
        }
        false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
