package defdev.labs.droid.vulnabank

import defdev.labs.droid.vulnabank.service.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsViewModelTest {
    private val auth = SettingsFakeAuth(address = SECURE)

    @Test
    fun checkboxReflectsStoredAddress() {
        assertTrue(SettingsViewModel(auth, PLAIN, SECURE).uiState.value.tlsEnabled)
        assertFalse(SettingsViewModel(SettingsFakeAuth(address = PLAIN), PLAIN, SECURE).uiState.value.tlsEnabled)
    }

    @Test
    fun uncheckingStoresOtherAddress() {
        val model = SettingsViewModel(auth, PLAIN, SECURE)
        model.onTlsChange(false)
        assertEquals(PLAIN, auth.address)
        assertTrue(model.uiState.value.tlsChanged)
    }

    @Test
    fun checkingStoresSecureAddress() {
        val model = SettingsViewModel(SettingsFakeAuth(address = PLAIN), PLAIN, SECURE)
        model.onTlsChange(true)
        assertTrue(model.uiState.value.tlsEnabled)
    }

    @Test
    fun matchingEntriesChangePassword() {
        val model = SettingsViewModel(auth, PLAIN, SECURE)
        model.onNewPasswordChange("5678")
        model.onReNewPasswordChange("5678")
        model.changePassword()
        assertEquals("5678", auth.registered)
        assertTrue(model.uiState.value.passwordChanged)
        assertEquals("", model.uiState.value.newPassword)
    }

    @Test
    fun differentEntriesKeepPassword() {
        val model = SettingsViewModel(auth, PLAIN, SECURE)
        model.onNewPasswordChange("5678")
        model.onReNewPasswordChange("5679")
        model.changePassword()
        assertEquals(null, auth.registered)
        assertTrue(model.uiState.value.passwordsDiffer)
    }

    @Test
    fun incompleteEntriesCannotBeSubmitted() {
        val model = SettingsViewModel(auth, PLAIN, SECURE)
        model.onNewPasswordChange("56")
        model.onReNewPasswordChange("5678")
        assertFalse(model.uiState.value.canChange)
    }

    private companion object {
        const val SECURE = "https://example.test/request"
        const val PLAIN = "http://example.test/request"
    }
}

private class SettingsFakeAuth(var address: String, var registered: String? = null) : AuthRepository {
    override fun register(password: String) {
        registered = password
    }
    override fun login(password: String) = password == registered
    override fun hasRegistration() = registered != null
    override fun getServerAddress() = address
    override fun setServerAddress(serverAddress: String) {
        address = serverAddress
    }
}
