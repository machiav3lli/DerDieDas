package com.machiav3lli.derdiedas.data

import android.database.SQLException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NounDao {
    @Query("SELECT COUNT(*) FROM noun")
    suspend fun getAllNounCount(): Int

    @Query("SELECT COUNT(*) FROM noun WHERE isMastered = 0")
    suspend fun getPendingNounCount(): Int

    @Query("SELECT COUNT(*) FROM noun WHERE isMastered = 1")
    suspend fun getMasteredNounCount(): Int

    @Insert
    @Throws(SQLException::class)
    suspend fun insert(vararg nouns: Noun)

    @Insert
    @Throws(SQLException::class)
    suspend fun insertAll(nouns: List<Noun>)

    @Update
    suspend fun update(noun: Noun)

    @Query(
        """
        SELECT * FROM noun
        WHERE isMastered = 0 
          AND (totalReviews > 0 OR originalOrder < :learningWindowSize)
        ORDER BY reviewScore DESC, originalOrder ASC
    """
    )
    fun getActiveNouns(learningWindowSize: Int): Flow<List<Noun>>

    @Delete
    suspend fun delete(noun: Noun)

    @Query("DELETE FROM noun")
    suspend fun deleteAll()

    @Query(
        """
        UPDATE noun
        SET isMastered = 0, 
            reviewScore = 100.0
    """
    )
    suspend fun resetAllToActive()
}