package com.mtjin.free_room.views.splash

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.ktx.Firebase
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseFragment
import com.mtjin.free_room.databinding.FragmentSplashBinding
import com.mtjin.free_room.utils.*
import com.mtjin.free_room.views.main.MainActivity
import timber.log.Timber


class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {


    override fun init() {
        val auth = FirebaseAuth.getInstance()
        getFcmToken()
        if (auth.currentUser != null) { //로그인 되어있을시 메인화면으로
            uuid = auth.currentUser?.uid.toString()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                initNavigation()
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToBottomNav1())
            }, 2000)
        } else {
            // 로그인 안되어있을 시 로그인 화면으로
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }, 2000)
        }
    }

    private fun initNavigation() {
        findNavController().graph.startDestination = R.id.bottom_nav_1
        (activity as MainActivity).initNavigation()
    }

    private fun getFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult?> {
                override fun onComplete(task: Task<InstanceIdResult?>) {
                    if (!task.isSuccessful) {
                        Timber.d("getFcmToken() Fail")
                        return
                    }
                    // Get new Instance ID token
                    task.result?.let {
                        Timber.d("FcmToken : %s", it.token)
                        fcmToken = it.token
                        updateFcmToken()
                    }
                }
            })
    }

    private fun updateFcmToken() { //스플래쉬는 편의상 mvvm으로 안함
        val database = Firebase.database.reference
        FirebaseAuth.getInstance().currentUser?.let {
            database.child(USER).child(BUSINESS_CODE).child(uuid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Timber.d("Error => SplashFragment updateFcmToken Error %s", error)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val map = HashMap<String, Any>()
                        map[FCM] = fcmToken
                        database.child(USER).child(BUSINESS_CODE).child(uuid)
                            .updateChildren(map)
                    }
                })
        }
    }

}