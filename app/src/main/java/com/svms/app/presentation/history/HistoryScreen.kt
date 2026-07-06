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
                        // Today's Reports
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setFilter(HistoryFilter.TODAY) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.currentFilter == HistoryFilter.TODAY) PurplePrimary else CardWhite
                            ),
                            elevation = CardDefaults.cardElevation(if (state.currentFilter == HistoryFilter.TODAY) 4.dp else 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (state.currentFilter == HistoryFilter.TODAY) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(12.dp)
                                                .clip(RoundedCornerShape(1.5.dp))
                                                .background(GoldAccent)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = "TODAY'S REPORTS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.currentFilter == HistoryFilter.TODAY) GoldAccent else TextSecondary,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    state.todayReportsCount.toString(),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (state.currentFilter == HistoryFilter.TODAY) Color.White else TextPrimary,
                                    letterSpacing = (-1).sp
                                )
                            }
                        }

                        // Total Incidents
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setFilter(HistoryFilter.ALL_TIME) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.currentFilter == HistoryFilter.ALL_TIME) PurplePrimary else CardWhite
                            ),
                            elevation = CardDefaults.cardElevation(if (state.currentFilter == HistoryFilter.ALL_TIME) 4.dp else 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (state.currentFilter == HistoryFilter.ALL_TIME) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(12.dp)
                                                .clip(RoundedCornerShape(1.5.dp))
                                                .background(GoldAccent)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = "TOTAL INCIDENTS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.currentFilter == HistoryFilter.ALL_TIME) GoldAccent else TextSecondary,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    String.format("%02d", state.totalIncidents),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (state.currentFilter == HistoryFilter.ALL_TIME) Color.White else TextPrimary,
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
                        if (state.currentFilter == HistoryFilter.TODAY) "Today's Submissions" else "All-Time Submissions", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = TextPrimary,
                        letterSpacing = (-0.2).sp
                    )

                    // Department Dropdown (Relocated from left to right, replacing LIVE FEED)
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(PurpleContainer.copy(alpha = 0.5f))
                                .border(1.dp, PurplePrimary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .clickable { expanded = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.selectedDepartment?.uppercase() ?: "ALL DEPARTMENTS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(CardWhite)
                                .widthIn(min = 150.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("ALL DEPARTMENTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PurplePrimary) },
                                onClick = {
                                    viewModel.setSelectedDepartment(null)
                                    expanded = false
                                }
                            )
                            state.departments.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept.departmentKey.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        viewModel.setSelectedDepartment(dept.departmentKey)
                                        expanded = false
                                    }
                                )
                            }
                        }
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
            } else if (state.filteredViolations.isEmpty()) {
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
                                if (state.currentFilter == HistoryFilter.TODAY) "No violations recorded yet today." else "No violations found in records.", 
                                color = TextSecondary, 
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                items(state.filteredViolations) { violation ->
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
                            if (state.currentFilter == HistoryFilter.TODAY) "END OF TODAY'S LOGS" else "END OF ALL LOGS", 
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
                        text = "ID: ${violation.studentId} • ${violation.violationDate}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Right: Category Badge
                ViolationCategoryChip(category = violation.violationCategory)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundGray.copy(alpha = 0.5f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("COLLEGE : COURSE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 0.5.sp)
                    Text(
                        text = "${violation.studentCollege ?: "N/A"} : ${violation.studentCourse ?: "N/A"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary,
                        maxLines = 1
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("STATUS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 0.5.sp)
                    Text(
                        violation.status, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = if (violation.status == "PENDING") GoldAccent else SuccessGreen
                    )
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
