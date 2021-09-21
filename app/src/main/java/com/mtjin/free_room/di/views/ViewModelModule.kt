package com.mtjin.free_room.di.views

import androidx.lifecycle.ViewModel
import com.mtjin.free_room.di.views.ViewModelKey
import com.mtjin.free_room.views.all_meeting_room.AllMeetingRoomViewModel
import com.mtjin.free_room.views.login.LoginViewModel
import com.mtjin.free_room.views.my_meeting_room.MyMeetingRoomViewModel
import com.mtjin.free_room.views.profile.ProfileViewModel
import com.mtjin.free_room.views.reservation.ReservationViewModel
import com.mtjin.free_room.views.reservation_detail.ReservationDetailViewModel
import com.mtjin.free_room.views.setting.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AllMeetingRoomViewModel::class)
    abstract fun bindAllRoomViewModel(allMeetingRoomViewModel: AllMeetingRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReservationViewModel::class)
    abstract fun bindReservationViewModel(reservationViewModel: ReservationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReservationDetailViewModel::class)
    abstract fun bindReservationDetailViewModel(reservationDetailViewModel: ReservationDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyMeetingRoomViewModel::class)
    abstract fun bindMyMeetingRoomViewModel(myMeetingRoomViewModel: MyMeetingRoomViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(settingViewModel: SettingViewModel): ViewModel
}