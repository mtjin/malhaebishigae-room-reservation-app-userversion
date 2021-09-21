package com.mtjin.free_room.data.my_meeting_room.source

import com.mtjin.free_room.model.MyMeetingRoom
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MyMeetingRoomRepository {
    fun requestMyMeetingRooms(): Flowable<List<MyMeetingRoom>>
    fun requestRemoteMyMeetingRooms(): Flowable<List<MyMeetingRoom>>
    fun requestLocalMyMeetingRooms(): Single<List<MyMeetingRoom>>
    fun cancelReservation(myMeetingRoom: MyMeetingRoom): Completable
}