package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.IosKeypadButton
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosDarkBackground
import com.example.ui.theme.IosGreen

@Composable
fun KeypadScreen(
    dialedNumber: String,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onCallClick: (String) -> Unit,
    onAddNumberClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IosDarkBackground)
            .padding(horizontal = 24.dp)
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display dialed digits area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dialedNumber.ifEmpty { "" },
                color = Color.White,
                fontSize = if (dialedNumber.length > 11) 28.sp else 36.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .testTag("dialed_number_display")
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = dialedNumber.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAddNumberClick(dialedNumber) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("add_number_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Contact",
                        tint = IosBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add Number",
                        color = IosBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Keypad 3x4 Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                IosKeypadButton("1", "", { onDigitClick("1") })
                IosKeypadButton("2", "A B C", { onDigitClick("2") })
                IosKeypadButton("3", "D E F", { onDigitClick("3") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                IosKeypadButton("4", "G H I", { onDigitClick("4") })
                IosKeypadButton("5", "J K L", { onDigitClick("5") })
                IosKeypadButton("6", "M N O", { onDigitClick("6") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                IosKeypadButton("7", "P Q R S", { onDigitClick("7") })
                IosKeypadButton("8", "T U V", { onDigitClick("8") })
                IosKeypadButton("9", "W X Y Z", { onDigitClick("9") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                IosKeypadButton("*", "", { onDigitClick("*") })
                IosKeypadButton("0", "+", { onDigitClick("0") })
                IosKeypadButton("#", "", { onDigitClick("#") })
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Green Call Button & Backspace
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Call Green Circle Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(IosGreen)
                        .clickable {
                            if (dialedNumber.isNotEmpty()) {
                                onCallClick(dialedNumber)
                            } else {
                                onCallClick("+1 (555) 234-5678")
                            }
                        }
                        .testTag("dial_call_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Backspace button on right
                if (dialedNumber.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    ) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.testTag("dial_delete_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Delete Digit",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
