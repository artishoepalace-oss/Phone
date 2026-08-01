package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import com.example.ui.theme.*

@Composable
fun ContactDetailScreen(
    contact: ContactEntity,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onFavoriteToggle: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val themeColor = try {
        Color(android.graphics.Color.parseColor(contact.colorHex))
    } catch (e: Exception) {
        IosBlue
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("contact_detail_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = IosBlue,
                    modifier = Modifier.size(26.dp)
                )
            }

            Row {
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (contact.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (contact.isFavorite) IosYellow else IosBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }

                TextButton(
                    onClick = onEditClick,
                    modifier = Modifier.testTag("contact_detail_edit_btn")
                ) {
                    Text(text = "Edit", color = IosBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Contact Poster Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(themeColor.copy(alpha = 0.8f), IosDarkSurface)
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Big Circle Avatar
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = contact.displayInitials,
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = contact.fullName,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (contact.company.isNotEmpty() || contact.jobTitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOf(contact.jobTitle, contact.company).filter { it.isNotEmpty() }.joinToString(" • "),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Action Round Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickActionButton(
                            icon = Icons.Default.Call,
                            label = "call",
                            color = IosGreen,
                            onClick = { onCallClick(contact.phoneMobile) }
                        )
                        QuickActionButton(
                            icon = Icons.Default.Message,
                            label = "message",
                            color = IosBlue,
                            onClick = {}
                        )
                        QuickActionButton(
                            icon = Icons.Default.Videocam,
                            label = "FaceTime",
                            color = IosBlue,
                            onClick = { onCallClick(contact.phoneMobile) }
                        )
                        QuickActionButton(
                            icon = Icons.Default.Email,
                            label = "mail",
                            color = IosBlue,
                            onClick = {}
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Phone Numbers Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IosDarkSurface)
                    .padding(16.dp)
            ) {
                if (contact.phoneMobile.isNotEmpty()) {
                    ContactInfoRow(
                        label = "mobile",
                        value = contact.phoneMobile,
                        onClick = { onCallClick(contact.phoneMobile) }
                    )
                }
                if (contact.phoneWork.isNotEmpty()) {
                    HorizontalDivider(color = IosDarkCard, modifier = Modifier.padding(vertical = 10.dp))
                    ContactInfoRow(
                        label = "work",
                        value = contact.phoneWork,
                        onClick = { onCallClick(contact.phoneWork) }
                    )
                }
                if (contact.phoneHome.isNotEmpty()) {
                    HorizontalDivider(color = IosDarkCard, modifier = Modifier.padding(vertical = 10.dp))
                    ContactInfoRow(
                        label = "home",
                        value = contact.phoneHome,
                        onClick = { onCallClick(contact.phoneHome) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Card
            if (contact.email.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IosDarkSurface)
                        .padding(16.dp)
                ) {
                    ContactInfoRow(
                        label = "email",
                        value = contact.email,
                        onClick = {}
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Notes / AI Context Card
            if (contact.notes.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IosDarkSurface)
                        .padding(16.dp)
                ) {
                    Text(text = "Notes & Gemini AI Context", color = IosDarkTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = contact.notes, color = Color.White, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Delete Contact Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(IosDarkSurface)
                    .clickable { onDeleteClick() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Delete Contact",
                    color = IosRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color)
                .clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun ContactInfoRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, color = IosDarkTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = IosBlue, fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call",
            tint = IosBlue,
            modifier = Modifier.size(20.dp)
        )
    }
}
