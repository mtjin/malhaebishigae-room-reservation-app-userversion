package com.mtjin.free_room.views.reservation_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mtjin.free_room.R
import com.mtjin.free_room.model.Reservation
import com.mtjin.free_room.databinding.ItemReservationDetailBinding

class ReservationDetailAdapter :
    RecyclerView.Adapter<ReservationDetailAdapter.ViewHolder>() {
    private val items: ArrayList<Reservation> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemReservationDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_reservation_detail,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let {
            holder.bind(it)
        }
    }

    class ViewHolder(private val binding: ItemReservationDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation) {
            binding.item = reservation
            binding.executePendingBindings()
        }
    }

    fun addItems(items: List<Reservation>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItem(item: Reservation) {
        this.items.add(item)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }
}