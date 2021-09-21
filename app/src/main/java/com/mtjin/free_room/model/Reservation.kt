package com.mtjin.free_room.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Reservation(
    var id: Long = 0, //timestamp
    var meetingRoomId: Long = 0, //회의실 코드
    var dateTimestamp: Long = 0,
    var startTimestamp: Long = 0,
    var endTimestamp: Long = 0,
    var userId: String = "",
    var register: String = "",
    var content: String = ""
)