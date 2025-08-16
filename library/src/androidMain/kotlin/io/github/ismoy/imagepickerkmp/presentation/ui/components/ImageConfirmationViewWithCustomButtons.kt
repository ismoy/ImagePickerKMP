package io.github.ismoy.imagepickerkmp.presentation.ui.components

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
import coil3.compose.AsyncImage
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerUiConstants
import io.github.ismoy.imagepickerkmp.domain.config.UiConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.resources.StringResource
import io.github.ismoy.imagepickerkmp.presentation.resources.stringResource

@Suppress("EndOfSentenceFormat","LongMethod","FunctionNaming")
@Composable
fun ImageConfirmationViewWithCustomButtons(
    result: PhotoResult,
    onConfirm: (PhotoResult) -> Unit,
    onRetry: () -> Unit,
    customConfirmationView: (@Composable (PhotoResult, (PhotoResult) -> Unit, () -> Unit) -> Unit)? = null,
    uiConfig: UiConfig = UiConfig()
) {
    if (customConfirmationView != null) {
        customConfirmationView(result, onConfirm, onRetry)
        return
    }
    val resolvedButtonColor = uiConfig.buttonColor ?: ImagePickerUiConstants.ConfirmationCardBackgroundColor
    val resolvedIconColor = uiConfig.iconColor ?: ImagePickerUiConstants.ConfirmationCardIconColor
    val resolvedButtonSize = uiConfig.buttonSize ?: ImagePickerUiConstants.ConfirmationCardButtonSize
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ImagePickerUiConstants.ConfirmationCardDialogBackground)
            .padding(horizontal = ImagePickerUiConstants.ConfirmationCardDialogHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
                modifier = Modifier
                    .widthIn(max = ImagePickerUiConstants.ConfirmationCardMaxWidth),
                shape = RoundedCornerShape(ImagePickerUiConstants.ConfirmationCardCornerRadius),
                elevation = ImagePickerUiConstants.DefaultCardElevation,
                backgroundColor = resolvedButtonColor
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                   Box(modifier = Modifier
                       .fillMaxWidth()
                       .aspectRatio(ImagePickerUiConstants.ConfirmationCardImageAspectRatio)){
                       AsyncImage(
                           model = result.uri,
                           contentDescription = stringResource(StringResource.PREVIEW_IMAGE_DESCRIPTION),
                           modifier = Modifier
                               .fillMaxSize()
                               .aspectRatio(ImagePickerUiConstants.ConfirmationCardImageAspectRatio)
                               .clip(RoundedCornerShape(topStart = ImagePickerUiConstants.ConfirmationCardCornerRadius,
                                   topEnd = ImagePickerUiConstants.ConfirmationCardCornerRadius)),
                           contentScale = ContentScale.Crop
                       )
                       val isHD = result.width >= 1280 && result.height >= 720
                       Column( modifier = Modifier
                           .align(Alignment.TopEnd)
                           .padding(ImagePickerUiConstants.ConfirmationCardPadding),
                           verticalArrangement = Arrangement
                               .spacedBy(ImagePickerUiConstants.ConfirmationCardButtonSpacing)) {
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
                    Spacer(modifier = Modifier.height(ImagePickerUiConstants.ConfirmationCardSpacerHeight))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(resolvedButtonColor)
                            .padding(horizontal = ImagePickerUiConstants.ConfirmationCardPadding,
                                vertical = ImagePickerUiConstants.ConfirmationCardPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(StringResource.IMAGE_CONFIRMATION_TITLE),
                            color = ImagePickerUiConstants.ConfirmationCardTitleColor,
                            fontSize = ImagePickerUiConstants.ConfirmationCardTitleFontSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(ImagePickerUiConstants
                            .ConfirmationCardButtonSpacing.plus(4.dp)))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement
                                .spacedBy(ImagePickerUiConstants.ConfirmationCardButtonSpacing,
                                    Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.
                                buttonColors(backgroundColor = ImagePickerUiConstants.ConfirmationCardRetryButtonColor),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(resolvedButtonSize)
                            ) {
                                Icon(
                                    imageVector = uiConfig.galleryIcon ?: Icons.Default.Refresh,
                                    contentDescription = stringResource(StringResource.RETRY_BUTTON),
                                    tint = resolvedIconColor,
                                    modifier = Modifier
                                        .padding(end = ImagePickerUiConstants.ConfirmationCardButtonIconPadding)
                                )
                                Text(
                                    stringResource(StringResource.RETRY_BUTTON), color = resolvedIconColor,
                                    fontWeight = ImagePickerUiConstants.ConfirmationCardButtonTextFontWeight)
                            }
                            Button(
                                onClick = { onConfirm(result) },
                                colors = ButtonDefaults
                                    .buttonColors(backgroundColor = ImagePickerUiConstants
                                        .ConfirmationCardAcceptButtonColor),
                                shape = CircleShape,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(resolvedButtonSize)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(StringResource.ACCEPT_BUTTON),
                                    tint = resolvedIconColor,
                                    modifier = Modifier
                                        .padding(end = ImagePickerUiConstants.ConfirmationCardButtonIconPadding)
                                )
                                Text(
                                    stringResource(StringResource.ACCEPT_BUTTON),
                                    color = resolvedIconColor,
                                    fontWeight = ImagePickerUiConstants.ConfirmationCardButtonTextFontWeight)
                            }
                        }
                    }
                }
            }

    }
}
