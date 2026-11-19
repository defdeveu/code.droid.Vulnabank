package defdev.labs.droid.vulnabank.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import defdev.labs.droid.vulnabank.R
import defdev.labs.droid.vulnabank.TransactionsViewModel
import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.theme.VulnaBankTheme
import org.junit.Rule
import org.junit.Test

class TransactionsScreenTest {
    @get:Rule val rule = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private fun text(id: Int) = context.getString(id)

    private fun show(repository: FakeTransactions) {
        val model = TransactionsViewModel(repository, context::getString)
        rule.setContent { VulnaBankTheme { TransactionsScreen(model) } }
    }

    @Test fun emptyListShowsPlaceholder() {
        show(FakeTransactions())
        rule.onNodeWithText(text(R.string.no_transactions)).assertIsDisplayed()
    }

    @Test fun rowShowsStateAndId() {
        show(FakeTransactions(listOf(Transaction(id = 1, state = 0, hash = "abc123", date = 1))))
        rule.onNodeWithText(text(R.string.success)).assertIsDisplayed()
        rule.onNodeWithText("abc123").assertIsDisplayed()
    }

    @Test fun failedSendShowsMessageAndRow() {
        show(FakeTransactions())
        rule.onNodeWithText(text(R.string.new_transaction), useUnmergedTree = true).performClick()
        rule.onNodeWithText(text(R.string.recipient_name)).performTextInput("Anna")
        rule.onNodeWithText(text(R.string.account_number)).performTextInput("12345678")
        rule.onNodeWithText(text(R.string.amount)).performTextInput("10")
        rule.onNodeWithText(text(android.R.string.ok)).performClick()
        rule.waitForIdle()
        rule.onNodeWithText(text(R.string.fail)).assertIsDisplayed()
    }

    @Test fun deleteAsksForConfirmation() {
        show(FakeTransactions(listOf(Transaction(id = 1, state = 0, hash = "abc123", date = 1))))
        rule.onNodeWithContentDescription(text(R.string.delete)).performClick()
        rule.onNodeWithText(text(R.string.delete_title)).assertIsDisplayed()
    }
}
