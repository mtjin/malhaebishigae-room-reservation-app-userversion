package com.mtjin.free_room.views.main

import com.mtjin.free_room.di.ActivityScope
import com.mtjin.free_room.views.all_meeting_room.AllMeetingRoomFragment
import com.mtjin.free_room.views.login.LoginFragment
import com.mtjin.free_room.views.my_meeting_room.MyMeetingRoomFragment
import com.mtjin.free_room.views.profile.ProfileFragment
import com.mtjin.free_room.views.reservation.ReservationFragment
import com.mtjin.free_room.views.reservation_detail.ReservationDetailFragment
import com.mtjin.free_room.views.setting.SettingFragment
import dagger.Subcomponent

// Scope annotation that the RegistrationComponent uses
// Classes annotated with @ActivityScope will have a unique instance in this Component
@ActivityScope
@Subcomponent
interface MainComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(allMeetingRoomFragment: AllMeetingRoomFragment)
    fun inject(myMeetingRoomFragment: MyMeetingRoomFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(reservationFragment: ReservationFragment)
    fun inject(reservationDetailFragment: ReservationDetailFragment)
    fun inject(settingFragment: SettingFragment)
    fun inject(loginFragment: LoginFragment)
}