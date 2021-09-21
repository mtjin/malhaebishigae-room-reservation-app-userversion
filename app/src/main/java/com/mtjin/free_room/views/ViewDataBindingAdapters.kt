@file:Suppress("UNCHECKED_CAST")

package com.mtjin.free_room.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mtjin.free_room.R
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.utils.convertTimestampToDate
import com.mtjin.free_room.utils.convertTimestampToTime
import com.mtjin.free_room.utils.getTimestamp
import com.mtjin.free_room.views.all_meeting_room.AllMeetingRoomAdapter
import com.mtjin.free_room.views.my_meeting_room.MyMeetingRoomAdapter
import com.mtjin.free_room.views.reservation.ReservationAdapter
import com.mtjin.free_room.views.reservation_detail.ReservationDetailAdapter
import com.skydoves.elasticviews.ElasticButton

@BindingAdapter("urlImage")
fun ImageView.setUrlImage(url: String) {
    Glide.with(this).load(url)
        .placeholder(R.drawable.ic_default_room)
        .into(this)
}

@BindingAdapter("setItems")
fun RecyclerView.setAdapterItems(items: List<Any>?) {
    when (adapter) {
        is AllMeetingRoomAdapter -> {
            items?.let {
                with(adapter as AllMeetingRoomAdapter) {
                    clear()
                    addItems(it as List<MeetingRoom>)
                }
            }
        }
        is ReservationAdapter -> {
            items?.let {
                with(adapter as ReservationAdapter) {
                    clear()
                    addItems(it as List<Reservation>)
                }
            }
        }
        is ReservationDetailAdapter -> {
            items?.let {
                with(adapter as ReservationDetailAdapter) {
                    clear()
                    addItems(it as List<Reservation>)
                }
            }
        }
        is MyMeetingRoomAdapter -> {
            items?.let {
                with(adapter as MyMeetingRoomAdapter) {
                    clear()
                    addItems(it as List<MyMeetingRoom>)
                }
            }
        }
    }
}

// 프로필 수정 버튼
@BindingAdapter("reviseState")
fun ElasticButton.reviseState(state: Boolean) {
    text = if (state) "수정완료" else "수정하기"
}

// timestamp -> 12:00
@BindingAdapter("timestampTime")
fun TextView.timestampTime(timestamp: Long) {
    text = timestamp.convertTimestampToTime()
}

// timestamp -> 11~20
@SuppressLint("SetTextI18n")
@BindingAdapter("startTimestamp", "endTimestamp")
fun TextView.timestampTerm(startTimestamp: Long, endTimestamp: Long) {
    text = startTimestamp.convertTimestampToTime() + "\n~\n" + endTimestamp.convertTimestampToTime()
}

// timestamp -> 11~20
@SuppressLint("SetTextI18n")
@BindingAdapter("startTimestampSingle", "endTimestampSingle")
fun TextView.timestampTermSingleLine(startTimestamp: Long, endTimestamp: Long) {
    text = startTimestamp.convertTimestampToTime() + "~" + endTimestamp.convertTimestampToTime()
}

// timestamp -> 11~20
@BindingAdapter("timestampDate")
fun TextView.timestampDate(timestamp: Long) {
    text = timestamp.convertTimestampToDate()
}

// 현재 이용중인 방인지에 따라 다른 텍스트와 배경색
@BindingAdapter("roomInUseStartTimestamp", "roomInUseEndTimestamp")
fun TextView.roomInUse(startTimestamp: Long, endTimestamp: Long) {
    if (getTimestamp() in startTimestamp until endTimestamp) {
        text = context.getString(R.string.proceeding_text)
        setBackgroundColor(Color.parseColor("#00B0FF"))
    } else {
        text = context.getString(R.string.reservation_text)
        setBackgroundColor(Color.parseColor("#2196F3"))
    }
}
