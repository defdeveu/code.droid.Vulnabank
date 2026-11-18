package defdev.labs.droid.vulnabank.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import defdev.labs.droid.vulnabank.LoginMode
import defdev.labs.droid.vulnabank.LoginViewModel
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.error.LoginError

@Composable
fun LoginScreen(model: LoginViewModel, onAuthenticated: () -> Unit) {
    val state by model.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onAuthenticated()
    }
    val register = state.mode == LoginMode.REGISTER
    Scaffold(topBar = { LabHeader() }) { insets ->
        ContentColumn(Modifier.padding(insets)) {
            PinField(
                value = state.password,
                onValueChange = model::onPasswordChange,
                label = stringResource(R.string.password),
                error = if (!register) state.error else null,
                imeAction = if (register) ImeAction.Next else ImeAction.Done,
                onDone = model::submit,
            )
            if (register) {
                PinField(
                    value = state.rePassword,
                    onValueChange = model::onRePasswordChange,
                    label = stringResource(R.string.re_enter_password),
                    error = state.error,
                    imeAction = ImeAction.Done,
                    onDone = model::submit,
                )
            }
            PrimaryAction(
                label = stringResource(if (register) R.string.register else R.string.login),
                enabled = state.canSubmit && !state.isAuthenticated,
                onClick = model::submit,
            )
        }
    }
}

@Composable
private fun PinField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: LoginError?,
    imeAction: ImeAction,
    onDone: () -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, fontWeight = MaterialTheme.typography.labelLargeEmphasized.fontWeight)
        },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(stringResource(it.messageRes())) } },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun LoginError.messageRes(): Int = when (this) {
    LoginError.PASSWORDS_DIFFER -> R.string.incorrect_re_password
    LoginError.INCORRECT_PASSWORD -> R.string.incorrect_password
}
