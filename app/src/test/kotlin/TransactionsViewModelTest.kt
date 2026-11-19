package defdev.labs.droid.vulnabank

import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.service.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {
    private val repository = FakeTransactions()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun model() = TransactionsViewModel(repository) { "text-$it" }

    @Test
    fun successfulSendStoresReturnedTransaction() {
        repository.reply = Result.success(Transaction(state = 0, hash = "abc", date = 5))
        val model = model()
        model.openForm()
        model.updateForm(TransactionForm("Anna", "12345678", "10", "Rent"))
        model.confirmForm()
        assertEquals("Anna;12345678;10;Rent", repository.sent)
        assertEquals("abc", repository.items.value.single().hash)
        assertFalse(model.uiState.value.isSending)
        assertNull(model.uiState.value.form)
    }

    @Test
    fun failedSendStoresFailedRow() {
        repository.reply = Result.failure(UnknownHostException())
        val model = model()
        model.openForm()
        model.updateForm(TransactionForm("Anna", "12345678", "10", ""))
        model.confirmForm()
        val row = repository.items.value.single()
        assertEquals(1, row.state)
        assertEquals("text-${R.string.no_internet}", row.hash)
        assertEquals(R.string.no_internet, model.uiState.value.sendError)
    }

    @Test
    fun otherFailuresUseGeneralError() {
        repository.reply = Result.failure(IllegalStateException())
        val model = model()
        model.openForm()
        model.updateForm(TransactionForm("Anna", "12345678", "10", ""))
        model.confirmForm()
        assertEquals(R.string.general, model.uiState.value.sendError)
    }

    @Test
    fun invalidFormIsNotSent() {
        val model = model()
        model.openForm()
        model.updateForm(TransactionForm("Anna", "123", "10", ""))
        model.confirmForm()
        assertNull(repository.sent)
        assertNotNull(model.uiState.value.form)
    }

    @Test
    fun cancelClosesForm() {
        val model = model()
        model.openForm()
        model.cancelForm()
        assertNull(model.uiState.value.form)
    }

    @Test
    fun confirmedDeleteRemovesRow() {
        val row = Transaction(id = 1, state = 0, hash = "abc", date = 5)
        repository.items.value = listOf(row)
        val model = model()
        model.askDelete(row)
        model.confirmDelete()
        assertEquals(0, repository.items.value.size)
        assertNull(model.uiState.value.pendingDelete)
    }

    @Test
    fun dismissedDeleteKeepsRow() {
        val row = Transaction(id = 1, state = 0, hash = "abc", date = 5)
        repository.items.value = listOf(row)
        val model = model()
        model.askDelete(row)
        model.dismissDelete()
        assertEquals(1, repository.items.value.size)
    }

    @Test
    fun refreshEnds() {
        val model = model()
        model.refresh()
        assertFalse(model.uiState.value.isRefreshing)
    }
}

private class FakeTransactions : TransactionRepository {
    val items = MutableStateFlow<List<Transaction>>(emptyList())
    var reply: Result<Transaction> = Result.failure(IllegalStateException())
    var sent: String? = null

    override fun getTransactionList(): Flow<List<Transaction>> = items
    override suspend fun insertTransactionItem(item: Transaction) {
        items.value = items.value + item
    }
    override suspend fun deleteTransactionById(transaction: Transaction) {
        items.value = items.value - transaction
    }
    override suspend fun sendTransactionMessage(message: String): Result<Transaction> {
        sent = message
        return reply
    }
}
