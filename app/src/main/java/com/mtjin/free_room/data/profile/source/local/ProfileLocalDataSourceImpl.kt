package com.mtjin.free_room.data.profile.source.local

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import com.mtjin.free_room.model.User
import com.mtjin.free_room.utils.getTimestamp
import io.reactivex.Completable
import io.reactivex.Single
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class ProfileLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao,
    private val context: Context
) : ProfileLocalDataSource {
    override fun insertUser(user: User): Completable = userDao.insertUser(user)

    override fun getUser(id: String): Single<User> = userDao.getUser(id)

    override fun deleteUser(user: User): Completable = userDao.deleteUser(user)

    override fun saveQrCodeFile(bitmap: Bitmap): Completable {
        return Completable.create { emitter ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.applicationContext.contentResolver
                val pictureCollection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val pictureDetails = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "WORKS_QR_" + getTimestamp())
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }
                val pictureContentUri = resolver.insert(pictureCollection, pictureDetails)!!
                resolver.openFileDescriptor(pictureContentUri, "w", null).use { pfd ->
                    try {
                        pfd?.let {
                            val fos = FileOutputStream(it.fileDescriptor)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                            fos.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        emitter.onError(e)
                    }
                }
                pictureDetails.clear()
                pictureDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(pictureContentUri, pictureDetails, null, null)
                emitter.onComplete()
            } else {
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap,
                    "WORKS_QR_" + getTimestamp(),
                    "WORKS_QR_CODE"
                )
                emitter.onComplete()
            }
        }
    }
}
