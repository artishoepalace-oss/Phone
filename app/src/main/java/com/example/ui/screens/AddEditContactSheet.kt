package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.data.ContactEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditContactSheet(
    initialContact: ContactEntity? = null,
    initialPhone: String = "",
    onDismiss: () -> Unit,
    onSave: (ContactEntity) -> Unit,
    onExtractAi: (String, (com.example.ai.ExtractedContact) -> Unit) -> Unit,
    isAiLoading: Boolean
) {
    var firstName by remember { mutableStateOf(initialContact?.firstName ?: "") }
    var lastName by remember { mutableStateOf(initialContact?.lastName ?: "") }
    var mobile by remember { mutableStateOf(initialContact?.phoneMobile ?: initialPhone) }
    var workPhone by remember { mutableStateOf(initialContact?.phoneWork ?: "") }
    var email by remember { mutableStateOf(initialContact?.email ?: "") }
    var company by remember { mutableStateOf(initialContact?.company ?: "") }
    var jobTitle by remember { mutableStateOf(initialContact?.jobTitle ?: "") }
    var notes by remember { mutableStateOf(initialContact?.notes ?: "") }

    var pasteTextForAi by remember { mutableStateOf("") }
    var isAiBoxExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("add_edit_contact_sheet")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = IosBlue, fontSize = 17.sp)
            }

            Text(
                text = if (initialContact == null) "New Contact" else "Edit Contact",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    if (firstName.isNotBlank() || lastName.isNotBlank() || mobile.isNotBlank()) {
                        val updated = (initialContact ?: ContactEntity(firstName = firstName, lastName = lastName, phoneMobile = mobile)).copy(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            phoneMobile = mobile.trim(),
                            phoneWork = workPhone.trim(),
                            email = email.trim(),
                            company = company.trim(),
                            jobTitle = jobTitle.trim(),
                            notes = notes.trim()
                        )
                        onSave(updated)
                    }
                },
                enabled = firstName.isNotBlank() || lastName.isNotBlank() || mobile.isNotBlank()
            ) {
                Text(
                    text = "Done",
                    color = if (firstName.isNotBlank() || lastName.isNotBlank() || mobile.isNotBlank()) IosBlue else IosDarkTextSecondary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Gemini AI Smart Import Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = IosPurple.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Auto-Fill",
                                tint = IosPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini AI Smart Auto-Fill",
                                color = IosPurple,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(onClick = { isAiBoxExpanded = !isAiBoxExpanded }) {
                            Text(
                                text = if (isAiBoxExpanded) "Close" else "Paste Text",
                                color = IosPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AnimatedVisibility(visible = isAiBoxExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "Paste business card, email signature, or raw text below:",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = pasteTextForAi,
                                onValueChange = { pasteTextForAi = it },
                                placeholder = { Text("e.g. John Doe, CEO at ACME, mobile +1 555-123-4567, email john@acme.com", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("ai_paste_text_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IosPurple,
                                    unfocusedBorderColor = IosDarkCard,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    onExtractAi(pasteTextForAi) { extracted ->
                                        firstName = extracted.firstName
                                        lastName = extracted.lastName
                                        if (extracted.mobile.isNotEmpty()) mobile = extracted.mobile
                                        if (extracted.workPhone.isNotEmpty()) workPhone = extracted.workPhone
                                        if (extracted.email.isNotEmpty()) email = extracted.email
                                        if (extracted.company.isNotEmpty()) company = extracted.company
                                        if (extracted.jobTitle.isNotEmpty()) jobTitle = extracted.jobTitle
                                        if (extracted.notes.isNotEmpty()) notes = extracted.notes
                                        isAiBoxExpanded = false
                                    }
                                },
                                enabled = pasteTextForAi.isNotBlank() && !isAiLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = IosPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_extract_action_btn")
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Extract Fields with Gemini AI", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Form Fields Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(IosDarkSurface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IosFormField("First Name", firstName) { firstName = it }
                HorizontalDivider(color = IosDarkCard)
                IosFormField("Last Name", lastName) { lastName = it }
                HorizontalDivider(color = IosDarkCard)
                IosFormField("Company", company) { company = it }
                HorizontalDivider(color = IosDarkCard)
                IosFormField("Job Title", jobTitle) { jobTitle = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(IosDarkSurface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IosFormField("Mobile Phone", mobile) { mobile = it }
                HorizontalDivider(color = IosDarkCard)
                IosFormField("Work Phone", workPhone) { workPhone = it }
                HorizontalDivider(color = IosDarkCard)
                IosFormField("Email", email) { email = it }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(IosDarkSurface)
                    .padding(16.dp)
            ) {
                Text("Notes", color = IosDarkTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Add notes...", color = IosDarkTextSecondary) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun IosFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = IosDarkTextSecondary,
            fontSize = 15.sp,
            modifier = Modifier.width(110.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, color = IosDarkTextSecondary.copy(alpha = 0.5f)) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
    }
}
