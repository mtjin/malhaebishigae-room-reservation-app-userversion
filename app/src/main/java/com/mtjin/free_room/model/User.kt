package com.mtjin.free_room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "user")
data class User(
    @PrimaryKey var id: String = "",
    var fcm: String = "",
    var name: String = "",
    var pw: String = ""
)