package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contacts: List<ContactEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onContactClick: (ContactEntity) -> Unit,
    onAddContactClick: () -> Unit,
    onAiImportClick: () -> Unit
) {
    val groupedContacts = contacts.groupBy {
        it.firstName.firstOrNull()?.uppercaseChar() ?: '#'
    }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
    ) {
        // Top iOS Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gemini AI Smart Paste button
            Surface(
                onClick = onAiImportClick,
                shape = RoundedCornerShape(20.dp),
                color = IosPurple.copy(alpha = 0.2f),
                contentColor = IosPurple,
                modifier = Modifier.testTag("ai_import_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Import",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "AI Import", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Contacts",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Add (+) button
            IconButton(
                onClick = onAddContactClick,
                modifier = Modifier.testTag("add_contact_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact",
                    tint = IosBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search", color = IosDarkTextSecondary, fontSize = 15.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = IosDarkTextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = IosDarkTextSecondary)
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = IosDarkCard,
                unfocusedContainerColor = IosDarkCard,
                disabledContainerColor = IosDarkCard,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .height(48.dp)
                .testTag("contacts_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Contact List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // My Card Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val myCard = ContactEntity(
                                firstName = "My",
                                lastName = "Card",
                                phoneMobile = "+1 (555) 000-1122",
                                email = "user@aistudio.app",
                                company = "Personal Profile",
                                colorHex = "#007AFF"
                            )
                            onContactClick(myCard)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(IosBlue)
                    ) {
                        Text(
                            text = "ME",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "My Card",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "My Contact Information & Poster",
                            color = IosDarkTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                HorizontalDivider(
                    color = IosDarkCard,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 86.dp)
                )
            }

            // Grouped Contact Sections
            groupedContacts.forEach { (initial, contactList) ->
                item {
                    Text(
                        text = initial.toString(),
                        color = IosDarkTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IosDarkSurface)
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                items(contactList) { contact ->
                    ContactRow(
                        contact = contact,
                        onClick = { onContactClick(contact) }
                    )
                    HorizontalDivider(
                        color = IosDarkCard,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(start = 72.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactRow(
    contact: ContactEntity,
    onClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(contact.colorHex))
    } catch (e: Exception) {
        IosBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("contact_item_${contact.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Monogram / Avatar Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
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
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (contact.company.isNotEmpty() || contact.jobTitle.isNotEmpty()) {
                val subtitle = listOf(contact.jobTitle, contact.company)
                    .filter { it.isNotEmpty() }
                    .joinToString(" • ")
                Text(
                    text = subtitle,
                    color = IosDarkTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}
