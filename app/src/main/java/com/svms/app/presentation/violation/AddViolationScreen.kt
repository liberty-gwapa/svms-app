package com.svms.app.presentation.violation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.svms.app.data.model.*
import com.svms.app.presentation.notification.NotificationViewModel
import com.svms.app.presentation.scanner.BarcodeScannerView
import com.svms.app.presentation.scanner.ScannerViewModel
import com.svms.app.presentation.shared.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddViolationScreen(
    onViolationAdded: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    scannerViewModel: ScannerViewModel = hiltViewModel(),
    violationViewModel: ViolationViewModel = hiltViewModel()
) {
    val scannerState by scannerViewModel.uiState.collectAsState()
    val violationState by violationViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    var showScanner by remember { mutableStateOf(false) }
    var minorExpanded by remember { mutableStateOf(false) }
    var majorExpanded by remember { mutableStateOf(false) }
    var selectedViolation by remember { mutableStateOf<ViolationTypeItem?>(null) }
    var remarks by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { violationViewModel.uploadEvidenceImage(context, it) }
    }

    LaunchedEffect(violationState.isSuccess) {
        if (violationState.isSuccess) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(violationState.error) {
        violationState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(scannerState.error) {
        scannerState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (showScanner) {
        BarcodeScannerView(
            onBarcodeDetected = { barcode ->
                scannerViewModel.onBarcodeScanned(barcode)
                showScanner = false
            },
            onDismiss = { showScanner = false }
        )
        return
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = SuccessGreen, 
                        modifier = Modifier.size(40.dp)
                    )
                }
            },
            title = { 
                Text(
                    "Violation Recorded", 
                    fontWeight = FontWeight.Bold, 
                    color = TextPrimary,
                    fontSize = 18.sp
                ) 
            },
            text = { 
                Text(
                    "The disciplinary incident has been successfully logged in the institutional database.", 
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    fontSize = 13.sp
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        violationViewModel.resetSuccess()
                        scannerViewModel.resetNavigation()
                        onNavigateToHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(18.dp)
                ) { 
                    Text("View History", fontWeight = FontWeight.Bold, fontSize = 13.sp) 
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        violationViewModel.resetSuccess()
                        scannerViewModel.resetNavigation()
                    }
                ) { 
                    Text("Add Another", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp) 
                }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Violation", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        if (violationState.currentUser?.profileImageUrl != null) {
                            AsyncImage(
                                model = violationState.currentUser?.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = violationState.currentUser?.firstName?.take(1) ?: "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurplePrimary,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                modifier = Modifier.shadow(4.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = PurplePrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.shadow(8.dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(24.dp)) },
                    label = { Text("Scan", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = PurpleLight
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(24.dp)) },
                    label = { Text("History", fontWeight = FontWeight.Medium, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = PurpleLight
                    )
                )
            }
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // STUDENT IDENTIFICATION SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionLabel("STUDENT IDENTIFICATION")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Identify the student involved in the incident.",
                        fontSize = 12.sp, color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show identified student or scan button
                    if (scannerState.scannedStudent != null) {
                        val student = scannerState.scannedStudent!!
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(PurpleContainer.copy(alpha = 0.5f))
                                .border(1.dp, PurplePrimary.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    student.firstName.take(1) + student.lastName.take(1),
                                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    student.fullName, 
                                    fontWeight = FontWeight.Bold, 
                                    fontSize = 15.sp, 
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "${student.schoolId} • ${student.course} • Yr ${student.yearLevel}", 
                                    fontSize = 12.sp, 
                                    color = TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { scannerViewModel.resetNavigation() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    Icons.Default.Close, 
                                    contentDescription = "Remove student", 
                                    tint = TextSecondary, 
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        SVMSButton(
                            text = "Scan Student ID Card",
                            onClick = {
                                if (cameraPermission.status.isGranted) {
                                    showScanner = true
                                } else {
                                    cameraPermission.launchPermissionRequest()
                                }
                            },
                            icon = Icons.Default.QrCodeScanner
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Divider or manual entry indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor.copy(alpha = 0.5f))
                            Text(
                                "OR ENTER MANUALLY", 
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontSize = 10.sp, 
                                color = TextSecondary, 
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor.copy(alpha = 0.5f))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Manual entry
                        OutlinedTextField(
                            value = scannerState.searchQuery,
                            onValueChange = scannerViewModel::onSearchQueryChange,
                            placeholder = { Text("Search by name or student ID...", color = TextSecondary.copy(alpha = 0.8f), fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PurplePrimary) },
                            trailingIcon = {
                                if (scannerState.searchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = { scannerViewModel.onManualSearch() },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(PurpleContainer)
                                    ) {
                                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = BackgroundGray.copy(alpha = 0.3f),
                                unfocusedContainerColor = BackgroundGray.copy(alpha = 0.3f)
                            )
                        )

                        // Search results dropdown
                        if (scannerState.searchResults.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    scannerState.searchResults.take(4).forEach { student ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { scannerViewModel.onStudentSelected(student) }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(PurpleContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                                Text("${student.schoolId} • ${student.course}", fontSize = 12.sp, color = TextSecondary)
                                            }
                                        }
                                        if (student != scannerState.searchResults.take(4).last()) {
                                            HorizontalDivider(color = BorderColor.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 8.dp))
                                        }
                                    }
                                }
                            }
                        }

                        if (scannerState.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SELECT OFFENSES SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionLabel("SELECT OFFENSES")
                    Spacer(modifier = Modifier.height(14.dp))

                    // Minor Offenses
                    ViolationCategoryHeader(
                        title = "Minor Offenses",
                        icon = Icons.Default.Info,
                        color = MinorOrange,
                        isExpanded = minorExpanded,
                        onClick = {
                            minorExpanded = !minorExpanded
                            if (minorExpanded) majorExpanded = false
                        }
                    )
                    AnimatedVisibility(visible = minorExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundGray.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            violationState.minorViolations.forEach { violation ->
                                ViolationOptionRow(
                                    violation = violation,
                                    isSelected = selectedViolation == violation,
                                    onSelect = {
                                        selectedViolation = it
                                        minorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Major Offenses
                    ViolationCategoryHeader(
                        title = "Major Offenses",
                        icon = Icons.Default.Warning,
                        color = MajorRed,
                        isExpanded = majorExpanded,
                        onClick = {
                            majorExpanded = !majorExpanded
                            if (majorExpanded) minorExpanded = false
                        }
                    )
                    AnimatedVisibility(visible = majorExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundGray.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            violationState.majorViolations.forEach { violation ->
                                ViolationOptionRow(
                                    violation = violation,
                                    isSelected = selectedViolation == violation,
                                    onSelect = {
                                        selectedViolation = it
                                        majorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Display selected offense preview below if any selected
                    selectedViolation?.let { selected ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected.category == ViolationCategory.MAJOR) MajorRed.copy(alpha = 0.08f) else MinorOrange.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                if (selected.category == ViolationCategory.MAJOR) MajorRed.copy(alpha = 0.2f) else MinorOrange.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (selected.category == ViolationCategory.MAJOR) Icons.Default.Warning else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (selected.category == ViolationCategory.MAJOR) MajorRed else MinorOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Selected Offense:", 
                                        fontSize = 11.sp, 
                                        color = if (selected.category == ViolationCategory.MAJOR) MajorRed else MinorOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        selected.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                }
                                IconButton(onClick = { selectedViolation = null }) {
                                    Icon(
                                        Icons.Default.Clear, 
                                        contentDescription = "Clear offense", 
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DOCUMENTATION SECTION
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionLabel("DOCUMENTATION")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Attach evidence and provide additional context.", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Photo evidence box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BackgroundGray.copy(alpha = 0.4f))
                            .border(1.5.dp, BorderColor, RoundedCornerShape(18.dp))
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (violationState.isUploadingImage) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PurplePrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Uploading Image...", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            }
                        } else if (violationState.evidenceImageUrl != null) {
                            AsyncImage(
                                model = violationState.evidenceImageUrl,
                                contentDescription = "Evidence Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Remove image overlay button
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .clickable { violationViewModel.removeEvidenceImage() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(PurpleContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Attach Photo Evidence", color = PurplePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Tap to select image from gallery", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location field
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Where did this happen? (e.g. Room 301, Main Gate)", color = TextSecondary.copy(alpha = 0.8f), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PurplePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BackgroundGray.copy(alpha = 0.3f),
                            unfocusedContainerColor = BackgroundGray.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Remarks
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        placeholder = { Text("Write some additional details or incident notes...", color = TextSecondary.copy(alpha = 0.8f), fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = BackgroundGray.copy(alpha = 0.3f),
                            unfocusedContainerColor = BackgroundGray.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include important details for fast review", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                        Text("${remarks.length}/50+", fontSize = 10.sp, color = if (remarks.length >= 50) SuccessGreen else TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit button
            if (violationState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            SVMSButton(
                text = "Submit Violation Report",
                onClick = {
                    violationViewModel.submitViolation(
                        student = scannerState.scannedStudent,
                        violationTypeItem = selectedViolation,
                        remarks = remarks,
                        location = location
                    )
                },
                isLoading = violationState.isLoading,
                isPrimary = true,
                icon = Icons.Default.CloudUpload
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ViolationCategoryHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, if (isExpanded) color else BorderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title, 
            fontWeight = FontWeight.Bold, 
            fontSize = 13.sp, 
            color = TextPrimary, 
            modifier = Modifier.weight(1f)
        )
        Icon(
            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ViolationOptionRow(
    violation: ViolationTypeItem,
    isSelected: Boolean,
    onSelect: (ViolationTypeItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurpleContainer.copy(alpha = 0.7f) else Color.Transparent)
            .clickable { onSelect(violation) }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(violation) },
            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = violation.name,
            fontSize = 12.sp,
            color = if (isSelected) PurplePrimary else TextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            lineHeight = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
