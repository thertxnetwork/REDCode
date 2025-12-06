package com.redcode.editor.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import java.io.*

class FileManager(private val context: Context) {
    
    companion object {
        private const val OUTPUT_MODE_WRITE_TRUNCATE = "wt"
    }
    
    fun readFile(uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun writeFile(uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri, OUTPUT_MODE_WRITE_TRUNCATE)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(content)
                }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getFileName(uri: Uri): String {
        var result = "Untitled"
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex)
                    }
                }
            }
        }
        if (result == "Untitled") {
            result = uri.path?.substringAfterLast('/') ?: "Untitled"
        }
        return result
    }
    
    fun createFile(directoryUri: Uri, fileName: String, mimeType: String = "text/plain"): Uri? {
        return try {
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri)
            documentFile?.createFile(mimeType, fileName)?.uri
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun deleteFile(uri: Uri): Boolean {
        return try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
