package defdev.labs.droid.vulnabank

import androidx.lifecycle.ViewModel
import defdev.labs.droid.vulnabank.error.LoginError
import defdev.labs.droid.vulnabank.service.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class LoginMode { REGISTER, LOGIN }

data class LoginUiState(
    val mode: LoginMode,
    val password: String = "",
    val rePassword: String = "",
    val error: LoginError? = null,
    val isAuthenticated: Boolean = false,
) {
    val canSubmit: Boolean
        get() = when (mode) {
            LoginMode.REGISTER -> password.length == PIN_LENGTH && rePassword.length == PIN_LENGTH
            LoginMode.LOGIN -> password.length == PIN_LENGTH
        }

    companion object {
        const val PIN_LENGTH = 4
    }
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val state = MutableStateFlow(
        LoginUiState(mode = if (authRepository.hasRegistration()) LoginMode.LOGIN else LoginMode.REGISTER),
    )
    val uiState: StateFlow<LoginUiState> = state.asStateFlow()

    fun onPasswordChange(value: String) {
        if (value.length <= LoginUiState.PIN_LENGTH) {
            state.update { it.copy(password = value, error = null) }
        }
    }

    fun onRePasswordChange(value: String) {
        if (value.length <= LoginUiState.PIN_LENGTH) {
            state.update { it.copy(rePassword = value, error = null) }
        }
    }

    fun submit() {
        val current = state.value
        if (!current.canSubmit) return
        when (current.mode) {
            LoginMode.REGISTER ->
                if (current.password.isNotEmpty() && current.password == current.rePassword) {
                    authRepository.register(current.rePassword)
                    state.update { it.copy(isAuthenticated = true) }
                } else {
                    state.update { it.copy(error = LoginError.PASSWORDS_DIFFER) }
                }
            LoginMode.LOGIN ->
                if (authRepository.login(current.password)) {
                    state.update { it.copy(isAuthenticated = true) }
                } else {
                    state.update { it.copy(error = LoginError.INCORRECT_PASSWORD) }
                }
        }
    }
}
