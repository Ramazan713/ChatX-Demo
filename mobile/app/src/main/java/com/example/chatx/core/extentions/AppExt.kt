package com.example.chatx.core.extentions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.LayoutDirection
import com.example.chatx.MainActivity


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
fun Context.refreshApp(){
    val activity = this as Activity
    activity.finish()
    activity.startActivity(Intent(activity, MainActivity::class.java))
}

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LayoutDirection.Rtl
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

fun Int.addPrefixZeros(totalLength: Int): String{
    val numStr = this.toString()
    val prefix = (totalLength-numStr.length).let { num->
        if(num>0) "0".repeat(num) else ""
    }
    return prefix + numStr
}
