package com.mtjin.free_room.data.setting.source.local

import com.mtjin.free_room.data.database.RoomDatabase
import com.mtjin.free_room.utils.PreferenceManager
import io.reactivex.Completable
import javax.inject.Inject

class SettingLocalDataSourceImpl @Inject constructor(
    private val roomDatabase: RoomDatabase,
    private val preferenceManager: PreferenceManager
) :
    SettingLocalDataSource {
    override fun deleteAll(): Completable {
        return Completable.create { emitter ->
            roomDatabase.clearAllTables()
            emitter.onComplete()
        }
    }

    override var alarmSetting: Boolean
        get() = preferenceManager.alarmSetting
        set(value) {
            preferenceManager.alarmSetting = value
        }

    override var businessCode: String
        get() = preferenceManager.businessCode
        set(value) {
            preferenceManager.businessCode = value
        }
}