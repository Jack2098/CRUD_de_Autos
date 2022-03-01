package kotlinforandroid.book.cruddeautos

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AutoDao {
    @Query(value = "Select * from auto")
    fun getAll(): LiveData<List<Auto>>
    @Query(value = "Select * from auto where idAuto=:id")
    fun get(id:Int):LiveData<Auto>
    @Insert
    fun insertAll(vararg auto:Auto)
    @Update
    fun update(auto: Auto)
    @Delete
    fun delete(auto: Auto)
}