package com.mtjin.free_room.data.profile.source.remote

import com.mtjin.free_room.model.User
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.NAME
import com.mtjin.free_room.utils.PW
import com.mtjin.free_room.utils.USER
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(private val database: DatabaseReference) :
    ProfileRemoteDataSource {
    override fun requestProfile(id: String): Single<User> {
        return Single.create<User> { emitter ->
            database.child(USER).child(BUSINESS_CODE).child(id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue(User::class.java)?.let {
                            emitter.onSuccess(it)
                        }
                    }
                })
        }
    }

    override fun changeProfile(user: User): Completable {
        return Completable.create { emitter ->
            database.child(USER).child(BUSINESS_CODE)
                .orderByChild(NAME)
                .equalTo(user.name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && !snapshot.hasChild(user.id)) {
                            emitter.onError(Throwable("중복아이디"))
                        } else {
                            val map = HashMap<String, Any>()
                            map[NAME] = user.name
                            map[PW] = user.pw
                            database.child(USER).child(BUSINESS_CODE).child(user.id)
                                .updateChildren(map).addOnSuccessListener {
                                    emitter.onComplete()
                                }.addOnFailureListener {
                                    emitter.onError(it)
                                }
                        }
                    }
                })
        }
    }
}