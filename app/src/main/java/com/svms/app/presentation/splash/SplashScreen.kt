package com.svms.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svms.app.R
import com.svms.app.presentation.shared.PurpleDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNextScreen: (Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val alpha = remember { Animatable(0f) }
    val isReady by viewModel.isReady.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    LaunchedEffect(isReady) {
        if (isReady) {
            onNextScreen(isLoggedIn)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.bisulogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(150.dp)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SVMS",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PurpleDark,
                modifier = Modifier.alpha(alpha.value)
            )
            Text(
                text = "Student Violation Monitoring System",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
