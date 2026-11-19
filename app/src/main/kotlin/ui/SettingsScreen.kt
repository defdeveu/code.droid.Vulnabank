package defdev.labs.droid.vulnabank.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.SettingsViewModel

@Composable
fun SettingsScreen(model: SettingsViewModel) {
    val state by model.uiState.collectAsStateWithLifecycle()
    ContentColumn {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.titleLargeEmphasized)
        SettingsPinField(state.newPassword, model::onNewPasswordChange, R.string.new_password, error = false)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SettingsPinField(state.reNewPassword, model::onReNewPasswordChange, R.string.re_enter_password, state.passwordsDiffer)
            if (state.passwordsDiffer) {
                Text(
                    stringResource(R.string.incorrect_re_password),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        PrimaryAction(stringResource(R.string.change_password), enabled = state.canChange, onClick = model::changePassword)
        if (state.passwordChanged) {
            Text(stringResource(R.string.password_change_done), style = MaterialTheme.typography.bodyMedium)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(value = state.tlsEnabled, role = Role.Checkbox, onValueChange = model::onTlsChange),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Checkbox(checked = state.tlsEnabled, onCheckedChange = null)
                Text(stringResource(R.string.tls_verification_enabled), style = MaterialTheme.typography.bodyLarge)
            }
            if (state.tlsChanged) {
                Text(
                    stringResource(R.string.tls_change_restart_notification),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsPinField(value: String, onValueChange: (String) -> Unit, label: Int, error: Boolean) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label), fontWeight = MaterialTheme.typography.labelLargeEmphasized.fontWeight) },
        singleLine = true,
        isError = error,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    )
}
