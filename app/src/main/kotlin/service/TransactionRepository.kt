package defdev.labs.droid.vulnabank.service

import android.util.Xml
import defdev.labs.droid.vulnabank.data.Transaction
import defdev.labs.droid.vulnabank.data.TransactionsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

interface TransactionRepository {
    fun getTransactionList(): Flow<List<Transaction>>
    suspend fun insertTransactionItem(item: Transaction)
    suspend fun deleteTransactionById(transaction: Transaction)
    suspend fun sendTransactionMessage(message: String): Result<Transaction>
}

class DefaultTransactionRepository(
    private val dao: TransactionsDao,
    private val cryptoService: CryptoService,
    private val transactionService: TransactionService,
    private val ioDispatcher: CoroutineDispatcher,
) : TransactionRepository {

    override fun getTransactionList(): Flow<List<Transaction>> = dao.findAllTransaction()

    override suspend fun insertTransactionItem(item: Transaction) {
        withContext(ioDispatcher) { dao.insertTransaction(item) }
    }

    override suspend fun deleteTransactionById(transaction: Transaction) {
        withContext(ioDispatcher) { dao.deleteTransaction(transaction) }
    }

    override suspend fun sendTransactionMessage(message: String): Result<Transaction> {
        val response = withContext(ioDispatcher) {
            runCatching {
                val encMessage = cryptoService.encryptAES(message)
                val transactionDto = Request(
                    cryptoService.encryptRSA(),
                    encMessage,
                    cryptoService.signRSA(encMessage),
                )
                transactionService.sendTransaction(transactionDto)
            }
        }
        return response.map { decryptMessage(it) }
    }

    private fun decryptMessage(response: String): Transaction {
        val body = response.substringAfter(RESPONSE_OPEN).substringBefore(RESPONSE_CLOSE)
        return parse(ByteArrayInputStream(cryptoService.decryptAES(body).toByteArray(Charsets.UTF_8)))
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): Transaction {
        inputStream.use { stream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(stream, null)
            parser.nextTag()
            return readText(parser)
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): Transaction {
        var code = ""
        var hash = ""
        if (parser.next() == XmlPullParser.TEXT) {
            code = parser.text
            parser.next()
            parser.nextTag()
            parser.next()
            hash = parser.text
        }
        return Transaction(state = code.toInt(), hash = hash, date = System.currentTimeMillis())
    }

    companion object {
        private const val RESPONSE_OPEN = "<response>"
        private const val RESPONSE_CLOSE = "</response>"
    }
}
