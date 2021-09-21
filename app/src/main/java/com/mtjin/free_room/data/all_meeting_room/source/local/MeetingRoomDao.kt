package com.mtjin.free_room.data.all_meeting_room.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mtjin.free_room.model.MeetingRoom
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MeetingRoomDao {

    @Insert(onConflict = REPLACE)
    fun insertMeetingRoom(meetingRooms: List<MeetingRoom>): Completable

    @Query("SELECT * FROM meetingRoom")
    fun getMeetingRooms(): Single<List<MeetingRoom>>

    @Delete
    fun deleteMeetingRoom(meetingRoom: MeetingRoom): Completable
}