package com.svms.app.presentation.violation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.svms.app.data.model.Violation
import com.svms.app.presentation.shared.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViolationDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ViolationDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Report Details", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PurplePrimary,
                    strokeWidth = 3.dp
                )
            } else if (state.error != null) {
                // Error handled via Snackbar
            } else {
                state.violation?.let { violation ->
                    ViolationDetailsContent(violation)
                }
            }
        }
    }
}

@Composable
private fun ViolationDetailsContent(violation: Violation) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status and Basic Info Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ViolationCategoryChip(category = violation.violationCategory)
                    StatusChip(status = violation.status)
                }
                
                Spacer(modifier = Modifier.height(18.dp))
                
                Text(
                    text = violation.violationType,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    lineHeight = 28.sp,
                    letterSpacing = (-0.5).sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundGray.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(violation.violationDate, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundGray.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(violation.violationTime, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Student Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("STUDENT INFORMATION")
                Spacer(modifier = Modifier.height(20.dp))
                
                InfoRowWithIcon(icon = Icons.Default.Person, label = "Student Full Name", value = violation.studentName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                InfoRowWithIcon(icon = Icons.Default.School, label = "College / Department", value = violation.studentCollege ?: "Not Specified")
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                InfoRowWithIcon(icon = Icons.Default.Book, label = "Course & Program", value = violation.studentCourse ?: "Not Specified")
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                InfoRowWithIcon(icon = Icons.Default.Badge, label = "Institutional Identification", value = violation.studentId.toString())
            }
        }

        // Incident Documentation Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("DOCUMENTATION")
                Spacer(modifier = Modifier.height(18.dp))
                
                if (violation.evidenceImageUrl != null) {
                    AsyncImage(
                        model = violation.evidenceImageUrl,
                        contentDescription = "Evidence Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BackgroundGray)
                            .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
                
                val location = violation.description?.substringBefore("\n")?.removePrefix("Location: ") ?: "Not specified"
                val remarks = violation.description?.substringAfter("\n") ?: violation.description ?: "No remarks provided."

                InfoRowWithIcon(icon = Icons.Default.LocationOn, label = "Incident Primary Location", value = location)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Administrator Remarks", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = BackgroundGray.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = remarks,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Audit Trail Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("AUDIT TRAIL")
                Spacer(modifier = Modifier.height(18.dp))
                
                InfoRowWithIcon(icon = Icons.Default.Security, label = "Reporting Security Officer", value = violation.guardName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PurpleContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("System Report UUID", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        Text(
                            text = violation.violationId.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = BorderColor.copy(alpha = 0.4f))
                
                // Static indicator for read-only
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "This report is digitally verified and archived for compliance monitoring.",
                            fontSize = 12.sp,
                            color = Color(0xFF1565C0),
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StatusChip(status: String) {
    val color = when (status.uppercase()) {
        "APPROVED" -> SuccessGreen
        "PENDING" -> GoldAccent
        "REJECTED" -> ErrorRed
        else -> TextSecondary
    }
    
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
private fun InfoRowWithIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PurpleContainer.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        }
    }
}
