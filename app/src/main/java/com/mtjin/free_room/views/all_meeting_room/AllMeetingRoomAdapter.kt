package com.mtjin.free_room.views.all_meeting_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mtjin.free_room.R
import com.mtjin.free_room.model.MeetingRoom
import com.mtjin.free_room.databinding.ItemMeetingRoomBinding

class AllMeetingRoomAdapter(
    private val detailClick: (MeetingRoom) -> Unit,
    private val reservationClick: (MeetingRoom) -> Unit
) :
    RecyclerView.Adapter<AllMeetingRoomAdapter.ViewHolder>() {
    private val items: ArrayList<MeetingRoom> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMeetingRoomBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_meeting_room,
            parent,
            false
        )
        val viewHolder = ViewHolder(binding)
        binding.btnDetail.setOnClickListener {
            detailClick(items[viewHolder.adapterPosition])
        }
        binding.btnReserve.setOnClickListener {
            reservationClick(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {
            holder.bind(it)
        }
    }

    class ViewHolder(private val binding: ItemMeetingRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meetingRoom: MeetingRoom) {
            binding.item = meetingRoom
            binding.executePendingBindings()
        }
    }

    fun addItems(items: List<MeetingRoom>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: MeetingRoom) {
        this.items.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }
}