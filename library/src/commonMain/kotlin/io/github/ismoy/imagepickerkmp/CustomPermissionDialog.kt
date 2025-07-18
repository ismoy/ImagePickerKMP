package io.github.ismoy.imagepickerkmp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        DialogContent(
            title = title,
            description = description,
            confirmationButtonText = confirmationButtonText,
            onConfirm = onConfirm
        )
    }
}

@Composable
private fun DialogContent(
    title: String,
    description: String,
    confirmationButtonText: String,
    onConfirm: () -> Unit
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
            DialogButtons(confirmationButtonText, onConfirm)
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
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(
            onClick = onConfirm,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black
            ),
            border = BorderStroke(
                1.dp,
                color = Color.Blue
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Text(
                text = confirmationButtonText,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
