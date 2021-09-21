package com.mtjin.free_room.data.login.source.remote

import com.mtjin.free_room.model.User
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.USER
import com.mtjin.free_room.utils.fcmToken
import com.mtjin.free_room.utils.uuid
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(private val database: DatabaseReference) :
    LoginRemoteDataSource {
    override fun googleLogin(): Completable {
        return Completable.create { emitter ->
            database.child(USER).child(BUSINESS_CODE)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (!snapshot.hasChild(uuid)) {
                            database.child(USER).child(BUSINESS_CODE).child(uuid)
                                .setValue(User(id = uuid, fcm = fcmToken, name = uuid, pw = uuid))
                                .addOnSuccessListener {
                                    emitter.onComplete()
                                }.addOnFailureListener {
                                    emitter.onError(it)
                                }
                        } else {
                            emitter.onComplete()
                        }
                    }

                })
        }
    }
}