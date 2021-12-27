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
    var mapOfChannelsView: MutableMap<Int, View> =
        mutableMapOf<Int, View>()
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
        onClickBuilder(holder.itemView.findViewById<LinearLayout>(R.id.layoutItem), position)
        mapOfChannelsView[position] = holder.itemView
        functionrnd(4)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    open fun onClickBuilder(layout: LinearLayout, index: Int) {}


    open fun functionrnd(int: Int){
        Log.e("TAG",int.toString())
    }
}