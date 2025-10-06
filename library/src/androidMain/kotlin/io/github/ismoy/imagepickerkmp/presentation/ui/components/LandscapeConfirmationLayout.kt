package io.github.ismoy.imagepickerkmp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.stringResource

@Composable
 fun LandscapeConfirmationLayout(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit,
    resolvedButtonColor: Color,
    resolvedIconColor: Color,
    resolvedButtonSize: Dp,
    uiConfig: UiConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.8f),
        shape = RoundedCornerShape(ImagePickerUiConstants.ConfirmationCardCornerRadius),
        elevation = ImagePickerUiConstants.DefaultCardElevation,
        backgroundColor = resolvedButtonColor
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = result.uri,
                    contentDescription = stringResource(StringResource.PREVIEW_IMAGE_DESCRIPTION),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(
                            topStart = ImagePickerUiConstants.ConfirmationCardCornerRadius,
                            bottomStart = ImagePickerUiConstants.ConfirmationCardCornerRadius
                        )),
                    contentScale = ContentScale.Crop
                )

                val isHD = result.width >= 1280 && result.height >= 720
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(ImagePickerUiConstants.ConfirmationCardPadding),
                    verticalArrangement = Arrangement.spacedBy(ImagePickerUiConstants.ConfirmationCardButtonSpacing)
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                            .size(resolvedButtonSize)
                    ) {
                        Icon(
                            imageVector = if (isHD) Icons.Default.Hd else Icons.Default.Sd,
                            contentDescription = if (isHD) stringResource(StringResource.HD_QUALITY_DESCRIPTION)
                            else stringResource(StringResource.SD_QUALITY_DESCRIPTION),
                            tint = resolvedIconColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(resolvedButtonColor)
                    .padding(ImagePickerUiConstants.ConfirmationCardPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(StringResource.IMAGE_CONFIRMATION_TITLE),
                    color = ImagePickerUiConstants.ConfirmationCardTitleColor,
                    fontSize = ImagePickerUiConstants.ConfirmationCardTitleFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onConfirm(result) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ImagePickerUiConstants.ConfirmationCardAcceptButtonColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(StringResource.ACCEPT_BUTTON),
                            tint = resolvedIconColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            stringResource(StringResource.ACCEPT_BUTTON),
                            color = resolvedIconColor,
                            fontWeight = ImagePickerUiConstants.ConfirmationCardButtonTextFontWeight
                        )
                    }

                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ImagePickerUiConstants.ConfirmationCardRetryButtonColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        Icon(
                            imageVector = uiConfig.galleryIcon ?: Icons.Default.Refresh,
                            contentDescription = stringResource(StringResource.RETRY_BUTTON),
                            tint = resolvedIconColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            stringResource(StringResource.RETRY_BUTTON),
                            color = resolvedIconColor,
                            fontWeight = ImagePickerUiConstants.ConfirmationCardButtonTextFontWeight
                        )
                    }
                }
            }
        }
    }
}