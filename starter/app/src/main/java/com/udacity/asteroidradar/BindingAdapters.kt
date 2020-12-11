package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.data.AsteroidsApiStatus

@BindingAdapter("statusIcon")
fun ImageView.bindAsteroidStatusImage(isHazardous: Boolean) {
    if (isHazardous) {
        setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun ImageView.bindDetailsStatusImage(isHazardous: Boolean) {
    if (isHazardous) {
        setImageResource(R.drawable.asteroid_hazardous)
    } else {
        setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun TextView.bindTextViewToAstronomicalUnit(number: Double) {
    val context = this.context
    text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun TextView.bindTextViewToKmUnit(number: Double) {
    val context = this.context
    text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun TextView.bindTextViewToDisplayVelocity(number: Double) {
    val context = this.context
    text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("asteroidsApiStatus")
fun ProgressBar.bindStatus(status: AsteroidsApiStatus?){
   status?.let {
       visibility = when(status){
           AsteroidsApiStatus.LOADING -> View.VISIBLE
           AsteroidsApiStatus.ERROR -> View.GONE
           AsteroidsApiStatus.DONE -> View.GONE
       }
   }
}

@BindingAdapter("pictureOfTheDay")
fun ImageView.bindPictureOfTheDay(pictureOfDay: PictureOfDay?) {
   pictureOfDay?.let {
       if(it.mediaType.equals("video",true)) {
           visibility = View.GONE
       }else{
           visibility = View.VISIBLE
           Picasso.with(context).load(it.url).into(this)
       }
   }
}

@BindingAdapter("videoOfTheDay")
fun VideoView.bindVideoOfTheDay(pictureOfDay: PictureOfDay?){
    pictureOfDay?.let {
        if(it.mediaType.equals("video",true)) {
           visibility = View.VISIBLE
            setVideoPath(it.url)
            start()
        }else{
            visibility = View.GONE
        }
    }
}
