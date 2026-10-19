package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.OrderRecord
import com.example.data.model.SavedUid
import com.example.data.model.SupportChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface TopupDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderRecord>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderRecord): Long

    @Update
    suspend fun updateOrder(order: OrderRecord)

    @Delete
    suspend fun deleteOrder(order: OrderRecord)

    @Query("DELETE FROM orders")
    suspend fun clearAllOrders()

    @Query("SELECT * FROM saved_uids WHERE serviceName = :service")
    suspend fun getSavedUid(service: String): SavedUid?

    @Query("SELECT * FROM saved_uids")
    fun getAllSavedUids(): Flow<List<SavedUid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUid(uid: SavedUid)

    @Query("DELETE FROM saved_uids")
    suspend fun clearAllSavedUids()

    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllSupportMessages(): Flow<List<SupportChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportMessage(msg: SupportChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportMessages(msgs: List<SupportChatMessage>)

    @Query("DELETE FROM support_messages WHERE id = :id")
    suspend fun deleteSupportMessageById(id: String)

    @Query("DELETE FROM support_messages WHERE conversationId = :conversationId")
    suspend fun deleteSupportMessagesByConversation(conversationId: String)
}
