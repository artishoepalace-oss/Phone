package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoicemailEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VoicemailScreen(
    voicemails: List<VoicemailEntity>,
    onCallClick: (String, String) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onMarkRead: (Long) -> Unit
) {
    var expandedVoicemailId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Voicemail",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (voicemails.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Voicemails",
                    color = IosDarkTextSecondary,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(voicemails) { vm ->
                    val isExpanded = expandedVoicemailId == vm.id
                    VoicemailRow(
                        voicemail = vm,
                        isExpanded = isExpanded,
                        onExpandToggle = {
                            expandedVoicemailId = if (isExpanded) null else vm.id
                            if (!vm.isRead) onMarkRead(vm.id)
                        },
                        onCallClick = { onCallClick(vm.phoneNumber, vm.contactName) },
                        onDeleteClick = { onDeleteClick(vm.id) }
                    )
                    HorizontalDivider(color = IosDarkCard, thickness = 0.5.dp, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
fun VoicemailRow(
    voicemail: VoicemailEntity,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onCallClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0.3f) }
    val timeFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(voicemail.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpandToggle)
            .padding(vertical = 12.dp)
            .testTag("voicemail_item_${voicemail.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unread blue dot indicator
            if (!voicemail.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(IosBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voicemail.contactName,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = if (!voicemail.isRead) FontWeight.Bold else FontWeight.SemiBold
                )
                Text(
                    text = voicemail.phoneNumber,
                    color = IosDarkTextSecondary,
                    fontSize = 13.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = timeFormatted,
                    color = IosDarkTextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "0:${if (voicemail.durationSeconds < 10) "0" else ""}${voicemail.durationSeconds}",
                    color = IosDarkTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Expanded Audio Player & Gemini AI Transcript
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosDarkSurface)
                    .padding(14.dp)
            ) {
                // AI Transcript Box
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Transcript",
                        tint = IosPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Gemini AI Live Transcript",
                        color = IosPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = voicemail.transcript,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Waveform / Audio Slider Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IosBlue)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Slider(
                        value = playbackProgress,
                        onValueChange = { playbackProgress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = IosBlue,
                            activeTrackColor = IosBlue,
                            inactiveTrackColor = IosDarkCard
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Callback & Delete Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = IosRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Button(
                        onClick = onCallClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IosGreen),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Back",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Back", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
