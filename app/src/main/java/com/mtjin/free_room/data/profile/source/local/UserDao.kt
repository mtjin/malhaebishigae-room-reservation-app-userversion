package com.mtjin.free_room.data.profile.source.local

import androidx.room.*
import com.mtjin.free_room.model.User
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Completable

    @Query("SELECT * FROM user WHERE id = :id limit 1")
    fun getUser(id: String): Single<User>

    @Delete
    fun deleteUser(user: User): Completable
}