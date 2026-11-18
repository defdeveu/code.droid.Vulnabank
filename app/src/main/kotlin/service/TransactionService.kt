package defdev.labs.droid.vulnabank.service

import android.util.Log
import defdev.labs.droid.vulnabank.error.HttpError
import java.net.HttpURLConnection
import java.net.URL

data class Request(val enckey: String, val message: String, val signature: String) {
    fun toXml(): String =
        "<request><enckey>$enckey</enckey><message>$message</message><signature>$signature</signature></request>"
}

interface TransactionService {
    fun sendTransaction(message: Request): String
}

class HttpTransactionService(private val baseUrl: String) : TransactionService {

    override fun sendTransaction(message: Request): String {
        val body = message.toXml()
        val connection = URL(baseUrl).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "text/xml")
            connection.setRequestProperty("Accept-Charset", "utf-8")
            Log.d(TAG, "--> POST $baseUrl")
            connection.requestProperties.forEach { (name, values) -> Log.d(TAG, "$name: ${values.joinToString()}") }
            Log.d(TAG, body)
            Log.d(TAG, "--> END POST (${body.length}-byte body)")

            connection.outputStream.use { it.write(body.toByteArray()) }

            val code = connection.responseCode
            Log.d(TAG, "<-- $code ${connection.responseMessage} $baseUrl")
            connection.headerFields.forEach { (name, values) ->
                if (name != null) Log.d(TAG, "$name: ${values.joinToString()}")
            }
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            Log.d(TAG, response)
            Log.d(TAG, "<-- END HTTP (${response.length}-byte body)")

            if (code !in 200..299) throw HttpError(code)
            return response
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        private const val TAG = "TransactionService"
    }
}
