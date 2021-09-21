@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.mtjin.free_room.utils

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// 날짜만 타임스탬프 변환 2020-01-01 - timestamp
fun String.convertDateToTimestamp(): Long =
    SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(this).time

fun Long.convertTimestampToDate(): String = DateFormat.format("yyyy-MM-dd", this).toString()


// 날짜,시간,분 포함된 타임스탬프 변환 2020-01-01-22-30 - timestamp
fun String.convertDateFullToTimestamp(): Long =
    SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.KOREA).parse(this).time

fun Long.convertTimestampToDateFull(): String =
    DateFormat.format("yyyy-MM-dd-HH:mm", this).toString()

fun Long.convertCurrentTimestampToDateTimestamp(): Long =
    this.convertTimestampToDate().convertDateToTimestamp()

// timestamp -> 13:40
fun Long.convertTimestampToTime(): String = DateFormat.format("HH:mm", this).toString()

//fun Long.convertTimesToTimestamp(): Long = DateFormat.format("HH:mm", this).toString()

// 시간 한자리면 앞에 0 붙여주어 변환
fun String.convertHourDoubleDigit(): String = if (this.length < 2) "0$this" else this

// 분 한자리면 앞에 0 붙여주어 반환
fun String.convertMinuteDoubleDigit(): String = if (this.length < 2) "0$this" else this

fun Long.convertTimestampToHour(): Int = DateFormat.format("HH", this).toString().toInt()

fun Long.convertTimestampToMinute(): Int = DateFormat.format("mm", this).toString().toInt()

fun Int.convertNextHour(): Int = if (this == 23) 0 else this + 1

fun Int.convertNextMinute(): Int = if (this == 59) 0 else this + 1

// 1시간 뒤 타임스탬프
fun Long.convertNextHourTimestamp(): Long = this + (60 * 60 * 1000)

// FCM 메시지로 사용
fun convertTimeToFcmMessage(date: Long, startTime: Long): String =
    date.convertTimestampToDate() + " " + startTime.convertTimestampToTime() + "에 회의실 예약이 있습니다."

fun combineTimestamp(x: Long, y: Long) = (x.toString() + y.toString()).toLong()

// 랜덤키값
fun getRandomKey(): Long = Random(System.currentTimeMillis()).nextLong(100000, 999999)

// 타임스탬프
fun getTimestamp(): Long = System.currentTimeMillis()