package com.svms.app.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.svms.app.data.model.ViolationCategory
import com.svms.app.presentation.notification.NotificationViewModel
import com.svms.app.presentation.shared.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // History is automatically refreshed in ViewModel init

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    val todayDate = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "History", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        if (state.currentUser?.profileImageUrl != null) {
                            AsyncImage(
                                model = state.currentUser?.profileImageUrl,
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
                                    text = state.currentUser?.firstName?.take(1) ?: "A",
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
                    selected = false,
                    onClick = onNavigateToScan,
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan", modifier = Modifier.size(24.dp)) },
                    label = { Text("Scan", fontWeight = FontWeight.Medium, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = PurpleLight
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(24.dp)) },
                    label = { Text("History", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        "Today's Reports",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Summary of disciplinary actions for $todayDate",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
                        fontWeight = FontWeight.Medium
                    )

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total incidents
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                SectionLabel("TOTAL INCIDENTS")
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    state.totalIncidents.toString(),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    letterSpacing = (-1).sp
                                )
                            }
                        }

                        // Pending review
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PurplePrimary),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(12.dp)
                                            .clip(RoundedCornerShape(1.5.dp))
                                            .background(GoldAccent)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PENDING REVIEW",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    String.format("%02d", state.pendingReview),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = (-1).sp
                                )
                            }
                        }
                    }
                }
            }

            // Recent Submissions header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Submissions", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = TextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "LIVE FEED", 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = SuccessGreen, 
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PurplePrimary, modifier = Modifier.size(36.dp))
                    }
                }
            } else if (state.violations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircleOutline, 
                                contentDescription = null, 
                                tint = TextSecondary.copy(alpha = 0.4f), 
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "No violations recorded yet today.", 
                                color = TextSecondary, 
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(state.violations) { violation ->
                    ViolationCard(
                        violation = violation,
                        onClick = { onNavigateToDetails(violation.violationId) }
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckBox, 
                            contentDescription = null, 
                            tint = TextSecondary.copy(alpha = 0.3f), 
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "END OF TODAY'S LOGS", 
                            fontSize = 11.sp, 
                            color = TextSecondary.copy(alpha = 0.4f), 
                            letterSpacing = 1.5.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ViolationCard(violation: Violation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Avatar Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PurpleContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = violation.studentName.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Middle: Student Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = violation.studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        maxLines = 1,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "Institutional ID: ${violation.studentId}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Right: Category Badge
                ViolationCategoryChip(category = violation.violationCategory)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Row (Similar to the reference grid)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundGray.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("OFFENSE TYPE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 0.5.sp)
                    Text(violation.violationType, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurplePrimary, maxLines = 1)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("RECORDED AT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 0.5.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(violation.violationTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            if (violation.evidenceImageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = violation.evidenceImageUrl,
                    contentDescription = "Evidence Preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
