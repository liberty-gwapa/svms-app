package com.svms.app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.svms.app.data.remote.CloudinaryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryRepository @Inject constructor(
    private val cloudinaryService: CloudinaryService
) {
    private val cloudName = "dogoeiyjb"
    private val apiKey = "343586569128975"
    private val apiSecret = "Ry7kM1B8e9z-jx11jHGZnMk8ZFg"
    private val uploadPreset = "ml_default"

    suspend fun uploadImageToCloudinary(
        context: Context,
        imageUri: Uri
    ): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("CLOUDINARY_DEBUG", "Starting upload pipeline for: $imageUri")
            
            val file = uriToFile(context, imageUri)
            if (file == null || !file.exists()) {
                Log.e("CLOUDINARY_DEBUG", "Failed to resolve URI to file")
                return@withContext null
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            fun String.toTextPart(): RequestBody = this.toRequestBody("text/plain".toMediaTypeOrNull())

            // 1. Try SIGNED Upload (Primary)
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val signatureStr = "timestamp=$timestamp&upload_preset=$uploadPreset$apiSecret"
            val signature = generateSha1(signatureStr)

            Log.d("CLOUDINARY_DEBUG", "Executing Signed Upload Request...")
            val response = cloudinaryService.uploadImage(
                cloudName = cloudName,
                file = filePart,
                uploadPreset = uploadPreset.toTextPart(),
                apiKey = apiKey.toTextPart(),
                timestamp = timestamp.toTextPart(),
                signature = signature.toTextPart()
            )

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("CLOUDINARY_DEBUG", "Signed Upload Successful. Body: $body")
                val url = body?.secureUrl ?: body?.url
                if (!url.isNullOrBlank()) {
                    Log.i("CLOUDINARY_DEBUG", "RETRIEVED URL: $url")
                    return@withContext url
                } else {
                    Log.e("CLOUDINARY_DEBUG", "Response successful but URL fields are null")
                }
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("CLOUDINARY_DEBUG", "Signed Upload Failed: $errorMsg")
                
                // 2. Fallback to UNSIGNED Upload
                Log.d("CLOUDINARY_DEBUG", "Attempting Unsigned Fallback...")
                val unsignedResponse = cloudinaryService.uploadImage(
                    cloudName = cloudName,
                    file = filePart,
                    uploadPreset = uploadPreset.toTextPart()
                )
                
                if (unsignedResponse.isSuccessful) {
                    val url = unsignedResponse.body()?.secureUrl ?: unsignedResponse.body()?.url
                    if (!url.isNullOrBlank()) {
                        Log.i("CLOUDINARY_DEBUG", "Unsigned Fallback Successful. URL: $url")
                        return@withContext url
                    }
                } else {
                    Log.e("CLOUDINARY_DEBUG", "Unsigned Fallback Failed: ${unsignedResponse.errorBody()?.string()}")
                }
            }
            
            null
        } catch (e: Exception) {
            Log.e("CLOUDINARY_DEBUG", "Upload pipeline exception", e)
            null
        }
    }

    private fun generateSha1(input: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(input.toByteArray())
        val sb = StringBuilder()
        for (b in result) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("CLOUDINARY_DEBUG", "File conversion error", e)
            null
        }
    }
}
