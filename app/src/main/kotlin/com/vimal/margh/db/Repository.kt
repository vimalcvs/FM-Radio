package com.vimal.margh.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.vimal.margh.models.ModelRadio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Repository(context: Context?) {

    private val roomDao: RoomDao
    private val favoriteLiveData: LiveData<List<ModelRadio>>

    init {
        val database = RoomDB.getDatabase(context!!)
        roomDao = database.getDao()
        favoriteLiveData = roomDao.getAllFavorite()
    }

    fun allFavorite(): LiveData<List<ModelRadio>> {
        return favoriteLiveData
    }

    fun deleteFavorite(model: ModelRadio?) {
        object : Thread(Runnable { roomDao.deleteFavorite(model!!) }) {}.start()
    }

    fun insertFavorite(model: ModelRadio?) {
        object : Thread(Runnable { roomDao.insertFavorite(model!!) }) {
        }.start()
    }


    fun deleteAllFavorite() {
        object : Thread(Runnable { roomDao.deleteAllFavorite() }) {
        }.start()
    }


    fun isFavorite(id: Int): Boolean {
        return runBlocking {
            withContext(Dispatchers.IO) {
                roomDao.isFavorite(id)
            }
        }
    }


    companion object {
        private var repository: Repository? = null
        fun getInstance(context: Context?): Repository? {
            if (repository == null) {
                repository = Repository(context)
            }
            return repository
        }
    }
}