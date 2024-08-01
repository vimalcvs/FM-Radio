package com.vimal.margh.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vimal.margh.models.ModelRadio


@Dao
interface RoomDao {

    @Query("Select * from table_radio")
    fun getAllFavorite(): LiveData<List<ModelRadio>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(modelRadio: ModelRadio)

    @Delete
    fun deleteFavorite(modelRadio: ModelRadio)

    @Query("DELETE FROM table_radio")
    fun deleteAllFavorite()

    @Query("SELECT EXISTS (SELECT 1 FROM table_radio WHERE radio_id = :id)")
    fun isFavorite(id: Int): Boolean

}