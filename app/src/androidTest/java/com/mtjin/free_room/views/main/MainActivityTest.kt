package com.mtjin.free_room.views.main

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.utils.BUSINESS_CODE
import com.mtjin.free_room.utils.MEETING_ROOM
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {
    @get:Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // context
    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Firebase.initialize(appContext)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun insertMeetingRoom() {
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("1")
            .setValue(
                MeetingRoom(
                    1, "기획팀",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/room_picture.png?alt=media&token=4a759405-6d05-4a63-ab71-cd3bca6a77b7",
                    num = 8, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("2")
            .setValue(
                MeetingRoom(
                    2, "디자인",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202020-08-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.20.55.png?alt=media&token=69c935a1-9158-41ac-9e18-46158390311d",
                    num = 5, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("3")
            .setValue(
                MeetingRoom(
                    3, "IOS",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/room_picture.png?alt=media&token=4a759405-6d05-4a63-ab71-cd3bca6a77b7",
                    num = 20, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("4")
            .setValue(
                MeetingRoom(
                    4, "딥러닝",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202020-08-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.20.55.png?alt=media&token=69c935a1-9158-41ac-9e18-46158390311d",
                    num = 5, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("5")
            .setValue(
                MeetingRoom(
                    5, "백엔드",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/room_picture.png?alt=media&token=4a759405-6d05-4a63-ab71-cd3bca6a77b7",
                    num = 20, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("6")
            .setValue(
                MeetingRoom(
                    6, "안드로이드",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202020-08-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.20.55.png?alt=media&token=69c935a1-9158-41ac-9e18-46158390311d",
                    num = 3, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("7")
            .setValue(
                MeetingRoom(
                    7, "프론트",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/room_picture.png?alt=media&token=4a759405-6d05-4a63-ab71-cd3bca6a77b7",
                    num = 12, isDisabled = false
                )
            )
        Firebase.database.reference.child(MEETING_ROOM).child(BUSINESS_CODE).child("7")
            .setValue(
                MeetingRoom(
                    8, "플러터",
                    "https://firebasestorage.googleapis.com/v0/b/worksmettingroomreservation.appspot.com/o/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202020-08-10%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%2012.20.55.png?alt=media&token=69c935a1-9158-41ac-9e18-46158390311d",
                    num = 2, isDisabled = false
                )
            )
    }
}