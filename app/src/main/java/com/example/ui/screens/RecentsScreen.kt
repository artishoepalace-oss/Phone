package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallLogEntity
import com.example.data.CallType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecentsScreen(
    callLogs: List<CallLogEntity>,
    isMissedOnly: Boolean,
    onFilterChanged: (Boolean) -> Unit,
    onCallClick: (String, String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
    ) {
        // Top Header with Segmented Control
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recents",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                if (callLogs.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Clear", color = IosBlue, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Segmented Toggle (All / Missed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(IosDarkCard)
                    .padding(3.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!isMissedOnly) IosDarkSurface else Color.Transparent)
                        .clickable { onFilterChanged(false) }
                        .padding(vertical = 6.dp)
                        .testTag("recents_all_tab")
                ) {
                    Text(
                        text = "All",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isMissedOnly) IosDarkSurface else Color.Transparent)
                        .clickable { onFilterChanged(true) }
                        .padding(vertical = 6.dp)
                        .testTag("recents_missed_tab")
                ) {
                    Text(
                        text = "Missed",
                        color = if (isMissedOnly) IosRed else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (callLogs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isMissedOnly) "No Missed Calls" else "No Recent Calls",
                    color = IosDarkTextSecondary,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(callLogs) { log ->
                    CallLogRow(
                        log = log,
                        onCallClick = { onCallClick(log.phoneNumber, log.contactName) }
                    )
                    HorizontalDivider(color = IosDarkCard, thickness = 0.5.dp, modifier = Modifier.padding(start = 36.dp))
                }
            }
        }
    }
}

@Composable
fun CallLogRow(
    log: CallLogEntity,
    onCallClick: () -> Unit
) {
    val isMissed = log.callType == CallType.MISSED
    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(vertical = 10.dp)
            .testTag("call_log_item_${log.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call type icon
            Icon(
                imageVector = when (log.callType) {
                    CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                    CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                    CallType.MISSED -> Icons.Default.CallMissed
                },
                contentDescription = log.callType.name,
                tint = if (isMissed) IosRed else IosDarkTextSecondary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.contactName,
                    color = if (isMissed) IosRed else Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${log.location} • ${log.phoneNumber}",
                    color = IosDarkTextSecondary,
                    fontSize = 13.sp
                )
            }

            Text(
                text = timeFormatted,
                color = IosDarkTextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = IosBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        // Gemini AI Summary Pill if available
        if (!log.aiSummary.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .padding(start = 32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IosPurple.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Summary",
                    tint = IosPurple,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = log.aiSummary,
                    color = IosPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
