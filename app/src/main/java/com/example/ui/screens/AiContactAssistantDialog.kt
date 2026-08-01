package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ai.ExtractedContact
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.IosDarkSurface
import com.example.ui.theme.IosPurple

@Composable
fun AiContactAssistantDialog(
    onDismiss: () -> Unit,
    onExtract: (String, (ExtractedContact) -> Unit) -> Unit,
    onSaveContact: (ExtractedContact) -> Unit,
    isLoading: Boolean
) {
    var rawText by remember { mutableStateOf("") }
    var extractedResult by remember { mutableStateOf<ExtractedContact?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = IosDarkSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("ai_assistant_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = IosPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Gemini Contact AI",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Paste any message, email signature, business card, or note. Gemini AI will parse mobile number, name, company, email and organize it automatically.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = { Text("e.g., Save Marcus Vance, VP at Tech, phone 555-888-2020, email marcus@tech.io", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("ai_dialog_text_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IosPurple,
                        unfocusedBorderColor = IosDarkBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (extractedResult == null) {
                    Button(
                        onClick = {
                            onExtract(rawText) { res ->
                                extractedResult = res
                            }
                        },
                        enabled = rawText.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = IosPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_dialog_parse_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Parse & Organize Contact", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Preview extracted contact fields
                    val res = extractedResult!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(IosDarkBackground, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Extracted Contact:", color = IosPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Name: ${res.firstName} ${res.lastName}", color = Color.White, fontSize = 14.sp)
                        Text("Mobile: ${res.mobile}", color = Color.White, fontSize = 14.sp)
                        if (res.company.isNotEmpty()) Text("Company: ${res.company}", color = Color.White, fontSize = 14.sp)
                        if (res.email.isNotEmpty()) Text("Email: ${res.email}", color = Color.White, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onSaveContact(res)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IosPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_dialog_save_btn")
                    ) {
                        Text("Save To Contacts", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
