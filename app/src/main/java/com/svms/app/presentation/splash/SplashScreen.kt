package com.svms.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svms.app.R
import com.svms.app.presentation.shared.PurpleDark
import com.svms.app.presentation.shared.PurplePrimary
import com.svms.app.presentation.shared.PurpleLight
import com.svms.app.presentation.shared.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNextScreen: (Boolean) -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.9f) }
    val isReady by viewModel.isReady.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(key1 = true) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(isReady) {
        if (isReady) {
            delay(500) // Small extra delay for smooth transition
            onNextScreen(isLoggedIn)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .size(240.dp)
                .clip(CircleShape)
                .background(PurplePrimary.copy(alpha = 0.05f))
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(PurpleLight.copy(alpha = 0.05f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value).scale(scale.value)
        ) {
            Surface(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(12.dp, CircleShape, spotColor = PurplePrimary.copy(alpha = 0.3f)),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.bisulogo),
                        contentDescription = "Institutional Logo",
                        modifier = Modifier.size(110.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "SVMS",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = PurpleDark,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Student Violation Monitoring System",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            CircularProgressIndicator(
                color = PurplePrimary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
        
        // Footer branding
        Text(
            text = "INSTITUTIONAL ADMINISTRATION",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = PurplePrimary,
            letterSpacing = 2.sp
        )
    }
}
