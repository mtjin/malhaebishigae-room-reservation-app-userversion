package com.mtjin.free_room.di.data

import android.content.Context
import androidx.room.Room
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSource
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSourceImpl
import com.mtjin.free_room.data.all_meeting_room.source.local.MeetingRoomDao
import com.mtjin.free_room.data.database.RoomDatabase
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomDao
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomLocalDataSource
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomLocalDataSourceImpl
import com.mtjin.free_room.data.profile.source.local.ProfileLocalDataSource
import com.mtjin.free_room.data.profile.source.local.ProfileLocalDataSourceImpl
import com.mtjin.free_room.data.profile.source.local.UserDao
import com.mtjin.free_room.data.reservation.source.local.ReservationLocalDataSource
import com.mtjin.free_room.data.reservation.source.local.ReservationLocalDataSourceImpl
import com.mtjin.free_room.data.setting.source.local.SettingLocalDataSource
import com.mtjin.free_room.data.setting.source.local.SettingLocalDataSourceImpl
import com.mtjin.free_room.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class LocalDataModule {

    @Provides
    @Reusable
    fun provideAllRoomLocalDataSource(meetingRoomDao: MeetingRoomDao): AllMeetingRoomLocalDataSource =
        AllMeetingRoomLocalDataSourceImpl(meetingRoomDao)


    @Provides
    @Reusable
    fun provideMyMeetingRoomLocalDataSource(myMeetingRoomDao: MyMeetingRoomDao): MyMeetingRoomLocalDataSource =
        MyMeetingRoomLocalDataSourceImpl(myMeetingRoomDao)


    @Provides
    @Reusable
    fun provideProfileLocalDataSource(userDao: UserDao, context: Context): ProfileLocalDataSource =
        ProfileLocalDataSourceImpl(userDao, context)


    @Provides
    @Reusable
    fun provideReservationLocalDataSource(preferenceManager: PreferenceManager): ReservationLocalDataSource =
        ReservationLocalDataSourceImpl(preferenceManager)

    @Provides
    @Reusable
    fun provideSettingLocalDataSource(
        roomDatabase: RoomDatabase,
        preferenceManager: PreferenceManager
    ): SettingLocalDataSource =
        SettingLocalDataSourceImpl(roomDatabase, preferenceManager)


    @Provides
    @Singleton
    fun provideRoomDatabase(context: Context): RoomDatabase {
        return Room.databaseBuilder(
            context,
            RoomDatabase::class.java,
            "WorksRoom.db"
        ).build()
    }

    @Provides
    @Reusable
    fun provideAllRoomDao(roomDatabase: RoomDatabase): MeetingRoomDao =
        roomDatabase.meetingRoomDao()

    @Provides
    @Reusable
    fun provideMyRoomDao(roomDatabase: RoomDatabase): MyMeetingRoomDao =
        roomDatabase.myMeetingRoomDao()

    @Provides
    @Reusable
    fun provideUserDao(roomDatabase: RoomDatabase): UserDao =
        roomDatabase.userDao()

}