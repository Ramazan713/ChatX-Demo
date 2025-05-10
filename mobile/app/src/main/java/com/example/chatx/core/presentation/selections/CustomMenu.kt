package com.example.chatx.core.presentation.selections


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatx.R
import com.example.chatx.core.domain.models.IMenuItemEnum
import com.example.chatx.core.domain.models.IconInfo
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.core.presentation.components.DefaultToolTip
import com.masterplus.animals.core.extentions.clickableWithoutRipple


@Composable
fun <T: IMenuItemEnum> ReplaceableIconMenu(
    items: List<T>,
    currentItem: T,
    modifier: Modifier = Modifier,
    onItemChange: ((T)->Unit)? = null,
    enabled: Boolean = true
) {
    val currentIconInfo = currentItem.iconInfo
    BaseMenu(
        modifier = modifier,
        items = items,
        onItemChange = onItemChange,
        enabled = enabled
    ){ _, onExpandedChange ->
        DefaultToolTip(tooltip = currentItem.title.asString()) {
            IconButton(
                onClick = { onExpandedChange(true) },
                enabled = enabled
            ) {
                Icon(
                    imageVector = currentIconInfo!!.imageVector,
                    contentDescription = null,
                    tint = currentIconInfo.tintColor?.asColor() ?: LocalContentColor.current,
                )
            }
        }
    }
}


@Composable
fun <T: IMenuItemEnum> PersistentIconMenu(
    items: List<T>,
    modifier: Modifier = Modifier,
    onItemChange: ((T)->Unit)? = null,
    icon: ImageVector = Icons.Default.MoreVert,
    contentDescription: String? = stringResource(id = R.string.menu),
    tooltip: String? = contentDescription,
    enabled: Boolean = true
) {
    BaseMenu(
        modifier = modifier,
        items = items,
        onItemChange = onItemChange,
        enabled = enabled
    ){ _, onExpandedChange ->
        DefaultToolTip(tooltip = tooltip) {
            IconButton(
                onClick = { onExpandedChange(true) },
                modifier = modifier,
                enabled = enabled
            ){
                Icon(icon, contentDescription = contentDescription)
            }
        }
    }
}

@Composable
fun <T: IMenuItemEnum> TextDynamicMenu(
    items: List<T>,
    onItemChange: (T) -> Unit,
    currentItem: T?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderWidth: Dp? = 1.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = MaterialTheme.shapes.small,
) {
    val context = LocalContext.current
    var currentText by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(currentItem){
        currentText = currentItem?.title?.asString(context) ?: "Select"
    }

    BaseMenu(
        modifier = modifier
            .clip(shape = shape)
            .then(borderWidth?.let {
                Modifier.border(
                    it,
                    MaterialTheme.colorScheme.outlineVariant,
                    shape
                )
            } ?: Modifier)
            .background(backgroundColor, shape)
        ,
        items = items,
        onItemChange = onItemChange,
        shape = shape,
        enabled = enabled
    ){ expanded, onExpandedChange ->

        val imageVector = remember(expanded) {
            if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
        }

        Row(
            modifier = modifier
                .background(Color.Transparent)
                .clickableWithoutRipple(enabled = enabled) {
                    onExpandedChange(true)
                }
                .padding(horizontal = 11.dp, vertical = 9.dp)
            ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(
                currentText,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(R.string.dropdown_menu_text),
            )

        }
    }
}

@Composable
private fun <T: IMenuItemEnum> BaseMenu(
    items: List<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onItemChange: ((T) -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.small,
    triggerContent: @Composable (Boolean, (Boolean) -> Unit) -> Unit
) {

    val context = LocalContext.current

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        triggerContent(expanded, { expanded = it })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 3.dp)
        ){
            items.forEach { item->
                val title = item.title.asString(context)
                GetDropdownMenuItem(
                    enabled = enabled,
                    title = title,
                    onClick = {
                        expanded = false
                        onItemChange?.invoke(item)
                    },
                    modifier = Modifier.clip(shape),
                    iconInfo = item.iconInfo
                )
            }
        }
    }
}

@Composable
private fun GetDropdownMenuItem(
    title: String,
    onClick: () -> Unit,
    enabled: Boolean,
    iconInfo: IconInfo?,
    modifier: Modifier = Modifier
) {
    if(iconInfo == null){
        DropdownMenuItem(
            enabled = enabled,
            text = { Text(title) },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = onClick,
            modifier = modifier
        )
    }else{
        DropdownMenuItem(
            enabled = enabled,
            text = { Text(title) },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onClick = onClick,
            modifier = modifier,
            leadingIcon = {
                Icon(
                    imageVector = iconInfo.imageVector,
                    contentDescription = stringResource(R.string.n_menu_item,title),
                    tint = iconInfo.tintColor?.asColor() ?: LocalContentColor.current
                )
            }
        )
    }

}

private enum class PreviewMenuItems(
    override val title: UiText,
    override val iconInfo: IconInfo?
): IMenuItemEnum {
    Add(
        title = UiText.Text("Add"),
        iconInfo = IconInfo(imageVector = Icons.Default.Add)
    ),
    List(
        title = UiText.Text("List"),
        iconInfo = IconInfo(imageVector = Icons.AutoMirrored.Filled.List)
    )
}

@Preview(showBackground = true)
@Composable
private fun TextDynamicMenuPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextDynamicMenu(
            items = PreviewMenuItems.entries,
            onItemChange = { },
            currentItem = PreviewMenuItems.Add
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PersistentIconMenuPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersistentIconMenu(
            items = PreviewMenuItems.entries,
            onItemChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReplaceableIconMenuPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReplaceableIconMenu(
            items = PreviewMenuItems.entries,
            currentItem = PreviewMenuItems.List,
            onItemChange = { }
        )
    }
}