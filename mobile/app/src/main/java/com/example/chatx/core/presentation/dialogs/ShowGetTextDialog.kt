package com.example.chatx.core.presentation.dialogs

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatx.R
import kotlinx.coroutines.delay


@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ShowGetTextDialog(
    title: String,
    content: String = "",
    imageVector: ImageVector? = null,
    onApproved: (String) -> Unit,
    onClosed: () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    closeAfterApprove: Boolean = true
){
    var textField by rememberSaveable(stateSaver = TextFieldValue.Saver){
        mutableStateOf(TextFieldValue(text = content, selection = TextRange(content.length)))
    }
    var error by rememberSaveable{
        mutableStateOf<String?>(null)
    }
    val context = LocalContext.current
    val shape = MaterialTheme.shapes.medium

    val localFocusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier = Modifier
            .focusRequester(focusRequester),
        onDismissRequest = onClosed,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text =  {
            OutlinedTextField(
                value = textField,
                onValueChange = {textField = it},
                isError = error != null,
                singleLine = true,
                label = { error?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done,
                    keyboardType = keyboardType,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        checkText(
                            context,
                            text = textField.text,
                            onSetError = { error = it },
                            onApprove = {
                                onApproved(it)
                                if(closeAfterApprove){
                                    onClosed()
                                }
                                localFocusManager.clearFocus()
                            }
                        )
                    }
                ),
                shape = shape,
                placeholder = { Text(stringResource(R.string.text_field)) },
                modifier = Modifier
                    .padding(horizontal = 1.dp, vertical = 13.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp)
                    .semantics {
                        contentDescription = context.getString(R.string.text_field)
                    }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onClosed,
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    checkText(
                        context,
                        text = textField.text,
                        onSetError = { error = it },
                        onApprove = {
                            onApproved(it)
                            if(closeAfterApprove){
                                onClosed()
                            }
                        }
                    )
                },
            ) {
                Text(text = stringResource(R.string.approve))
            }
        },
        icon = {
            Icon(imageVector = imageVector ?: return@AlertDialog, contentDescription = null)
        }
    )
}

private fun checkText(
    context: Context,
    text: String,
    onSetError: (String?) -> Unit,
    onApprove: (String) -> Unit
){
    if(text.isBlank()){
        return onSetError(context.getString(R.string.error_not_empty_field))
    }
    onApprove(text)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
private fun ShowGetTextDialogPreview() {
    ShowGetTextDialog(
        title = "Enter a name",
        content = "Please enter a name",
        onApproved = {},
        onClosed = {}
    )
}


