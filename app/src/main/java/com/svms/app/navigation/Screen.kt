package com.svms.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Scanner : Screen("scanner")
    object StudentDetails : Screen("student_details/{studentId}") {
        fun createRoute(studentId: String) = "student_details/$studentId"
    }
    object AddViolation : Screen("add_violation/{studentId}") {
        fun createRoute(studentId: String) = "add_violation/$studentId"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object ViolationDetails : Screen("violation_details/{violationId}") {
        fun createRoute(violationId: String) = "violation_details/$violationId"
    }
}
