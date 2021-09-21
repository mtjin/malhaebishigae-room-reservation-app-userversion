package com.mtjin.free_room.data.my_meeting_room.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MyMeetingRoomDao {

    @Insert(onConflict = REPLACE)
    fun insertMyMeetingRoom(myMeetingRooms: List<MyMeetingRoom>): Completable

    @Query("SELECT * FROM myMeetingRoom WHERE endTimestamp > :timestamp")
    fun getMyMeetingRooms(timestamp: Long = getTimestamp()): Single<List<MyMeetingRoom>>

    @Delete
    fun deleteMyMeetingRoom(myMeetingRoom: MyMeetingRoom): Completable
}