package com.example.chatx.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatx.R


@Composable
fun SharedHeader(
    modifier: Modifier = Modifier,
    title: String,
    onIconClick: () -> Unit,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    textAlign: TextAlign = TextAlign.Center,
    iconVector: ImageVector = Icons.Default.Close,
    contentDescription: String? = stringResource(id = R.string.close),
    tooltip: String? = contentDescription,
) {
    SharedHeader(
        content = {
            Text(
                text = title,
                textAlign = textAlign,
                style = titleStyle,
            )
        },
        onIconClick = onIconClick,
        tooltip = tooltip,
        iconVector = iconVector,
        modifier = modifier
    )
}

@Composable
fun SharedHeader(
    modifier: Modifier = Modifier,
    content: @Composable() () -> Unit,
    onIconClick: () -> Unit,
    iconVector: ImageVector = Icons.Default.Close,
    contentDescription: String? = stringResource(id = R.string.close),
    tooltip: String? = contentDescription,
    contentPadding: PaddingValues = PaddingValues(horizontal = 48.dp),
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        DefaultToolTip(
            tooltip = tooltip,
        ) {
            IconButton(
                onClick = onIconClick,
            ) {
                Icon(imageVector = iconVector, contentDescription = contentDescription)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SharedHeaderPreview() {
    SharedHeader(
        title = "My title",
        onIconClick = {}
    )
}
