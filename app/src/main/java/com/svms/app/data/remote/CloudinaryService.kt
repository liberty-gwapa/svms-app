package com.svms.app.data.remote

import com.svms.app.data.model.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CloudinaryService {
    @Multipart
    @POST("{cloudName}/image/upload")
    suspend fun uploadImage(
        @Path("cloudName") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part("api_key") apiKey: RequestBody? = null,
        @Part("timestamp") timestamp: RequestBody? = null,
        @Part("signature") signature: RequestBody? = null
    ): Response<CloudinaryResponse>
}
