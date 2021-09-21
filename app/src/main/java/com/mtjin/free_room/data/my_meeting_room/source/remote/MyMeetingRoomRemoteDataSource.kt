package com.mtjin.free_room.data.my_meeting_room.source.remote

import com.mtjin.free_room.model.MyMeetingRoom
import io.reactivex.Completable
import io.reactivex.Flowable

interface MyMeetingRoomRemoteDataSource {
    fun requestMyMeetingRooms(): Flowable<List<MyMeetingRoom>>
    fun cancelReservation(myMeetingRoom: MyMeetingRoom): Completable
}