package io.github.ismoy.imagepickerkmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler.PhotoResult
import io.github.ismoy.imagepickerkmp.Constant.BTN_ACCEPT
import io.github.ismoy.imagepickerkmp.Constant.BTN_RETRY
import io.github.ismoy.imagepickerkmp.Constant.TITLE_IMAGE_CONFIRMATION
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
fun ImageConfirmationViewWithCustomButtons(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    buttonColor: Color? = null,
    iconColor: Color? = null,
    buttonSize: Dp? = null,
    layoutPosition: String? = null,
    flashIcon: ImageVector? = null,
    switchCameraIcon: ImageVector? = null,
    captureIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null
) {
    if (customConfirmationView != null) {
        customConfirmationView(result, onConfirm, onRetry)
        return
    }
    val resolvedButtonColor = buttonColor ?: Color(0xFF2D3748)
    val resolvedIconColor = iconColor ?: Color.White
    val resolvedButtonSize = buttonSize ?: 48.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A202C))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
                modifier = Modifier
                    .widthIn(max = 400.dp),
                shape = RoundedCornerShape(32.dp),
                elevation = 16.dp,
                backgroundColor = resolvedButtonColor
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                   Box(modifier = Modifier
                       .fillMaxWidth()
                       .aspectRatio(1f)){
                       AsyncImage(
                           model = result.uri,
                           contentDescription = "Preview",
                           modifier = Modifier
                               .fillMaxSize()
                               .aspectRatio(1f)
                               .clip(RoundedCornerShape(topStart = 32.dp,topEnd = 32.dp)),
                           contentScale = ContentScale.Crop
                       )
                       val isHD = result.width >= 1280 && result.height >= 720
                       Column( modifier = Modifier
                           .align(Alignment.TopEnd)
                           .padding(16.dp),
                           verticalArrangement = Arrangement.spacedBy(16.dp)) {
                           IconButton(
                               onClick = {},
                               modifier = Modifier
                                   .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                                   .size(resolvedButtonSize)
                           ) {
                               Icon(
                                   imageVector = if (isHD) Icons.Default.Hd else Icons.Default.Sd,
                                   contentDescription = if (isHD) "HD" else "SD",
                                   tint = resolvedIconColor
                               )
                           }
                       }
                   }
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(resolvedButtonColor)
                            .padding(horizontal = 24.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = TITLE_IMAGE_CONFIRMATION,
                            color = Color(0xFFCBD5E1),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE53935)),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(resolvedButtonSize)
                            ) {
                                Icon(
                                    imageVector = galleryIcon ?: Icons.Default.Refresh,
                                    contentDescription = BTN_RETRY,
                                    tint = resolvedIconColor,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(BTN_RETRY, color = resolvedIconColor, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onConfirm(result) },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF43A047)),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(resolvedButtonSize)
                            ) {
                                Icon(
                                    imageVector = captureIcon ?: Icons.Default.Check,
                                    contentDescription = BTN_ACCEPT,
                                    tint = resolvedIconColor,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(BTN_ACCEPT, color = resolvedIconColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

    }
}