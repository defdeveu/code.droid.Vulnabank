package defdev.labs.droid.vulnabank.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionsDao {

    @Query("SELECT * from transactionData")
    fun findAllTransaction(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(item: Transaction): Long

    @Delete
    fun deleteTransaction(item: Transaction)

    @Query("SELECT * from transactionData")
    fun findAllTransactionExternal(): Cursor

    @Query("SELECT * from transactionData WHERE id = :id")
    fun findTransactionByIdExternal(id: Int): Cursor

    @Query("DELETE FROM ${Transaction.TABLE_NAME} WHERE id = :id")
    fun deleteTransactionByIdExternal(id: Int)
}
