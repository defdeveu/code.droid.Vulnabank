package defdev.labs.droid.vulnabank.service

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import defdev.labs.droid.vulnabank.data.TransactionsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: TransactionsDatabase
    private val crypto = DefaultCryptoService(context)
    private var reply = ""
    private val service = object : TransactionService {
        override fun sendTransaction(message: Request): String = reply
    }

    @Before
    fun open() {
        database = Room.inMemoryDatabaseBuilder(context, TransactionsDatabase::class.java).build()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun responseIsReadIntoTransaction() = runBlocking {
        reply = "<response>" + crypto.encryptAES("<code>0</code><id>abc123</id>") + "</response>"
        val repository = DefaultTransactionRepository(database.transactionsDao(), crypto, service, Dispatchers.IO)
        val transaction = repository.sendTransactionMessage("Anna;12345678;10;").getOrThrow()
        assertEquals(0, transaction.state)
        assertEquals("abc123", transaction.hash)
    }

    @Test
    fun encryptedTextRoundTrips() {
        assertEquals("Anna;12345678;10;", crypto.decryptAES(crypto.encryptAES("Anna;12345678;10;")))
    }
}
