package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomPermissionDialog(
    title: String,
    description: String,
    confirmationButtonText: String,
    onConfirm: () -> Unit,
    cancelButtonText: String? = null,
    onCancel: (() -> Unit)? = null
) {
    var isProcessing by remember { mutableStateOf(false) }
    
    Dialog(
        onDismissRequest = {
            if (!isProcessing) {
            }
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = !isProcessing 
        )
    ) {
        DialogContent(
            title = title,
            description = description,
            confirmationButtonText = confirmationButtonText,
            isProcessing = isProcessing,
            cancelButtonText = cancelButtonText,
            onConfirm = {
                if (!isProcessing) {
                    isProcessing = true
                    onConfirm()
                }
            },
            onCancel = onCancel
        )
    }
}

@Composable
private fun DialogContent(
    title: String,
    description: String,
    confirmationButtonText: String,
    isProcessing: Boolean,
    cancelButtonText: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitle(title)
            Spacer(modifier = Modifier.height(8.dp))
            DialogDescription(description)
            Spacer(modifier = Modifier.height(24.dp))
            DialogButtons(
                confirmationButtonText = confirmationButtonText,
                isProcessing = isProcessing,
                cancelButtonText = cancelButtonText,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun DialogTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun DialogDescription(description: String) {
    Text(
        text = description,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
private fun DialogButtons(
    confirmationButtonText: String,
    isProcessing: Boolean,
    cancelButtonText: String? = null,
    onConfirm: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    if (cancelButtonText != null && onCancel != null) {
        // Botones verticales (uno sobre otro) para iOS
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConfirmButton(
                text = confirmationButtonText,
                isProcessing = isProcessing,
                onClick = onConfirm
            )
            CancelButton(
                text = cancelButtonText,
                isProcessing = isProcessing,
                onClick = onCancel
            )
        }
    } else {
        // Botón único horizontal (comportamiento original)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ConfirmButton(
                text = confirmationButtonText,
                isProcessing = isProcessing,
                onClick = onConfirm
            )
        }
    }
}

@Composable
private fun ConfirmButton(
    text: String,
    isProcessing: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isProcessing,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isProcessing) Color.Gray else Color.Black,
            disabledContentColor = Color.Gray
        ),
        border = BorderStroke(
            1.dp,
            color = if (isProcessing) Color.Gray else Color.Blue
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (isProcessing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 8.dp),
                    color = Color.Gray,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Opening...",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun CancelButton(
    text: String,
    isProcessing: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isProcessing,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isProcessing) Color.Gray else Color.Black,
            disabledContentColor = Color.Gray
        ),
        border = BorderStroke(
            1.dp,
            color = if (isProcessing) Color.Gray else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isProcessing) Color.Gray else Color.Gray
            )
        )
    }
}
