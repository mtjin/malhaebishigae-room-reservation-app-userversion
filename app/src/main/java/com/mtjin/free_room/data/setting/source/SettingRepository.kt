package com.mtjin.free_room.data.setting.source

import io.reactivex.Completable

interface SettingRepository {
    fun deleteAll(): Completable
    var alarmSetting: Boolean
    var businessCode: String
}