package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
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
import com.example.data.ContactEntity
import com.example.ui.theme.*

@Composable
fun FavoritesScreen(
    favorites: List<ContactEntity>,
    onContactClick: (ContactEntity) -> Unit,
    onCallClick: (String) -> Unit
) {
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
                text = "Favorites",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Favorites Yet",
                    color = IosDarkTextSecondary,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(favorites) { contact ->
                    FavoriteRow(
                        contact = contact,
                        onCallClick = { onCallClick(contact.phoneMobile) },
                        onInfoClick = { onContactClick(contact) }
                    )
                    HorizontalDivider(color = IosDarkCard, thickness = 0.5.dp, modifier = Modifier.padding(start = 56.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteRow(
    contact: ContactEntity,
    onCallClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(contact.colorHex))
    } catch (e: Exception) {
        IosBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCallClick)
            .padding(vertical = 12.dp)
            .testTag("favorite_item_${contact.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(parsedColor)
        ) {
            Text(
                text = contact.displayInitials,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.fullName,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${contact.tag} • ${contact.phoneMobile}",
                color = IosDarkTextSecondary,
                fontSize = 13.sp
            )
        }

        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Details",
                tint = IosBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
