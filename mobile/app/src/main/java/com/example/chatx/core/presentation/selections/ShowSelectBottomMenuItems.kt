package com.example.chatx.core.presentation.selections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatx.core.domain.models.IMenuItemEnum
import com.example.chatx.core.domain.models.IconInfo
import com.example.chatx.core.domain.utils.UiText
import com.example.chatx.core.presentation.components.IconLabelDefaults
import com.example.chatx.core.presentation.components.IconLabelRow
import com.example.chatx.core.presentation.components.SharedHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: IMenuItemEnum> ShowSelectBottomMenuItems(
    items: List<T>,
    onClickItem: (T) -> Unit,
    onClose: () -> Unit,
    title: String? = null,
    itemSpaceBy: Dp = 2.dp
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
    ){
        Column(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .padding(horizontal = 4.dp)
        ) {
            if(title != null){
                SharedHeader(
                    title = title,
                    onIconClick = { onClose() },
                    titleStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(itemSpaceBy)
            ) {
                items(items){item->
                    IconLabelRow(
                        modifier = Modifier.fillMaxWidth(),
                        menuItem = item,
                        onClick = { onClickItem(item) },
                        defaults = IconLabelDefaults(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            iconTextWidth = 12.dp,
                            iconSize = 32.dp
                        ),
                    )
                }
            }
        }
    }
}

private enum class SampleMenuItems: IMenuItemEnum{
    Item1{
        override val title: UiText
            get() = UiText.Text("Item 1")
        override val iconInfo: IconInfo?
            get() = IconInfo(imageVector = Icons.Default.Add)
    },
    Item2{
        override val title: UiText
        get() = UiText.Text("Item 2")
        override val iconInfo: IconInfo?
            get() = IconInfo(imageVector = Icons.Default.Save)
    }
}

@Preview(showBackground = true)
@Composable
private fun ShowSelectBottomMenuItemsPreview() {
    ShowSelectBottomMenuItems(
        items = SampleMenuItems.entries,
        onClickItem = {},
        title = "Sample Title",
        onClose = {}
    )
}