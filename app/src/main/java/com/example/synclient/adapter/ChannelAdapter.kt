package com.example.synclient.adapter


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synclient.R
import com.example.synclient.entities.ItemChannel
import com.example.synclient.entities.PortCalibrationStatus


open class ChannelAdapter : RecyclerView.Adapter<ChannelAdapter.ItemChannelAdapter>() {
    var dataList = ArrayList<ItemChannel>()
    lateinit var context: Context
    fun setData(data: List<ItemChannel>) {
        dataList = data as ArrayList<ItemChannel>
    }

    class ItemChannelAdapter(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemChannelAdapter {
        return ItemChannelAdapter(
            LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemChannelAdapter, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.channel_name).text = dataList[position].name
        holder.itemView.setOnClickListener {
            onClickBuilder(it, position)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    open fun onClickBuilder(view: View, index: Int) {}


    fun functionrnd(int: Int){
        Log.e("TAG",int.toString())
    }
}