package com.udacity.asteroidradar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.ListItemAsteroidBinding

class AsteroidAdapter: RecyclerView.Adapter<AsteroidViewHolder>() {
    var mData =  listOf<Asteroid>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = mData.size

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = mData[position]
        holder.name.text = item.toString()
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
    val name: TextView = itemView.findViewById(R.id.name)
}
