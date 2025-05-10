package com.example.chatx.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatx.core.domain.models.IMenuItemEnum
import com.example.chatx.core.domain.models.IconInfo
import com.masterplus.animals.core.extentions.useBackground
import com.masterplus.animals.core.extentions.useBorder

data class IconLabelDefaults(
    val enabled: Boolean = true,
    val iconSize: Dp = 24.dp,
    val iconTextWidth: Dp = 4.dp,
    val borderWidth: Dp? = null,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
)

@Composable
fun IconLabelRow(
    menuItem: IMenuItemEnum,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    defaults: IconLabelDefaults = IconLabelDefaults(),
    shape: Shape = MaterialTheme.shapes.medium,
    horizontalArrangement: Arrangement. Horizontal = Arrangement.Start
){
    IconLabelRow(
        title = menuItem.title.asString(),
        onClick = onClick,
        iconInfo = menuItem.iconInfo,
        modifier = modifier,
        defaults = defaults,
        shape = shape,
        horizontalArrangement = horizontalArrangement
    )
}

@Composable
fun IconLabelRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconInfo: IconInfo? = null,
    defaults: IconLabelDefaults = IconLabelDefaults(),
    shape: Shape = MaterialTheme.shapes.medium,
    horizontalArrangement: Arrangement. Horizontal = Arrangement.Start
){
    Row(
        modifier = modifier
            .clip(shape)
            .useBorder(defaults.borderWidth, shape = shape)
            .useBackground(defaults.containerColor, shape = shape)
            .clickable(
                enabled = defaults.enabled,
                onClick = onClick
            )
            .padding(defaults.contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        iconInfo?.let { iconInfo->
            Icon(
                imageVector = iconInfo.imageVector,
                contentDescription = iconInfo.description?.asString(),
                modifier = Modifier
                    .size(defaults.iconSize),
                tint = iconInfo.tintColor?.asColor() ?: defaults.contentColor ?: LocalContentColor.current
            )
            Spacer(Modifier.width(defaults.iconTextWidth))
        }
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = defaults.contentColor ?: LocalContentColor.current
            ),
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun IconLabelRowPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconLabelRow(
            title = "sample title",
            iconInfo = IconInfo(imageVector = Icons.Default.Add),
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        )

        IconLabelRow(
            title = "sample title",
            iconInfo = null,
            onClick = {}
        )

        IconLabelRow(
            title = "sample title",
            iconInfo = null,
            onClick = {},
            defaults = IconLabelDefaults(
                borderWidth = 1.dp
            )
        )
    }
}