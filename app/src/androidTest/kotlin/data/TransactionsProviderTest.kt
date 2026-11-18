package defdev.labs.droid.vulnabank.data

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import defdev.labs.droid.vulnabank.VulnaBankApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionsProviderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val resolver: ContentResolver = context.contentResolver
    private val dao = (context.applicationContext as VulnaBankApplication).container.database.transactionsDao()
    private val tableUri: Uri =
        Uri.parse("content://" + TransactionsProvider.AUTHORITY + "/" + Transaction.TABLE_NAME)

    @Before
    fun clear() = runBlocking {
        dao.findAllTransaction().first().forEach { dao.deleteTransaction(it) }
    }

    @Test
    fun insertAddsTransaction() = runBlocking {
        val uri = resolver.insert(tableUri, values(hash = "c3"))
        assertNotNull(uri)
        assertEquals("c3", dao.findAllTransaction().first().single().hash)
    }

    @Test
    fun deleteRemovesTransaction() = runBlocking {
        val uri = checkNotNull(resolver.insert(tableUri, values(hash = "f6")))
        assertEquals(1, resolver.delete(uri, null, null))
        assertEquals(0, dao.findAllTransaction().first().size)
    }

    @Test
    fun typeDescribesTable() {
        assertEquals(
            "vnd.android.cursor.dir/" + TransactionsProvider.AUTHORITY + "." + Transaction.TABLE_NAME,
            resolver.getType(tableUri),
        )
    }

    private fun values(hash: String) = ContentValues().apply {
        put("state", 0)
        put("hash", hash)
        put("date", 1L)
    }
}
