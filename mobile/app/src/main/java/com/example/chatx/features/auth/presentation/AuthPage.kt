package com.example.chatx.features.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.chatx.core.presentation.utils.EventHandler
import com.example.chatx.core.presentation.utils.ShowLifecycleToastMessage

@Composable
fun AuthPageRoot(
    onNavigateSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AuthPage(
        state = state,
        onAction = viewModel::onAction,
        onNavigateSuccess = onNavigateSuccess
    )
}

@Composable
fun AuthPage(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    onNavigateSuccess: () -> Unit,
) {

    ShowLifecycleToastMessage(
        state.message
    ) {
        onAction(AuthAction.ClearMessage)
    }

    EventHandler(state.uiEvent) { uiEvent ->
        when(uiEvent){
            AuthUiEvent.NavigateToSuccess -> onNavigateSuccess()
        }
        onAction(AuthAction.ClearUiEvent)
    }

    Scaffold(
        
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = state.username,
                    onValueChange = {
                        onAction(AuthAction.OnUsernameChange(it))
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = {
                        onAction(AuthAction.OnPasswordChange(it))
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onAction(AuthAction.SignUp)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sign Up")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            onAction(AuthAction.Login)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthPreview(modifier: Modifier = Modifier) {
    AuthPage(
        state = AuthState(),
        onAction = {},
        onNavigateSuccess = {}
    )
}