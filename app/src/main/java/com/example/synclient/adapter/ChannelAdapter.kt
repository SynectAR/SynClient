package com.example.synclient.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synclient.R
import com.example.synclient.entities.ItemChannel


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
        /* if(dataList[position].isActive)
             holder.itemView.findViewById<LinearLayout>(R.id.layoutItem).setBackgroundColor(Color.parseColor("#00CCF3B7"))
         else
             holder.itemView.findViewById<LinearLayout>(R.id.layoutItem).setBackgroundColor(Color.parseColor("#00CCF3B7"))*/
        onClickBuilder(holder.itemView.findViewById<LinearLayout>(R.id.layoutItem), position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    open fun onClickBuilder(layout: LinearLayout, index: Int) {
    }


}