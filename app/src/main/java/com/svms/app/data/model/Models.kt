package com.svms.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID
import com.google.gson.annotations.SerializedName as GsonName

// ================================
// USERS
// ================================
@Serializable
data class User(
    @SerialName("user_id")
    val userId: String = UUID.randomUUID().toString(),
    val username: String = "",
    val email: String = "",
    val password: String = "",
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("middle_name")
    val middleName: String? = null,
    @SerialName("last_name")
    val lastName: String = "",
    @SerialName("contact_number")
    val contactNumber: String? = null,
    val role: String = "guard",
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("auth_user_id")
    val authUserId: String? = null,
    @SerialName("profile_image_url")
    val profileImageUrl: String? = null,
    @SerialName("ismis_id")
    val ismisId: Long? = null,
    @SerialName("rei_sub_department")
    val reiSubDepartment: String? = null,
    val systems: List<String>? = null,
    @SerialName("campus_id")
    val campusId: Int? = null,
    @SerialName("department_id")
    val departmentId: Long? = null,
    @SerialName("designation_id")
    val designationId: Long? = null,
    @SerialName("postgraduate_education_id")
    val postgraduateEducationId: Int? = null,
    val office: String? = null
) {
    val fullName: String
        get() = listOfNotNull(firstName, middleName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

@Serializable
enum class UserRole {
    @SerialName("guard")
    GUARD,
    @SerialName("admin")
    ADMIN,
    @SerialName("super_admin")
    SUPER_ADMIN,
    @SerialName("sao_admin")
    SAO_ADMIN,
    @SerialName("faculty")
    FACULTY
}

// ================================
// STUDENTS
// ================================
@Serializable
data class Student(
    @SerialName("school_id")
    val schoolId: Long = 0,
    @SerialName("auth_user_id")
    val studentId: String? = null,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("middle_name")
    val middleName: String? = null,
    @SerialName("last_name")
    val lastName: String = "",
    val address: String? = null,
    val college: String? = null,
    val course: String? = null,
    val sex: String? = null,
    @SerialName("year_level")
    val yearLevel: String? = null,
    val dob: String? = null,
    @SerialName("created_at")
    val createdAt: String = ""
) {
    val fullName: String
        get() = listOfNotNull(firstName, middleName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

// ================================
// VIOLATION TYPES
// ================================
@Serializable
data class ViolationType(
    @SerialName("violation_type_id")
    val violationTypeId: String = UUID.randomUUID().toString(),
    val name: String = "",
    var description: String? = null,
    val severity: Int = 1,
    @SerialName("created_at")
    val createdAt: String = ""
)

@Serializable
enum class ViolationCategory {
    MINOR,
    MAJOR
}

@Serializable
data class ViolationTypeItem(
    val id: String = "",
    val name: String,
    val category: ViolationCategory
)

// ================================
// NOTIFICATIONS
// ================================
@Serializable
data class NotificationSvms(
    @SerialName("notification_id")
    val notificationId: String = UUID.randomUUID().toString(),
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("role_target")
    val roleTarget: String? = null,
    val title: String,
    val message: String,
    @SerialName("is_read")
    val isRead: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null
)

// ================================
// VIOLATIONS
// ================================
@Serializable
data class Violation(
    @SerialName("violation_id")
    val violationId: String = UUID.randomUUID().toString(),
    @SerialName("student_id")
    val studentId: Long = 0,
    @SerialName("violation_type_id")
    val violationTypeId: String = "",
    @SerialName("reported_by_guard")
    val reportedByGuard: String? = null,
    @SerialName("reported_by_faculty")
    val reportedByFaculty: String? = null,
    @SerialName("violation_date")
    val violationDate: String = "",
    @SerialName("violation_time")
    val violationTime: String = "",
    var description: String? = null,
    val status: String = "PENDING",
    val place: String? = null,
    @SerialName("offense_count")
    val offenseCount: Int = 1,
    @SerialName("created_at")
    val createdAtDb: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("evidence_url")
    var evidenceUrl: String? = null
) {
    @Transient
    var studentName: String = ""
    @Transient
    var studentCollege: String? = null
    @Transient
    var studentCourse: String? = null
    @Transient
    var violationType: String = ""
    @Transient
    var violationCategory: ViolationCategory = ViolationCategory.MINOR
    @Transient
    var guardName: String = ""
    @Transient
    var evidenceImageUrl: String? = null
    @Transient
    var createdAt: Long = System.currentTimeMillis()
}

@Serializable
data class ViolationEvidence(
    @SerialName("evidence_id")
    val evidenceId: String = UUID.randomUUID().toString(),
    @SerialName("violation_id")
    val violationId: String = "",
    @SerialName("file_path")
    val filePath: String = "",
    @SerialName("uploaded_at")
    val uploadedAt: String? = null
)

@Serializable
data class CloudinaryResponse(
    @SerialName("secure_url")
    @GsonName("secure_url")
    val secureUrl: String? = null,
    
    @SerialName("url")
    @GsonName("url")
    val url: String? = null,
    
    @SerialName("public_id")
    @GsonName("public_id")
    val publicId: String? = null,
    
    @SerialName("version")
    @GsonName("version")
    val version: Long = 0,
    
    @SerialName("format")
    @GsonName("format")
    val format: String? = null,
    
    @SerialName("resource_type")
    @GsonName("resource_type")
    val resourceType: String? = null,
    
    @SerialName("created_at")
    @GsonName("created_at")
    val createdAt: String? = null
)

@Serializable
data class ProfileSvms(
    val id: String? = null,
    @SerialName("guard_id")
    val guardId: String,
    @SerialName("profile_url")
    val profileUrl: String? = null,
    val date: String? = null
)
