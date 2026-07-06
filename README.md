# Student Violation Monitoring System (SVMS)
### Jetpack Compose Android App

A fully functional mobile application for monitoring and recording student violations. Built with Jetpack Compose, MVVM + Clean Architecture, Hilt DI, CameraX, and ML Kit Barcode Scanning.

---

## 📱 Screens

| Screen | Description |
|--------|-------------|
| **Login** | Institutional gateway with email/password authentication |
| **Add Violation** | Scan student ID (QR/barcode) or manual search, select offense, attach documentation |
| **History** | Live feed of today's reports with incident stats |

---

## 🚀 System Flow

```
Login Screen
    ↓ (guard@institution.edu / password123)
Add Violation Screen
    ├── SCAN STUDENT ID (CameraX + ML Kit barcode/QR)
    │       ↓ (scan any barcode: STU001, 2024-00001 ... 2024-00005)
    ├── OR Manual Search (type name or ID)
    ↓
Select Minor or Major Offense
    ↓
Add Location + Remarks
    ↓
Submit → Success Dialog
    ↓
History Screen (violation appears in live feed)
```

---

## 🔑 Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Guard | guard@institution.edu | password123 |
| Admin | admin@institution.edu | admin123 |

---

## 🎯 Test Barcode Values

To test without a physical barcode scanner, print or display these IDs on screen and scan:

| Student ID | Name | Course |
|------------|------|--------|
| STU001 | John Doe | BSIT |
| STU002 | Jane Santos | BSCS |
| 2024-00001 | Marcus Thorne | BSIT |
| 2024-00002 | Elena Rodriguez | BSCS |
| 2024-00003 | Jordan Smith | BSBA |
| 2024-00004 | Li Wei | BSN |
| 2024-00005 | Ana Cruz | BSED |

---

## 🛠️ Tech Stack

```
UI              → Jetpack Compose + Material 3
Architecture    → MVVM + Clean Architecture
DI              → Hilt
Navigation      → Navigation Compose
Camera          → CameraX
Barcode/QR      → ML Kit Barcode Scanning
State           → StateFlow + collectAsState
Coroutines      → Kotlin Coroutines
Permissions     → Accompanist Permissions
```

---

## 📁 Project Structure

```
app/src/main/java/com/svms/app/
├── data/
│   ├── model/          Models.kt (User, Student, Violation, Enums)
│   └── repository/     Repositories.kt (Auth, Student, Violation)
├── di/
│   └── AppModule.kt    Hilt DI module
├── navigation/
│   └── Screen.kt       Route definitions
├── presentation/
│   ├── login/          LoginScreen + LoginViewModel
│   ├── scanner/        BarcodeScanner + ScannerViewModel
│   ├── violation/      AddViolationScreen + ViolationViewModel
│   ├── history/        HistoryScreen + HistoryViewModel
│   └── shared/         Theme.kt + Components.kt
├── MainActivity.kt     NavGraph + Entry point
└── SVMSApplication.kt  Hilt Application
```

---

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17
- Android SDK 35
- Physical device or emulator with API 26+

### Steps

1. **Clone / Open the project** in Android Studio

2. **Sync Gradle** — Android Studio will download all dependencies automatically

3. **Run on device/emulator**
   - For barcode scanning, a physical device is recommended
   - Camera permission will be requested on first scan attempt

4. **To connect real Supabase** (optional):
   - Add your Supabase URL and anon key to `BuildConfig`
   - Replace `AuthRepository`, `StudentRepository`, `ViolationRepository` with Supabase-backed implementations
   - The architecture is already set up for this swap

---

## 🎨 Design System

| Token | Value |
|-------|-------|
| Primary | `#5B2D8E` (Purple) |
| Primary Dark | `#3D1A6B` |
| Accent | `#F5A623` (Gold) |
| Background | `#F5F5F8` |
| Card | `#FFFFFF` |
| Minor Offense | `#FF9800` (Orange) |
| Major Offense | `#E53935` (Red) |

---

## 📋 Violation Types

**Minor Offenses:** No ID, Improper Uniform, Tardiness, Littering, Mobile Phone Distraction, Incomplete Uniform, Late Entry, Noise Disturbance

**Major Offenses:** Bullying, Smoking, Academic Dishonesty, Vandalism, Fighting, Substance Abuse, Theft

---

## 🔐 Permissions Required

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

---

## 🔄 Extending to Supabase

The repository layer is designed for easy Supabase integration:

```kotlin
// Replace in AppModule.kt
@Provides @Singleton
fun provideStudentRepository(
    supabaseClient: SupabaseClient
): StudentRepository = StudentRepositorySupabaseImpl(supabaseClient)
```

Tables needed (see architecture doc):
- `users`, `students`, `violations`, `violation_types`

Storage bucket: `violation-evidence`

---

## ✅ Features Implemented

- [x] Login screen with validation
- [x] CameraX barcode / QR code scanning
- [x] ML Kit multi-format barcode support
- [x] Flash toggle during scanning
- [x] Manual student search with live dropdown
- [x] Minor / Major offense selection with accordion
- [x] Photo evidence attachment UI
- [x] Location field
- [x] Remarks with character counter
- [x] Success confirmation dialog
- [x] History screen with live feed
- [x] Incident stats (total + pending review)
- [x] Bottom navigation (Scan ↔ History)
- [x] Role-based user system
- [x] Hilt dependency injection
- [x] MVVM + Clean Architecture
