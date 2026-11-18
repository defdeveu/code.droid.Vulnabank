package defdev.labs.droid.vulnabank

import defdev.labs.droid.vulnabank.error.LoginError
import defdev.labs.droid.vulnabank.service.AuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun firstStartShowsRegistration() {
        assertEquals(LoginMode.REGISTER, LoginViewModel(FakeAuth()).uiState.value.mode)
    }

    @Test
    fun registeredStartShowsLogin() {
        assertEquals(LoginMode.LOGIN, LoginViewModel(FakeAuth(registered = "1234")).uiState.value.mode)
    }

    @Test
    fun registrationNeedsBothFieldsComplete() {
        val model = LoginViewModel(FakeAuth())
        model.onPasswordChange("1234")
        assertFalse(model.uiState.value.canSubmit)
        model.onRePasswordChange("1234")
        assertTrue(model.uiState.value.canSubmit)
    }

    @Test
    fun inputLongerThanFourIsIgnored() {
        val model = LoginViewModel(FakeAuth())
        model.onPasswordChange("1234")
        model.onPasswordChange("12345")
        assertEquals("1234", model.uiState.value.password)
    }

    @Test
    fun differentEntriesShowError() {
        val auth = FakeAuth()
        val model = LoginViewModel(auth)
        model.onPasswordChange("1234")
        model.onRePasswordChange("1235")
        model.submit()
        assertEquals(LoginError.PASSWORDS_DIFFER, model.uiState.value.error)
        assertNull(auth.registered)
    }

    @Test
    fun matchingEntriesRegister() {
        val auth = FakeAuth()
        val model = LoginViewModel(auth)
        model.onPasswordChange("1234")
        model.onRePasswordChange("1234")
        model.submit()
        assertEquals("1234", auth.registered)
        assertTrue(model.uiState.value.isAuthenticated)
    }

    @Test
    fun wrongPinShowsError() {
        val model = LoginViewModel(FakeAuth(registered = "1234"))
        model.onPasswordChange("9999")
        model.submit()
        assertEquals(LoginError.INCORRECT_PASSWORD, model.uiState.value.error)
        assertFalse(model.uiState.value.isAuthenticated)
    }

    @Test
    fun editingClearsError() {
        val model = LoginViewModel(FakeAuth(registered = "1234"))
        model.onPasswordChange("9999")
        model.submit()
        model.onPasswordChange("999")
        assertNull(model.uiState.value.error)
    }

    @Test
    fun rightPinLogsIn() {
        val model = LoginViewModel(FakeAuth(registered = "1234"))
        model.onPasswordChange("1234")
        model.submit()
        assertTrue(model.uiState.value.isAuthenticated)
    }
}

private class FakeAuth(var registered: String? = null) : AuthRepository {
    private var address = ""
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
