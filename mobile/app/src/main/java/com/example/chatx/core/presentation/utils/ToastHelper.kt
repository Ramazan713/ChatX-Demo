package com.example.chatx.core.presentation.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.example.chatx.core.domain.utils.UiText

class ToastHelper {

    companion object{
        private var toast: Toast? = null

        fun showMessage(message: String, context: Context,duration: Int = Toast.LENGTH_LONG){
            toast?.cancel()
            toast = Toast.makeText(context,message,Toast.LENGTH_LONG)
            toast?.show()
        }
        fun showMessage(uiText: UiText, context: Context, duration: Int = Toast.LENGTH_LONG){
            showMessage(uiText.asString(context), context, duration)
        }
    }

}


@Composable
fun ShowLifecycleToastMessage(
    message: UiText?,
    onDismiss: () -> Unit
) {

    val currentOnDismiss by rememberUpdatedState(newValue = onDismiss)
    val context = LocalContext.current

    EventHandler(event = message) { msg->
        ToastHelper.showMessage(msg, context)
        currentOnDismiss()
    }
}