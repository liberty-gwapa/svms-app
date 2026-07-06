package com.svms.app.di

import com.svms.app.data.repository.AuthRepository
import com.svms.app.data.repository.StudentRepository
import com.svms.app.data.repository.ViolationRepository
import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.svms.app.data.remote.CloudinaryService
import com.svms.app.data.repository.CloudinaryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message ->
            if (message.contains("Authorization: Bearer", ignoreCase = true)) {
                Log.d("OkHttp_Masked", "Authorization: Bearer [MASKED]")
            } else {
                Log.d("OkHttp", message)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideCloudinaryService(okHttpClient: OkHttpClient): CloudinaryService {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/v1_1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryService::class.java)
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        Log.d("SVMS_AUTH", "AppModule: Providing SupabaseClient with persistent SessionManager")
        val jsonConfig = Json { 
            ignoreUnknownKeys = true 
            coerceInputValues = true
            encodeDefaults = true
        }
        
        return createSupabaseClient(
            supabaseUrl = "https://fjsvonbegisutzilymgp.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZqc3ZvbmJlZ2lzdXR6aWx5bWdwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk3NTAwMDEsImV4cCI6MjA5NTMyNjAwMX0.mYp6WaRW6obaKKbAeu3ZVfq4g1XCfLQE7DMNqtVg9sk"
        ) {
            defaultSerializer = io.github.jan.supabase.serializer.KotlinXSerializer(jsonConfig)
            install(Postgrest)
            install(Auth) {
                alwaysAutoRefresh = true
                sessionManager = object : SessionManager {
                    private val prefs = context.getSharedPreferences("svms_auth_session", Context.MODE_PRIVATE)
                    private val json = Json { ignoreUnknownKeys = true }

                    override suspend fun saveSession(session: UserSession) {
                        Log.d("SVMS_AUTH", "SessionManager: saveSession() called. User: ${session.user?.email}")
                        try {
                            val sessionString = json.encodeToString(session)
                            prefs.edit().putString("auth_token", sessionString).apply()
                            Log.d("SVMS_AUTH", "SessionManager: Session successfully saved to SharedPreferences")
                        } catch (e: Exception) {
                            Log.e("SVMS_AUTH", "SessionManager: Error saving session", e)
                        }
                    }

                    override suspend fun loadSession(): UserSession? {
                        Log.d("SVMS_AUTH", "SessionManager: loadSession() called")
                        val sessionString = prefs.getString("auth_token", null)
                        if (sessionString == null) {
                            Log.d("SVMS_AUTH", "SessionManager: No session found in SharedPreferences")
                            return null
                        }
                        return try {
                            val session = json.decodeFromString<UserSession>(sessionString)
                            Log.d("SVMS_AUTH", "SessionManager: Session successfully loaded and decoded")
                            session
                        } catch (e: Exception) {
                            Log.e("SVMS_AUTH", "SessionManager: Error decoding session", e)
                            null
                        }
                    }

                    override suspend fun deleteSession() {
                        Log.d("SVMS_AUTH", "SessionManager: deleteSession() called")
                        prefs.edit().remove("auth_token").apply()
                        Log.d("SVMS_AUTH", "SessionManager: Session cleared from SharedPreferences")
                    }
                }
            }
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        supabaseClient: SupabaseClient,
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepository(supabaseClient, context)

    @Provides
    @Singleton
    fun provideStudentRepository(supabaseClient: SupabaseClient): StudentRepository = StudentRepository(supabaseClient)

    @Provides
    @Singleton
    fun provideViolationRepository(supabaseClient: SupabaseClient): ViolationRepository = ViolationRepository(supabaseClient)
}
