package com.mtjin.free_room.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "meetingRoom")
data class MeetingRoom(
    @PrimaryKey var id: Long = 0,
    var name: String = "",
    var image: String = "",
    var num: Int = 0,
    var isDisabled: Boolean = false
) : Parcelable