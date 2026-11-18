package defdev.labs.droid.vulnabank.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import defdev.labs.droid.vulnabank.VulnaBankApplication

class TransactionsProvider : ContentProvider() {

    private val database: TransactionsDatabase
        get() = (checkNotNull(context).applicationContext as VulnaBankApplication).container.database

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): Cursor? {
        val code = MATCHER.match(uri)
        if (code == CODE_TRANSACTIONS_DIR || code == CODE_TRANSACTIONS_ITEM) {
            val context = context ?: return null
            val cursor: Cursor = if (code == CODE_TRANSACTIONS_ITEM) {
                if (selection == null && sortOrder == null) {
                    database.transactionsDao().findAllTransactionExternal()
                } else {
                    var sql = "SELECT * FROM " + Transaction.TABLE_NAME
                    if (selection != null) sql += " WHERE " + selection
                    if (sortOrder != null) sql += " ORDER BY " + sortOrder
                    database.openHelper.readableDatabase.query(sql, selectionArgs ?: emptyArray())
                }
            } else {
                database.transactionsDao().findTransactionByIdExternal(ContentUris.parseId(uri).toInt())
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String = when (MATCHER.match(uri)) {
        CODE_TRANSACTIONS_DIR -> "vnd.android.cursor.dir/" + AUTHORITY + "." + Transaction.TABLE_NAME
        CODE_TRANSACTIONS_ITEM -> "vnd.android.cursor.item/" + AUTHORITY + "." + Transaction.TABLE_NAME
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (MATCHER.match(uri)) {
            CODE_TRANSACTIONS_DIR -> {
                val context = context ?: return null
                val id = database.transactionsDao().insertTransaction(Transaction.fromContentValues(checkNotNull(values)))
                context.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
            CODE_TRANSACTIONS_ITEM -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (MATCHER.match(uri)) {
            CODE_TRANSACTIONS_DIR -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_TRANSACTIONS_ITEM -> {
                val context = context ?: return 0
                database.transactionsDao().deleteTransactionByIdExternal(ContentUris.parseId(uri).toInt())
                context.contentResolver.notifyChange(uri, null)
                return 1
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): Int = 0

    companion object {
        const val AUTHORITY = "defdev.labs.droid.vulnabank.transactions"

        private const val CODE_TRANSACTIONS_DIR = 1
        private const val CODE_TRANSACTIONS_ITEM = 2

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, Transaction.TABLE_NAME, CODE_TRANSACTIONS_DIR)
            addURI(AUTHORITY, Transaction.TABLE_NAME + "/*", CODE_TRANSACTIONS_ITEM)
        }
    }
}
