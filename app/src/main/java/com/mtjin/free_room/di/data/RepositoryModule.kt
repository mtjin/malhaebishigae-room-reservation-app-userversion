package com.mtjin.free_room.di.data


import com.mtjin.free_room.data.all_meeting_room.source.AllMeetingRoomRepositoryImpl
import com.mtjin.free_room.data.all_meeting_room.source.AllMeetingRoomRepository
import com.mtjin.free_room.data.all_meeting_room.source.local.AllMeetingRoomLocalDataSource
import com.mtjin.free_room.data.all_meeting_room.source.remote.AllMeetingRoomRemoteDataSource
import com.mtjin.free_room.data.login.source.LoginRepository
import com.mtjin.free_room.data.login.source.LoginRepositoryImpl
import com.mtjin.free_room.data.login.source.remote.LoginRemoteDataSource
import com.mtjin.free_room.data.my_meeting_room.source.MyMeetingRoomRepository
import com.mtjin.free_room.data.my_meeting_room.source.MyMeetingRoomRepositoryImpl
import com.mtjin.free_room.data.my_meeting_room.source.local.MyMeetingRoomLocalDataSource
import com.mtjin.free_room.data.my_meeting_room.source.remote.MyMeetingRoomRemoteDataSource
import com.mtjin.free_room.data.profile.source.ProfileRepository
import com.mtjin.free_room.data.profile.source.ProfileRepositoryImpl
import com.mtjin.free_room.data.profile.source.local.ProfileLocalDataSource
import com.mtjin.free_room.data.profile.source.remote.ProfileRemoteDataSource
import com.mtjin.free_room.data.reservation.source.ReservationRepository
import com.mtjin.free_room.data.reservation.source.ReservationRepositoryImpl
import com.mtjin.free_room.data.reservation.source.local.ReservationLocalDataSource
import com.mtjin.free_room.data.reservation.source.remote.ReservationRemoteDataSource
import com.mtjin.free_room.data.reservation_detail.source.ReservationDetailRepository
import com.mtjin.free_room.data.reservation_detail.source.ReservationDetailRepositoryImpl
import com.mtjin.free_room.data.reservation_detail.source.remote.ReservationDetailRemoteDataSource
import com.mtjin.free_room.data.setting.source.SettingRepository
import com.mtjin.free_room.data.setting.source.SettingRepositoryImpl
import com.mtjin.free_room.data.setting.source.local.SettingLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class RepositoryModule {
    @Provides
    @Reusable
    fun provideAllRoomRepository(
        allMeetingRoomRemoteDataSource: AllMeetingRoomRemoteDataSource,
        allMeetingRoomLocalDataSource: AllMeetingRoomLocalDataSource
    ): AllMeetingRoomRepository =
        AllMeetingRoomRepositoryImpl(allMeetingRoomRemoteDataSource, allMeetingRoomLocalDataSource)


    @Provides
    @Reusable
    fun provideReservationRepository(
        reservationRemoteDataSource: ReservationRemoteDataSource,
        reservationLocalDataSource: ReservationLocalDataSource
    ): ReservationRepository =
        ReservationRepositoryImpl(reservationRemoteDataSource, reservationLocalDataSource)


    @Provides
    @Reusable
    fun provideReservationDetailRepository(
        reservationDetailRemoteDataSource: ReservationDetailRemoteDataSource
    ): ReservationDetailRepository =
        ReservationDetailRepositoryImpl(reservationDetailRemoteDataSource)


    @Provides
    @Reusable
    fun provideMyMeetingRoomRepository(
        myMeetingRoomRemoteDataSource: MyMeetingRoomRemoteDataSource,
        myMeetingRoomLocalDataSource: MyMeetingRoomLocalDataSource
    ): MyMeetingRoomRepository =
        MyMeetingRoomRepositoryImpl(myMeetingRoomRemoteDataSource, myMeetingRoomLocalDataSource)


    @Provides
    @Reusable
    fun provideProfileRepository(
        profileRemoteDataSource: ProfileRemoteDataSource,
        profileLocalDataSource: ProfileLocalDataSource
    ): ProfileRepository = ProfileRepositoryImpl(profileRemoteDataSource, profileLocalDataSource)


    @Provides
    @Reusable
    fun provideLoginRepository(
        loginRemoteDataSource: LoginRemoteDataSource
    ): LoginRepository = LoginRepositoryImpl(loginRemoteDataSource)

    @Provides
    @Reusable
    fun provideSettingRepository(
        settingLocalDataSource: SettingLocalDataSource
    ): SettingRepository = SettingRepositoryImpl(settingLocalDataSource)
}
