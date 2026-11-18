package defdev.labs.droid.vulnabank.data

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Transaction.TABLE_NAME)
data class Transaction(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var state: Int,
    var hash: String? = null,
    var date: Long,
) {
    companion object {
        const val TABLE_NAME = "transactionData"

        fun fromContentValues(values: ContentValues): Transaction {
            var state = 0
            if (values.containsKey("state")) {
                state = values.getAsInteger("state")
            }

            var hash: String? = null
            if (values.containsKey("hash")) {
                hash = values.getAsString("hash")
            }

            var date = System.currentTimeMillis()
            if (values.containsKey("date")) {
                date = values.getAsLong("date")
            }

            return Transaction(state = state, hash = hash, date = date)
        }
    }
}
