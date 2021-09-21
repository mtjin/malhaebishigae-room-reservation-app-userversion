package com.mtjin.free_room.data.setting.source

import com.mtjin.free_room.data.setting.source.local.SettingLocalDataSource
import io.reactivex.Completable
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(private val settingLocalDataSource: SettingLocalDataSource) :
    SettingRepository {
    override fun deleteAll(): Completable = settingLocalDataSource.deleteAll()

    override var alarmSetting: Boolean
        get() = settingLocalDataSource.alarmSetting
        set(value) {
            settingLocalDataSource.alarmSetting = value
        }
    override var businessCode: String
        get() = settingLocalDataSource.businessCode
        set(value) {
            settingLocalDataSource.businessCode = value
        }
}