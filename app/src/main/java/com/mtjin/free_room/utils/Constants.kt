package com.mtjin.free_room.utils

/*
* Firebase Database
* */

// root data
const val MEETING_ROOM = "meeting_room" //회의실
const val RESERVATION = "reservation" // 회의실에약
const val USER = "user" // 유저프로필

// etc data
const val DATE_TIMESTAMP = "dateTimestamp" // 시간분초를 제와한 날짜 스탬프
const val ID = "id" // id
const val NAME = "name" // User name
const val PW = "pw" // User pw
const val FCM = "fcm" // fcm

// 변할 수 있는 회사코드
var BUSINESS_CODE = "BUSINESS_CODE_EXAMPLE" // 예시

// 유저 고유 토큰
var uuid = ""

// FCM 토큰
var fcmToken: String = ""


/*
* Request Code
* */

const val RC_GOOGLE_LOGIN = 101
const val EXTRA_NOTIFICATION_TITLE = "EXTRA_NOTIFICATION_TITLE"
const val EXTRA_NOTIFICATION_MESSAGE = "EXTRA_NOTIFICATION_MESSAGE"

