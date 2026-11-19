package defdev.labs.droid.vulnabank

import androidx.lifecycle.ViewModel
import defdev.labs.droid.vulnabank.service.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val newPassword: String = "",
    val reNewPassword: String = "",
    val passwordsDiffer: Boolean = false,
    val passwordChanged: Boolean = false,
    val tlsEnabled: Boolean = false,
    val tlsChanged: Boolean = false,
) {
    val canChange: Boolean
        get() = newPassword.length == LoginUiState.PIN_LENGTH && reNewPassword.length == LoginUiState.PIN_LENGTH
}

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val serverUrl: String,
    private val serverUrlSsl: String,
) : ViewModel() {
    private val state = MutableStateFlow(
        SettingsUiState(tlsEnabled = authRepository.getServerAddress() == serverUrlSsl),
    )
    val uiState: StateFlow<SettingsUiState> = state.asStateFlow()

    fun onNewPasswordChange(value: String) {
        if (value.length <= LoginUiState.PIN_LENGTH) {
            state.update { it.copy(newPassword = value, passwordsDiffer = false, passwordChanged = false) }
        }
    }

    fun onReNewPasswordChange(value: String) {
        if (value.length <= LoginUiState.PIN_LENGTH) {
            state.update { it.copy(reNewPassword = value, passwordsDiffer = false, passwordChanged = false) }
        }
    }

    fun changePassword() {
        val current = state.value
        if (!current.canChange) return
        if (current.newPassword == current.reNewPassword) {
            authRepository.register(current.reNewPassword)
            state.update { it.copy(newPassword = "", reNewPassword = "", passwordChanged = true) }
        } else {
            state.update { it.copy(passwordsDiffer = true) }
        }
    }

    fun onTlsChange(checked: Boolean) {
        authRepository.setServerAddress(if (checked) serverUrlSsl else serverUrl)
        state.update { it.copy(tlsEnabled = checked, tlsChanged = true) }
    }
}
