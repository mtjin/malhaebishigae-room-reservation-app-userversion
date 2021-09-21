package com.mtjin.free_room.data.all_meeting_room.source.remote

import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.MEETING_ROOM
import com.mtjin.free_room.utils.NAME
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AllMeetingRoomRemoteDataSourceImpl @Inject constructor(private val database: DatabaseReference) :
    AllMeetingRoomRemoteDataSource {

    override fun requestAllMeetingRooms(): Single<List<MeetingRoom>> {
        return Single.create<List<MeetingRoom>> { emitter ->
            database.child(MEETING_ROOM).child(BUSINESS_CODE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val meetingRoomList: ArrayList<MeetingRoom> = ArrayList()
                        for (roomSnapShot: DataSnapshot in snapshot.children) {
                            roomSnapShot.getValue(MeetingRoom::class.java)?.let {
                                meetingRoomList.add(it)
                            }
                        }
                        emitter.onSuccess(meetingRoomList)
                    }
                })
        }
    }

    override fun requestSearchRooms(roomName: String): Single<List<MeetingRoom>> {
        Timber.d("검색한 회의실이름 결과 11-> $roomName")
        return Single.create<List<MeetingRoom>> { emitter ->
            database.child(MEETING_ROOM).child(BUSINESS_CODE).orderByChild(NAME).startAt(roomName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        Timber.d("검색한 회의실이름 결과 22-> $snapshot")
                        val meetingRoomList: ArrayList<MeetingRoom> = ArrayList()
                        for (roomSnapShot: DataSnapshot in snapshot.children) {
                            roomSnapShot.getValue(MeetingRoom::class.java)?.let {
                                if (it.name.startsWith(roomName, true)) meetingRoomList.add(it)
                            }
                        }
                        emitter.onSuccess(meetingRoomList)
                    }
                })
        }
    }

}