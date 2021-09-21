package com.mtjin.free_room.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.data.all_meeting_room.source.local.MeetingRoomDao
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomDao
import com.mtjin.free_room.model.User
import com.mtjin.free_room.data.profile.source.local.UserDao

@Database(
    entities = [MeetingRoom::class, MyMeetingRoom::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun meetingRoomDao(): MeetingRoomDao
    abstract fun myMeetingRoomDao(): MyMeetingRoomDao
    abstract fun userDao(): UserDao
}