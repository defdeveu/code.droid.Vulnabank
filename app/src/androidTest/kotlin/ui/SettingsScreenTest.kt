package defdev.labs.droid.vulnabank.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.SettingsViewModel
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule val rule = createComposeRule()
    private fun text(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun show(auth: FakeAuth) {
        val model = SettingsViewModel(auth, PLAIN, SECURE)
        rule.setContent { VulnaBankTheme { SettingsScreen(model) } }
    }

    @Test fun checkboxFollowsStoredAddress() {
        show(FakeAuth(address = SECURE))
        rule.onNodeWithText(text(R.string.tls_verification_enabled)).assertIsOn()
    }

    @Test fun toggleShowsRestartNotice() {
        show(FakeAuth(address = SECURE))
        rule.onNodeWithText(text(R.string.tls_verification_enabled)).performClick()
        rule.onNodeWithText(text(R.string.tls_verification_enabled)).assertIsOff()
        rule.onNodeWithText(text(R.string.tls_change_restart_notification)).assertIsDisplayed()
    }

    @Test fun matchingEntriesShowConfirmation() {
        show(FakeAuth(registered = "1234", address = SECURE))
        rule.onNodeWithText(text(R.string.new_password)).performTextInput("5678")
        rule.onNodeWithText(text(R.string.re_enter_password)).performTextInput("5678")
        rule.onNodeWithText(text(R.string.change_password)).performClick()
        rule.onNodeWithText(text(R.string.password_change_done)).assertIsDisplayed()
    }

    private companion object {
        const val SECURE = "https://example.test/request"
        const val PLAIN = "http://example.test/request"
    }
}
