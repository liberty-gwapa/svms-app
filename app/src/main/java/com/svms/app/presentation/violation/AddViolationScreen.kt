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
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(50))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                }
            },
            title = { Text("Violation Recorded", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("The violation has been successfully recorded in the system.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        violationViewModel.resetSuccess()
                        scannerViewModel.resetNavigation()
                        onNavigateToHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) { Text("View History") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    violationViewModel.resetSuccess()
                    scannerViewModel.resetNavigation()
                }) { Text("Add Another", color = PurplePrimary) }
            },
            containerColor = CardWhite
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Violation", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleDark)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = CardWhite) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan") },
                    label = { Text("Scan") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurplePrimary,
                        selectedTextColor = PurplePrimary,
                        indicatorColor = PurpleContainer
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                    label = { Text("History") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionLabel("STUDENT IDENTIFICATION")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Identify the student involved in the incident.",
                            fontSize = 12.sp, color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show identified student or scan button
                        if (scannerState.scannedStudent != null) {
                            val student = scannerState.scannedStudent!!
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PurpleContainer)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(PurplePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        student.firstName.take(1) + student.lastName.take(1),
                                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                    Text("${student.schoolId} • ${student.course} • ${student.yearLevel}", fontSize = 12.sp, color = TextSecondary)
                                }
                                IconButton(onClick = { scannerViewModel.resetNavigation() }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        } else {
                            SVMSButton(
                                text = "Scan Student ID",
                                onClick = {
                                    if (cameraPermission.status.isGranted) {
                                        showScanner = true
                                    } else {
                                        cameraPermission.launchPermissionRequest()
                                    }
                                },
                                icon = Icons.Default.QrCodeScanner
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Manual entry
                            OutlinedTextField(
                                value = scannerState.searchQuery,
                                onValueChange = scannerViewModel::onSearchQueryChange,
                                placeholder = { Text("Enter Student Name or ID Number", color = TextSecondary, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PurplePrimary) },
                                trailingIcon = {
                                    if (scannerState.searchQuery.isNotBlank()) {
                                        IconButton(onClick = scannerViewModel::onManualSearch) {
                                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PurplePrimary)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PurplePrimary,
                                    unfocusedBorderColor = BorderColor,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CardWhite,
                                    unfocusedContainerColor = CardWhite
                                )
                            )

                            // Search results dropdown
                            if (scannerState.searchResults.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column {
                                        scannerState.searchResults.take(4).forEach { student ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { scannerViewModel.onStudentSelected(student) }
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(student.fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                                                    Text("${student.schoolId} • ${student.course}", fontSize = 12.sp, color = TextSecondary)
                                                }
                                            }
                                            if (student != scannerState.searchResults.last()) HorizontalDivider(color = BorderColor)
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

                Spacer(modifier = Modifier.height(12.dp))

                // SELECT OFFENSES SECTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionLabel("SELECT OFFENSES")

                        Spacer(modifier = Modifier.height(10.dp))

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
                            Column(modifier = Modifier.padding(top = 6.dp)) {
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

                        Spacer(modifier = Modifier.height(8.dp))

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
                            Column(modifier = Modifier.padding(top = 6.dp)) {
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // DOCUMENTATION SECTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionLabel("DOCUMENTATION")
                        Text("Attach evidence and provide additional context.", fontSize = 12.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Photo evidence button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleContainer.copy(alpha = 0.5f))
                                .border(2.dp, PurplePrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (violationState.isUploadingImage) {
                                CircularProgressIndicator(color = PurplePrimary)
                            } else if (violationState.evidenceImageUrl != null) {
                                AsyncImage(
                                    model = violationState.evidenceImageUrl,
                                    contentDescription = "Evidence",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .clickable { violationViewModel.removeEvidenceImage() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Attach Photo Evidence", color = PurplePrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Location field
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            placeholder = { Text("Location (e.g. Room 301, Main Gate)", color = TextSecondary, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PurplePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Remarks
                        OutlinedTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            placeholder = { Text("Enter Additional Remarks...", color = TextSecondary, fontSize = 13.sp) },
                            modifier = Modifier.fillMaxWidth().height(110.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CardWhite,
                                unfocusedContainerColor = CardWhite
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Minimum 50 characters recommended", fontSize = 10.sp, color = TextSecondary)
                            Text("${remarks.length}/50", fontSize = 10.sp, color = if (remarks.length >= 50) SuccessGreen else TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                if (violationState.error != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                SVMSButton(
                    text = "Submit Violation",
                    onClick = {
                        violationViewModel.submitViolation(
                            student = scannerState.scannedStudent,
                            violationTypeItem = selectedViolation,
                            remarks = remarks,
                            location = location
                        )
                    },
                    isLoading = violationState.isLoading,
                    isPrimary = false,
                    icon = Icons.Default.CloudUpload
                )

                Spacer(modifier = Modifier.height(16.dp))
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
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(
            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = TextSecondary
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
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PurpleContainer else Color.Transparent)
            .clickable { onSelect(violation) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(violation) },
            colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            violation.name,
            fontSize = 13.sp,
            color = if (isSelected) PurplePrimary else TextPrimary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
