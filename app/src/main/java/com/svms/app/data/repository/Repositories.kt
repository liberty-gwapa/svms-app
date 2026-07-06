package com.svms.app.data.repository

import com.svms.app.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val context: Context
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()
    private val prefs = context.getSharedPreferences("user_cache", Context.MODE_PRIVATE)

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        Log.d("SVMS_AUTH", "AuthRepository: initialize() called")
        try {
            // 1. Try to load from cache first for immediate UI update
            val cachedUserJson = prefs.getString("current_user", null)
            if (cachedUserJson != null) {
                Log.d("SVMS_AUTH", "AuthRepository: Found cached user profile")
                val cachedUser = Json.decodeFromString<User>(cachedUserJson)
                _currentUser.value = cachedUser
            }

            // 2. Wait for Supabase session restoration
            var session = supabaseClient.auth.currentSessionOrNull()
            var retryCount = 0
            while (session == null && retryCount < 10) {
                Log.d("SVMS_AUTH", "AuthRepository: Waiting for session... attempt $retryCount")
                delay(100)
                session = supabaseClient.auth.currentSessionOrNull()
                retryCount++
            }

            Log.d("SVMS_AUTH", "AuthRepository: Supabase session is ${if (session != null) "active" else "null"}")
            
            if (session != null) {
                val email = session.user?.email
                val authId = session.user?.id
                if (email != null) {
                    Log.d("SVMS_AUTH", "AuthRepository: Verifying profile for $email")
                    var user = supabaseClient.postgrest["users"]
                        .select { filter { eq("email", email) } }
                        .decodeSingleOrNull<User>()
                    
                    if (user != null && user.role.lowercase() == "guard") {
                        Log.d("SVMS_AUTH", "AuthRepository: Profile verified as GUARD")

                        // Try to load profile picture from profile_svms table
                        if (authId != null) {
                            try {
                                val profileSvms = supabaseClient.postgrest["profile_svms"]
                                    .select { filter { eq("guard_id", authId) } }
                                    .decodeSingleOrNull<ProfileSvms>()
                                if (profileSvms?.profileUrl != null) {
                                    user = user.copy(profileImageUrl = profileSvms.profileUrl)
                                    Log.d("SVMS_AUTH", "AuthRepository: Profile URL loaded from profile_svms")
                                }
                            } catch (e: Exception) {
                                Log.e("SVMS_AUTH", "AuthRepository: profile_svms fetch error", e)
                            }
                        }

                        _currentUser.value = user
                        // Update cache
                        prefs.edit().putString("current_user", Json.encodeToString(user)).apply()
                        return@withContext true
                    } else {
                        Log.w("SVMS_AUTH", "AuthRepository: Profile mismatch or not GUARD. Role: ${user?.role}")
                    }
                }
            }
            
            // Final check: if we have NO session after retries, clear the local profile cache
            if (session == null) {
                Log.d("SVMS_AUTH", "AuthRepository: No valid session after waiting, clearing user cache")
                prefs.edit().remove("current_user").apply()
                _currentUser.value = null
                return@withContext false
            }
            
            _currentUser.value != null
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "AuthRepository: Initialization error", e)
            _currentUser.value != null
        }
    }

    fun getCurrentSession() = supabaseClient.auth.currentSessionOrNull()

    suspend fun login(email: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        Log.d("SVMS_AUTH", "AuthRepository: login() called for $email")
        try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }

            val user = supabaseClient.postgrest["users"]
                .select { filter { eq("email", email.trim()) } }
                .decodeSingleOrNull<User>()
                ?: return@withContext Result.failure(Exception("User profile not found."))

            if (user.role.lowercase() != "guard") {
                Log.w("SVMS_AUTH", "AuthRepository: Login blocked - User role is ${user.role}")
                supabaseClient.auth.signOut()
                return@withContext Result.failure(Exception("Access Denied: Guard only"))
            }

            // Save to cache
            Log.d("SVMS_AUTH", "AuthRepository: Login successful, caching profile")
            prefs.edit().putString("current_user", Json.encodeToString(user)).apply()
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "AuthRepository: Login failed", e)
            Result.failure(e)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        Log.d("SVMS_AUTH", "AuthRepository: logout() called")
        try {
            supabaseClient.auth.signOut()
            prefs.edit().remove("current_user").apply()
            _currentUser.value = null
            Log.d("SVMS_AUTH", "AuthRepository: Logout successful, cache cleared")
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "AuthRepository: Logout error", e)
            prefs.edit().remove("current_user").apply()
            _currentUser.value = null
        }
    }

    suspend fun updateProfileImage(imageUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
            val session = supabaseClient.auth.currentSessionOrNull()
            val authId = session?.user?.id ?: user.authUserId 
                ?: return@withContext Result.failure(Exception("Auth identification missing"))

            // 1. Update/Insert into profile_svms table (the new table)
            val profileEntry = ProfileSvms(
                guardId = authId,
                profileUrl = imageUrl
            )
            
            try {
                supabaseClient.postgrest["profile_svms"].upsert(profileEntry) {
                    onConflict = "guard_id"
                }
                Log.d("SVMS_AUTH", "Successfully upserted to profile_svms")
            } catch (e: Exception) {
                Log.e("SVMS_AUTH", "Error upserting to profile_svms", e)
                // Continue anyway to update the main users table
            }
            
            // 2. Update the users table as well for general profile access
            supabaseClient.postgrest["users"]
                .update({
                    set("profile_image_url", imageUrl)
                }) {
                    filter {
                        eq("user_id", user.userId)
                    }
                }
            
            // Update local user state
            val updatedUser = user.copy(profileImageUrl = imageUrl)
            _currentUser.value = updatedUser
            
            // Update cache
            prefs.edit().putString("current_user", Json.encodeToString(updatedUser)).apply()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "updateProfileImage error", e)
            Result.failure(e)
        }
    }

    suspend fun getProfileImage(): String? = withContext(Dispatchers.IO) {
        try {
            val user = _currentUser.value ?: return@withContext null
            supabaseClient.postgrest["users"]
                .select {
                    filter {
                        eq("user_id", user.userId)
                    }
                }
                .decodeSingle<User>()
                .profileImageUrl
        } catch (e: Exception) {
            null
        }
    }

    fun isLoggedIn() = _currentUser.value != null

    suspend fun getNotifications(): Result<List<NotificationSvms>> = withContext(Dispatchers.IO) {
        try {
            val user = _currentUser.value ?: return@withContext Result.failure(Exception("Not logged in"))
            Log.d("SVMS_AUTH", "Fetching notifications for user: ${user.userId} and role: ${user.role}")
            
            val notifications = supabaseClient.postgrest["notification_svms"]
                .select {
                    filter {
                        or {
                            eq("user_id", user.userId)
                            // Match role_target exactly as it appears in DB
                            eq("role_target", user.role.lowercase())
                            eq("role_target", "guard")
                        }
                    }
                    order("created_at", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<NotificationSvms>()
            
            Log.d("SVMS_AUTH", "Successfully fetched ${notifications.size} notifications")
            Result.success(notifications)
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "Error fetching notifications", e)
            Result.failure(e)
        }
    }

    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseClient.postgrest["notification_svms"]
                .update({
                    set("is_read", true)
                }) {
                    filter {
                        eq("notification_id", notificationId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Singleton
class StudentRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getStudentById(id: String): Result<Student> = withContext(Dispatchers.IO) {
        try {
            // school_id is bigint in database
            val schoolId = id.trim().toLongOrNull() ?: return@withContext Result.failure(Exception("Invalid Student ID format: $id"))

            val student = supabaseClient.postgrest["students"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingleOrNull<Student>()

            if (student != null) {
                Result.success(student)
            }
            else Result.failure(Exception("Student not found with ID: $id"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchStudents(query: String): Result<List<Student>> = withContext(Dispatchers.IO) {
        try {
            val students = supabaseClient.postgrest["students"]
                .select {
                    filter {
                        or {
                            ilike("first_name", "%$query%")
                            ilike("last_name", "%$query%")
                            // If numeric, try to find by school_id
                            query.toLongOrNull()?.let { id ->
                                eq("school_id", id)
                            }
                        }
                    }
                }
                .decodeList<Student>()
            Result.success(students)
        } catch (e: Exception) {
            println("Search Students Error: ${e.message}")
            Result.failure(e)
        }
    }
}

@Singleton
class ViolationRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val _violations = MutableStateFlow<List<Violation>>(emptyList())
    val violations: Flow<List<Violation>> = _violations.asStateFlow()

    suspend fun getDepartments(): Result<List<Department>> = withContext(Dispatchers.IO) {
        try {
            Log.d("SVMS_DEBUG", "Fetching departments from Supabase...")
            val depts = supabaseClient.postgrest["departments"]
                .select()
                .decodeList<Department>()
            Log.d("SVMS_DEBUG", "Fetched ${depts.size} departments: ${depts.map { it.departmentKey }}")
            Result.success(depts)
        } catch (e: Exception) {
            Log.e("SVMS_DEBUG", "Error fetching departments", e)
            Result.failure(e)
        }
    }

    suspend fun getViolationTypes(): Result<List<ViolationType>> = withContext(Dispatchers.IO) {
        try {
            val types = supabaseClient.postgrest["violation_types"]
                .select()
                .decodeList<ViolationType>()
            Result.success(types)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTodayViolations(): Result<List<Violation>> = withContext(Dispatchers.IO) {
        try {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            Log.d("SVMS_DEBUG", "Fetching violations for date: $todayDate")
            
            // 1. Fetch violations for today
            val violations = supabaseClient.postgrest["violations"]
                .select {
                    filter {
                        eq("violation_date", todayDate)
                    }
                    order("violation_time", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<Violation>()
            
            // 2. Filter for guard reports
            val guardViolations = violations.filter { it.reportedByGuard != null }

            val enriched = enrichViolationList(guardViolations)
            _violations.value = enriched
            Result.success(enriched)
        } catch (e: Exception) {
            Log.e("SVMS_DEBUG", "Fetch Today Violations Error", e)
            Result.failure(e)
        }
    }

    suspend fun fetchAllTimeGuardViolations(guardId: String): Result<List<Violation>> = withContext(Dispatchers.IO) {
        try {
            Log.d("SVMS_DEBUG", "Fetching all-time violations for guard: $guardId")
            
            val violations = supabaseClient.postgrest["violations"]
                .select {
                    filter {
                        eq("reported_by_guard", guardId)
                    }
                    order("violation_date", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                    order("violation_time", order = io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<Violation>()
            
            val enriched = enrichViolationList(violations)
            Result.success(enriched)
        } catch (e: Exception) {
            Log.e("SVMS_DEBUG", "Fetch All Time Violations Error", e)
            Result.failure(e)
        }
    }

    private suspend fun enrichViolationList(violations: List<Violation>): List<Violation> {
        // 3. Batch fetch Students to avoid N+1 queries
        val studentIds = violations.map { it.studentId }.distinct()
        val students = if (studentIds.isNotEmpty()) {
            supabaseClient.postgrest["students"]
                .select { filter { isIn("school_id", studentIds) } }
                .decodeList<Student>()
                .associateBy { it.schoolId }
        } else emptyMap()

        // 4. Batch fetch Violation Types
        val typeIds = violations.map { it.violationTypeId }.distinct()
        val types = if (typeIds.isNotEmpty()) {
            supabaseClient.postgrest["violation_types"]
                .select { filter { isIn("violation_type_id", typeIds) } }
                .decodeList<ViolationType>()
                .associateBy { it.violationTypeId }
        } else emptyMap()

        // 5. Batch fetch Users (Guards)
        val guardIds = violations.mapNotNull { it.reportedByGuard }.distinct()
        val guards = if (guardIds.isNotEmpty()) {
            supabaseClient.postgrest["users"]
                .select { filter { isIn("user_id", guardIds) } }
                .decodeList<User>()
                .associateBy { it.userId }
        } else emptyMap()

        // 6. Enrich violations
        return violations.map { violation ->
            val student = students[violation.studentId]
            val type = types[violation.violationTypeId]
            val guard = guards[violation.reportedByGuard]

            violation.apply {
                studentName = student?.fullName ?: "Unknown Student"
                studentCollege = student?.college
                studentCourse = student?.course
                violationType = type?.name ?: "Unknown Type"
                violationCategory = if ((type?.severity ?: 1) <= 1) ViolationCategory.MINOR else ViolationCategory.MAJOR
                guardName = guard?.fullName ?: "Unknown Guard"
                
                evidenceImageUrl = if (!violation.evidenceUrl.isNullOrBlank()) {
                    violation.evidenceUrl
                } else null
            }
        }
    }

    suspend fun addViolation(violation: Violation): Result<Violation> = withContext(Dispatchers.IO) {
        try {
            Log.i("SVMS_DEBUG", "SUBMITTING VIOLATION TO DB. URL: ${violation.evidenceUrl}")
            
            // 1. Insert into Supabase violations table
            supabaseClient.postgrest["violations"].insert(violation)
            
            // 2. Create notification for SAO Admin
            try {
                val notification = NotificationSvms(
                    roleTarget = "sao_admin",
                    title = "A report from guard",
                    message = "A new report from guard has been submitted."
                )
                supabaseClient.postgrest["notification_svms"].insert(notification)
                Log.i("SVMS_DEBUG", "Notification created for SAO Admin")
            } catch (ne: Exception) {
                Log.e("SVMS_DEBUG", "Failed to create notification", ne)
            }
            
            Log.i("SVMS_DEBUG", "Database insertion complete")
            
            // Fetch updated list for today to keep UI in sync
            fetchTodayViolations()
            
            Result.success(violation)
        } catch (e: Exception) {
            Log.e("SVMS_AUTH", "Add Violation Error", e)
            Result.failure(e)
        }
    }

    fun getPendingReviewCount(): Int {
        return _violations.value.count { it.status == "PENDING" }
    }

    suspend fun getViolationById(violationId: String): Result<Violation> = withContext(Dispatchers.IO) {
        try {
            val violation = supabaseClient.postgrest["violations"]
                .select {
                    filter {
                        eq("violation_id", violationId)
                    }
                }
                .decodeSingleOrNull<Violation>()
                ?: return@withContext Result.failure(Exception("Violation not found"))

            // Enrich violation data
            val student = supabaseClient.postgrest["students"]
                .select { filter { eq("school_id", violation.studentId) } }
                .decodeSingleOrNull<Student>()
            
            val type = supabaseClient.postgrest["violation_types"]
                .select { filter { eq("violation_type_id", violation.violationTypeId) } }
                .decodeSingleOrNull<ViolationType>()

            val reporterId = violation.reportedByGuard
            val guard = if (reporterId != null) {
                supabaseClient.postgrest["users"]
                    .select { filter { eq("user_id", reporterId) } }
                    .decodeSingleOrNull<User>()
            } else null

            violation.apply {
                studentName = student?.fullName ?: "Unknown Student"
                studentCollege = student?.college
                studentCourse = student?.course
                violationType = type?.name ?: "Unknown Type"
                violationCategory = if ((type?.severity ?: 1) <= 1) ViolationCategory.MINOR else ViolationCategory.MAJOR
                guardName = guard?.fullName ?: "Unknown Guard"
                
                // Use evidenceUrl column
                evidenceImageUrl = if (!violation.evidenceUrl.isNullOrBlank()) {
                    violation.evidenceUrl
                } else null
            }

            Result.success(violation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
