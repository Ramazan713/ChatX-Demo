package com.example.chatx.core.presentation.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.example.chatx.R


@Composable
fun ShowQuestionDialog(
    title: String? = null,
    content: String? = null,
    onApproved: () -> Unit,
    onCancel: (() -> Unit)? = null,
    allowDismiss: Boolean = true,
    onClosed: () -> Unit,
    negativeTitle: String = stringResource(R.string.cancel),
    positiveTitle: String = stringResource(R.string.approve),
    iconVector: ImageVector? = Icons.Default.Warning
){
    
    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = allowDismiss,
            dismissOnBackPress = allowDismiss
        ),
        onDismissRequest = onClosed,
        title = {
            Text(
                text = title ?: return@AlertDialog,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text =  { Text(text = content ?: return@AlertDialog )},
        dismissButton = {
            TextButton(
                onClick = {
                    onClosed()
                    onCancel?.invoke()
                },
            ){
                Text(negativeTitle)
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onClosed()
                    onApproved()
                },
            ){
                Text(positiveTitle)
            }
        },
        icon = {
            Icon(imageVector = iconVector ?: return@AlertDialog, contentDescription = null)
        }
    )
}