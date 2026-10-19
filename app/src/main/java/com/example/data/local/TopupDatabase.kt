package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.OrderRecord
import com.example.data.model.SavedUid
import com.example.data.model.SupportChatMessage
import com.example.data.model.UserTransactionRecord

@Database(
    entities = [
        OrderRecord::class,
        SavedUid::class,
        UserTransactionRecord::class,
        SupportChatMessage::class
    ],
    version = 4,
    exportSchema = false
)
abstract class TopupDatabase : RoomDatabase() {
    abstract fun topupDao(): TopupDao
    abstract fun userTransactionDao(): UserTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TopupDatabase? = null

        fun getDatabase(context: Context): TopupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TopupDatabase::class.java,
                    "johirul_topup_bd.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
