package com.mtjin.free_room.data.my_meeting_room.source.remote

import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.model.Reservation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mtjin.free_room.utils.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class MyMeetingRoomRemoteDataSourceImpl @Inject constructor(private val database: DatabaseReference) :
    MyMeetingRoomRemoteDataSource {

    override fun requestMyMeetingRooms(): Flowable<List<MyMeetingRoom>> {
        return Flowable.create<List<MyMeetingRoom>>({ emitter ->
            database.child(MEETING_ROOM).child(BUSINESS_CODE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val meetingRoomList = ArrayList<MeetingRoom>()
                        for (meetingRoomSnapShot in snapshot.children) {
                            meetingRoomSnapShot.getValue(MeetingRoom::class.java)?.let {
                                meetingRoomList.add(it)
                            }
                        }
                        database.child(RESERVATION).child(BUSINESS_CODE).orderByChild(ID)
                            .startAt(getTimestamp().toDouble()).addValueEventListener(object :
                                ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    emitter.onError(error.toException())
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val myMeetingRoomList =
                                        ArrayList<MyMeetingRoom>()
                                    for (reservationSnapshot: DataSnapshot in snapshot.children) {
                                        reservationSnapshot.getValue(
                                            Reservation::class.java
                                        )
                                            ?.let { reservation ->
                                                for (meetingRoom in meetingRoomList) { //mapping
                                                    if (meetingRoom.id == reservation.meetingRoomId && reservation.userId == uuid) {
                                                        myMeetingRoomList.add(
                                                            MyMeetingRoom(
                                                                id = meetingRoom.id,
                                                                timestamp = reservation.id,
                                                                roomName = meetingRoom.name,
                                                                image = meetingRoom.image,
                                                                num = meetingRoom.num,
                                                                startTimestamp = reservation.startTimestamp,
                                                                endTimestamp = reservation.endTimestamp,
                                                                content = reservation.content,
                                                                register = reservation.register
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                    }
                                    emitter.onNext(myMeetingRoomList)
                                }
                            })
                    }
                })
        }, BackpressureStrategy.BUFFER)
    }

    override fun cancelReservation(myMeetingRoom: MyMeetingRoom): Completable {
        return Completable.create { emitter ->
            database.child(RESERVATION).child(BUSINESS_CODE)
                .child(
                    myMeetingRoom.timestamp.toString()
                ).removeValue().addOnSuccessListener {
                    emitter.onComplete()
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}
