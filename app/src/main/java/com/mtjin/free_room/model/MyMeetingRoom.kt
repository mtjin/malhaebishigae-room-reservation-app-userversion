package com.mtjin.free_room.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "myMeetingRoom")
data class MyMeetingRoom(
    var id: Long = 0, //희의실 코드(meetingroom id)
    @PrimaryKey var timestamp: Long = 0, //타임스탬프(reservation id)
    var roomName: String = "",
    var image: String = "",
    var num: Int = 0,
    var startTimestamp: Long = 0,
    var endTimestamp: Long = 0,
    var content: String = "",
    var register: String = ""
) : Parcelable