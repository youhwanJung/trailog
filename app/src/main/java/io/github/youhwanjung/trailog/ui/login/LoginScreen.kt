package io.github.youhwanjung.trailog.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.youhwanjung.trailog.ui.theme.TrailogTheme

/**
 * 로그인 화면의 진입점. ViewModel 과 화면을 연결하는 얇은 껍데기다.
 *
 * Route 와 Screen 을 나누는 이유는 아래 LoginScreen 을 ViewModel 없이
 * Preview 와 UI 테스트에서 그대로 띄울 수 있게 하기 위해서다.
 */
@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel(),
) {
    // collectAsState 가 아니라 collectAsStateWithLifecycle 을 쓴다.
    // 화면이 백그라운드로 가면 구독을 멈춰서 보이지도 않는 화면 때문에 일하지 않는다.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login,
        onSignUpClick = onSignUpClick,
        modifier = modifier,
    )
}

/**
 * 로그인 화면.
 *
 * 상태를 직접 들고 있지 않고 전부 파라미터로 받는다(stateless).
 * 같은 입력을 주면 항상 같은 화면이 나오므로 Preview 와 테스트가 쉬워진다.
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Trailog",
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "여행의 발자취를 기록하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("이메일") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                isError = uiState.errorMessage != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("비밀번호") },
                singleLine = true,
                enabled = !uiState.isSubmitting,
                isError = uiState.errorMessage != null,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // 에러 자리는 높이를 고정해두면 메시지가 뜰 때 버튼이 밀리지 않는다.
            Spacer(Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onLoginClick,
                enabled = uiState.canSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("로그인")
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onSignUpClick,
                enabled = !uiState.isSubmitting,
            ) {
                Text("계정이 없으신가요? 회원가입")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    TrailogTheme {
        LoginScreen(uiState = LoginUiState(email = "trailog@example.com", password = "1234"))
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    TrailogTheme {
        LoginScreen(
            uiState = LoginUiState(
                email = "trailog@example.com",
                password = "1",
                errorMessage = "이메일 또는 비밀번호를 확인해 주세요.",
            ),
        )
    }
}
