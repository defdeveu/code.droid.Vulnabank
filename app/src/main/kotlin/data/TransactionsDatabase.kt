package defdev.labs.droid.vulnabank.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionsDatabase : RoomDatabase() {
    abstract fun transactionsDao(): TransactionsDao

    companion object {
        const val NAME = "transactionsDB"

        fun create(context: Context): TransactionsDatabase =
            Room.databaseBuilder(context.applicationContext, TransactionsDatabase::class.java, NAME).build()
    }
}
