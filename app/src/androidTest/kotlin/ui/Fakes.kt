package defdev.labs.droid.vulnabank.ui

import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.service.AuthRepository
import defdev.labs.droid.vulnabank.service.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuth(var registered: String? = null, var address: String = "https://example.test/request") : AuthRepository {
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

class FakeTransactions(initial: List<Transaction> = emptyList()) : TransactionRepository {
    val items = MutableStateFlow(initial)
    var reply: Result<Transaction> = Result.failure(IllegalStateException())

    override fun getTransactionList(): Flow<List<Transaction>> = items
    override suspend fun insertTransactionItem(item: Transaction) {
        items.value = items.value + item
    }
    override suspend fun deleteTransactionById(transaction: Transaction) {
        items.value = items.value - transaction
    }
    override suspend fun sendTransactionMessage(message: String): Result<Transaction> = reply
}
