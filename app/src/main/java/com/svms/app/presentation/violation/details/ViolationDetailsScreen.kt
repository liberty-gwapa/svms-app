package com.svms.app.presentation.violation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
                    Text("Report Details", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PurpleDark)
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
                    color = PurplePrimary
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status and Basic Info Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = violation.violationType,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    lineHeight = 28.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(violation.violationDate, fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(violation.violationTime, fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Student Details Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("STUDENT INFORMATION")
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRowWithIcon(icon = Icons.Default.Person, label = "Student Name", value = violation.studentName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor.copy(alpha = 0.5f))
                InfoRowWithIcon(icon = Icons.Default.Badge, label = "Institutional ID", value = violation.studentId.toString())
            }
        }

        // Incident Documentation Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("INCIDENT DOCUMENTATION")
                Spacer(modifier = Modifier.height(16.dp))
                
                if (violation.evidenceImageUrl != null) {
                    AsyncImage(
                        model = violation.evidenceImageUrl,
                        contentDescription = "Evidence Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                val location = violation.description?.substringBefore("\n")?.removePrefix("Location: ") ?: "Not specified"
                val remarks = violation.description?.substringAfter("\n") ?: violation.description ?: "No remarks provided."

                InfoRowWithIcon(icon = Icons.Default.LocationOn, label = "Incident Location", value = location)
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor.copy(alpha = 0.5f))
                
                Row {
                    Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Additional Remarks", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remarks,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Audit Trail Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SectionLabel("ADMINISTRATIVE DETAILS")
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRowWithIcon(icon = Icons.Default.Security, label = "Reporting Officer", value = violation.guardName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor.copy(alpha = 0.5f))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("System Report UUID", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = violation.violationId.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextSecondary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor.copy(alpha = 0.5f))
                
                // Static indicator for read-only
                Surface(
                    color = BackgroundGray,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "This report is verified and locked for compliance.",
                            fontSize = 11.sp,
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
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
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun InfoRowWithIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}
