package defdev.labs.droid.vulnabank.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionsDaoTest {
    private lateinit var database: TransactionsDatabase

    @Before
    fun open() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            TransactionsDatabase::class.java,
        ).build()
    }

    @After
    fun close() {
        database.close()
    }

    @Test
    fun insertedTransactionIsListed() = runBlocking {
        val dao = database.transactionsDao()
        dao.insertTransaction(Transaction(state = 0, hash = "a1", date = 10))
        val list = dao.findAllTransaction().first()
        assertEquals(1, list.size)
        assertEquals("a1", list[0].hash)
    }

    @Test
    fun deletedTransactionIsGone() = runBlocking {
        val dao = database.transactionsDao()
        val id = dao.insertTransaction(Transaction(state = 1, hash = "b2", date = 20))
        dao.deleteTransaction(Transaction(id = id.toInt(), state = 1, hash = "b2", date = 20))
        assertEquals(0, dao.findAllTransaction().first().size)
    }
}
