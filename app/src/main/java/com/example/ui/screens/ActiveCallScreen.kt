package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IosKeypadButton
import com.example.ui.theme.*

@Composable
fun ActiveCallScreen(
    contactName: String,
    phoneNumber: String,
    isIncoming: Boolean,
    isConnected: Boolean,
    durationSeconds: Int,
    isMuted: Boolean,
    isSpeakerOn: Boolean,
    isKeypadOpen: Boolean,
    inCallDialedString: String,
    onAcceptCall: () -> Unit,
    onEndCall: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onToggleKeypad: () -> Unit,
    onInCallDigitClick: (String) -> Unit
) {
    // Pulse animation for call ring
    val infiniteTransition = rememberInfiniteTransition(label = "RingPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val formatSeconds = { seconds: Int ->
        val mins = seconds / 60
        val secs = seconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1C1C1E), Color(0xFF000000))
                )
            )
            .padding(24.dp)
            .testTag("active_call_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = contactName,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isIncoming) "iPhone Audio..."
                           else if (!isConnected) "calling..."
                           else formatSeconds(durationSeconds),
                    color = IosDarkTextSecondary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                if (inCallDialedString.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = inCallDialedString,
                        color = IosBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            // Big Contact Avatar with pulse
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(if (!isConnected || isIncoming) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(IosBlue)
            ) {
                Text(
                    text = contactName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // In-Call Dialpad Sheet Overlay if toggled
            if (isKeypadOpen) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(IosDarkSurface.copy(alpha = 0.95f))
                        .padding(16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IosKeypadButton("1", "", { onInCallDigitClick("1") })
                        IosKeypadButton("2", "ABC", { onInCallDigitClick("2") })
                        IosKeypadButton("3", "DEF", { onInCallDigitClick("3") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IosKeypadButton("4", "GHI", { onInCallDigitClick("4") })
                        IosKeypadButton("5", "JKL", { onInCallDigitClick("5") })
                        IosKeypadButton("6", "MNO", { onInCallDigitClick("6") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IosKeypadButton("7", "PQRS", { onInCallDigitClick("7") })
                        IosKeypadButton("8", "TUV", { onInCallDigitClick("8") })
                        IosKeypadButton("9", "WXYZ", { onInCallDigitClick("9") })
                    }
                    TextButton(onClick = onToggleKeypad) {
                        Text("Hide Keypad", color = IosBlue, fontSize = 16.sp)
                    }
                }
            } else {
                // Standard 6-Grid Call Control Actions
                if (!isIncoming) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                            InCallActionButton(
                                icon = Icons.Default.MicOff,
                                label = "mute",
                                isActive = isMuted,
                                onClick = onToggleMute
                            )
                            InCallActionButton(
                                icon = Icons.Default.Dialpad,
                                label = "keypad",
                                isActive = isKeypadOpen,
                                onClick = onToggleKeypad
                            )
                            InCallActionButton(
                                icon = Icons.Default.VolumeUp,
                                label = "speaker",
                                isActive = isSpeakerOn,
                                onClick = onToggleSpeaker
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                            InCallActionButton(
                                icon = Icons.Default.Add,
                                label = "add call",
                                isActive = false,
                                onClick = {}
                            )
                            InCallActionButton(
                                icon = Icons.Default.Videocam,
                                label = "FaceTime",
                                isActive = false,
                                onClick = {}
                            )
                            InCallActionButton(
                                icon = Icons.Default.Person,
                                label = "contacts",
                                isActive = false,
                                onClick = {}
                            )
                        }
                    }
                }
            }

            // Bottom Call Action Button Row (Accept / Decline or Red End Call Button)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isIncoming) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Decline Call (Red)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(IosRed)
                                    .clickable { onEndCall() }
                                    .testTag("decline_call_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Decline Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Decline", color = Color.White, fontSize = 13.sp)
                        }

                        // Accept Call (Green)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(IosGreen)
                                    .clickable { onAcceptCall() }
                                    .testTag("accept_call_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Accept Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Accept", color = Color.White, fontSize = 13.sp)
                        }
                    }
                } else {
                    // Ongoing Call End Button (Red Circle)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(IosRed)
                            .clickable { onEndCall() }
                            .testTag("end_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InCallActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isActive) Color.White else IosDarkSurface)
                .clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
