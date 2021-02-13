package com.example.socialapp.readapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.models.Bleep
import com.example.socialapp.readapters.BleepsAdapter.BleepViewHolder
import com.mikhaellopez.circularimageview.CircularImageView

class BleepsAdapter(private val bleepList: List<Bleep>, private val context: Context, private val bleepClickListener: OnBleepClickListener) : RecyclerView.Adapter<BleepViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleepViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.bleep_row, parent, false)
        return BleepViewHolder(v, bleepClickListener)
    }

    override fun onBindViewHolder(holder: BleepViewHolder, position: Int) {
        val bleep = bleepList[position]
        Glide.with(context).load(bleep.user?.image).into(holder.bleepPicView)
        holder.bleepNickView.text = bleep.user?.nick
        holder.bleepTimeView.text = Bleep.timeStringFromMillis(bleep.timeMillis)
        holder.bleepContentView.text = bleep.content
    }

    override fun getItemCount(): Int {
        return bleepList.size
    }

    class BleepViewHolder(itemView: View, private val bleepClickListener: OnBleepClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val bleepPicView: CircularImageView = itemView.findViewById(R.id.bleepDetailsPic)
        val bleepNickView: TextView = itemView.findViewById(R.id.bleepNickTextView)
        val bleepTimeView: TextView = itemView.findViewById(R.id.bleepTimeTextView)
        val bleepContentView: TextView = itemView.findViewById(R.id.bleepContentTextView)

        override fun onClick(v: View) {
            bleepClickListener.onBleepClick(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnBleepClickListener {
        fun onBleepClick(position: Int)
    }
}