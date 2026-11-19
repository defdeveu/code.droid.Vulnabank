package defdev.labs.droid.vulnabank.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import defdev.labs.droid.vulnabank.LoginViewModel
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule val rule = createComposeRule()
    private fun text(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun show(auth: FakeAuth, onDone: () -> Unit = {}) {
        val model = LoginViewModel(auth)
        rule.setContent { VulnaBankTheme { LoginScreen(model, onDone) } }
    }

    @Test fun firstStartShowsRegistration() {
        show(FakeAuth())
        rule.onNodeWithText(text(R.string.password)).assertIsDisplayed()
        rule.onNodeWithText(text(R.string.re_enter_password)).assertIsDisplayed()
        rule.onNodeWithText(text(R.string.register)).assertIsNotEnabled()
    }

    @Test fun completeEntriesEnableRegistration() {
        show(FakeAuth())
        rule.onNodeWithText(text(R.string.password)).performTextInput("1234")
        rule.onNodeWithText(text(R.string.re_enter_password)).performTextInput("1234")
        rule.onNodeWithText(text(R.string.register)).assertIsEnabled()
    }

    @Test fun differentEntriesShowMessage() {
        show(FakeAuth())
        rule.onNodeWithText(text(R.string.password)).performTextInput("1234")
        rule.onNodeWithText(text(R.string.re_enter_password)).performTextInput("1235")
        rule.onNodeWithText(text(R.string.register)).performClick()
        rule.onNodeWithText(text(R.string.incorrect_re_password)).assertIsDisplayed()
    }

    @Test fun wrongPinShowsMessage() {
        show(FakeAuth(registered = "1234"))
        rule.onNodeWithText(text(R.string.password)).performTextInput("9999")
        rule.onNodeWithText(text(R.string.login)).performClick()
        rule.onNodeWithText(text(R.string.incorrect_password)).assertIsDisplayed()
    }

    @Test fun rightPinCompletes() {
        var done = false
        show(FakeAuth(registered = "1234")) { done = true }
        rule.onNodeWithText(text(R.string.password)).performTextInput("1234")
        rule.onNodeWithText(text(R.string.login)).performClick()
        rule.waitForIdle()
        assertTrue(done)
    }
}
