package com.mtjin.free_room.views.my_meeting_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mtjin.free_room.R
import com.mtjin.free_room.model.MyMeetingRoom
import com.mtjin.free_room.databinding.ItemMyMeetingRoomBinding

class MyMeetingRoomAdapter(
    private val detailClick: (MyMeetingRoom) -> Unit,
    private val reservationClick: (MyMeetingRoom) -> Unit
) :
    RecyclerView.Adapter<MyMeetingRoomAdapter.ViewHolder>() {
    private val items: ArrayList<MyMeetingRoom> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemMyMeetingRoomBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_my_meeting_room,
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

    class ViewHolder(private val binding: ItemMyMeetingRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myMeetingRoom: MyMeetingRoom) {
            binding.item = myMeetingRoom
            binding.executePendingBindings()
        }
    }

    fun addItems(items: List<MyMeetingRoom>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: MyMeetingRoom) {
        this.items.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }
}