package com.mtjin.free_room.views.main

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mtjin.free_room.R
import com.mtjin.free_room.base.BaseActivity
import com.mtjin.free_room.databinding.ActivityMainBinding
import com.mtjin.free_room.di.MyApplication
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.PreferenceManager
import timber.log.Timber


class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    lateinit var mainComponent: MainComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent = (application as MyApplication).appComponent.mainComponent().create()
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)
        initTimber()
        initBusinessCode()
    }

    private fun initBusinessCode() {
        val preferenceManager = PreferenceManager(this)
        BUSINESS_CODE = preferenceManager.businessCode
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

    fun initNavigation() {
        val navController = findNavController(R.id.main_nav_host)
        binding.mainBottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.bottom_nav_1 || destination.id == R.id.bottom_nav_2 || destination.id == R.id.bottom_nav_3 || destination.id == R.id.bottom_nav_4) {
                binding.mainBottomNavigation.visibility = View.VISIBLE
            } else {
                binding.mainBottomNavigation.visibility = View.GONE
            }
        }
    }
}