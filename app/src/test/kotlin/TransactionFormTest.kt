package defdev.labs.droid.vulnabank

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionFormTest {

    @Test
    fun emptyFormPointsAtName() {
        assertEquals(FormField.NAME, TransactionForm().invalidField)
    }

    @Test
    fun shortAccountNumberIsReported() {
        assertEquals(FormField.ACCOUNT_NUMBER, TransactionForm(name = "Anna", accountNumber = "1234567").invalidField)
    }

    @Test
    fun zeroAmountIsReported() {
        assertEquals(
            FormField.AMOUNT,
            TransactionForm(name = "Anna", accountNumber = "12345678", amount = "0").invalidField,
        )
    }

    @Test
    fun completeFormIsValid() {
        val form = TransactionForm(name = "Anna", accountNumber = "12345678", amount = "10")
        assertNull(form.invalidField)
        assertTrue(form.isValid)
    }

    @Test
    fun messageJoinsFieldsInOrder() {
        assertEquals(
            "Anna;12345678;10;Rent",
            TransactionForm("Anna", "12345678", "10", "Rent").message(),
        )
    }
}
