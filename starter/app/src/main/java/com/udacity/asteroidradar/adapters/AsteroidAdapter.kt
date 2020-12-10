package com.udacity.asteroidradar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R

class AsteroidAdapter: RecyclerView.Adapter<AsteroidViewHolder>() {
    var mData =  listOf<Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = mData[position]
        holder.tvName.text = item.codename
        holder.tvDate.text = item.closeApproachDate
        holder.ivImage.setImageResource(if(item.isPotentiallyHazardous) R.drawable.ic_status_potentially_hazardous else R.drawable.ic_status_normal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_asteroid, parent, false)

        return AsteroidViewHolder(view)
    }
}


/**
 * ViewHolder for asteroid items. All work is done by data binding.
 */
class AsteroidViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tv_code_name)
    val tvDate: TextView = itemView.findViewById(R.id.tv_close_date)
    val ivImage: ImageView = itemView.findViewById(R.id.iv_status)
}
