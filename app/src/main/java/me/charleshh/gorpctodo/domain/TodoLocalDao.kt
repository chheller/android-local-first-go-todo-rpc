package me.charleshh.gorpctodo.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import domain.TodoLocalEntity
import me.charleshh.gorpctodo.AppDatabase
import javax.inject.Singleton

@Dao
interface TodoLocalDao {
    data class TodoId(val id: String)

    @Query(
        //language=roomsql
        """
            SELECT * FROM todolocalentity
        """
    )
    fun getAllTodos(): List<TodoLocalEntity>

    @Insert
    fun createTodo(todoLocalEntity: TodoLocalEntity)

    @Update
    fun updateTodo(todoLocalEntity: TodoLocalEntity)

    @Delete
    fun delete(id: TodoId)

}
// Create singleton we can inject via Hilt & Dagger
@Module
@InstallIn(SingletonComponent::class)
class TodoLocalDaoProvider {
    @Provides
    fun provideTodoLocalDao(appDatabase: AppDatabase): TodoLocalDao {
        return appDatabase.localTodoDao()
    }
}