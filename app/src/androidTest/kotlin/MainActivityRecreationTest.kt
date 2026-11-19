package defdev.labs.droid.vulnabank

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class MainActivityRecreationTest {
    @get:Rule val rule = createEmptyComposeRule()
    private fun text(id: Int) = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test fun selectedScreenSurvivesRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            rule.onNodeWithContentDescription(text(R.string.settings)).performClick()
            rule.onNodeWithText(text(R.string.new_password)).assertIsDisplayed()
            scenario.recreate()
            rule.onNodeWithText(text(R.string.new_password)).assertIsDisplayed()
        }
    }
}
