package com.mtjin.free_room.di.data

import android.content.Context
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSource
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSourceImpl
import com.mtjin.free_room.data.login.source.remote.LoginRemoteDataSource
import com.mtjin.free_room.data.login.source.remote.LoginRemoteDataSourceImpl
import com.mtjin.free_room.data.my_meeting_room.source.remote.MyMeetingRoomRemoteDataSource
import com.mtjin.free_room.data.my_meeting_room.source.remote.MyMeetingRoomRemoteDataSourceImpl
import com.mtjin.free_room.data.profile.source.remote.ProfileRemoteDataSource
import com.mtjin.free_room.data.profile.source.remote.ProfileRemoteDataSourceImpl
import com.mtjin.free_room.data.reservation.source.remote.ReservationRemoteDataSource
import com.mtjin.free_room.data.reservation.source.remote.ReservationRemoteDataSourceImpl
import com.mtjin.free_room.data.reservation_detail.source.remote.ReservationDetailRemoteDataSource
import com.mtjin.free_room.data.reservation_detail.source.remote.ReservationDetailRemoteDataSourceImpl
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class RemoteDataModule {
    @Provides
    @Reusable
    fun provideAllMeetingRoomRemoteDataSource(database: DatabaseReference): AllMeetingRoomRemoteDataSource =
        AllMeetingRoomRemoteDataSourceImpl(database)

    @Provides
    @Reusable
    fun provideReservationRemoteDataSource(
        database: DatabaseReference,
        context: Context
    ): ReservationRemoteDataSource =
        ReservationRemoteDataSourceImpl(database, context)

    @Provides
    @Reusable
    fun provideReservationDetailRemoteDataSource(database: DatabaseReference): ReservationDetailRemoteDataSource =
        ReservationDetailRemoteDataSourceImpl(database)

    @Provides
    @Reusable
    fun provideMyMeetingRoomRemoteDataSource(database: DatabaseReference): MyMeetingRoomRemoteDataSource =
        MyMeetingRoomRemoteDataSourceImpl(database)

    @Provides
    @Reusable
    fun provideProfileRemoteDataSource(database: DatabaseReference): ProfileRemoteDataSource =
        ProfileRemoteDataSourceImpl(database)

    @Provides
    @Reusable
    fun provideLoginRemoteDataSource(database: DatabaseReference): LoginRemoteDataSource =
        LoginRemoteDataSourceImpl(database)

    @Provides
    @Reusable
    fun provideFireDatabase(): DatabaseReference = Firebase.database.reference

}