package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.adapters.AsteroidAdapter
import com.udacity.asteroidradar.data.AsteroidsApiStatus
import com.udacity.asteroidradar.data.PotDApiStatus
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay

@BindingAdapter("statusIcon")
fun ImageView.bindAsteroidStatusImage(isHazardous: Boolean) {
    if (isHazardous) {
        setImageResource(R.drawable.ic_status_potentially_hazardous)
        contentDescription =context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        setImageResource(R.drawable.ic_status_normal)
        contentDescription =context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("asteroidStatusImage")
fun ImageView.bindDetailsStatusImage(isHazardous: Boolean) {
    if (isHazardous) {
        setImageResource(R.drawable.asteroid_hazardous)
        contentDescription =context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        setImageResource(R.drawable.asteroid_safe)
        contentDescription =context.getString(R.string.not_hazardous_asteroid_image)
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

//@BindingAdapter("asteroidsApiStatusVisibility")
//fun RecyclerView.bindStatus(status: AsteroidsApiStatus?){
//    status?.let {
//        visibility = when(status){
//            AsteroidsApiStatus.LOADING -> View.GONE
//            AsteroidsApiStatus.ERROR -> View.GONE
//            AsteroidsApiStatus.DONE -> View.VISIBLE
//        }
//    }
//}

@BindingAdapter("asteroidsAdapter")
fun RecyclerView.bindList(asteroids: List<Asteroid>?){
    asteroids?.let {
        visibility = View.VISIBLE
        (adapter as AsteroidAdapter).submitList(asteroids)
    }
}

@BindingAdapter("pictureOfTheDay")
fun ImageView.bindPictureOfTheDay(pictureOfDay: PictureOfDay?) {
   pictureOfDay?.let {
       if(it.mediaType.equals("video",true)) {
           visibility = View.GONE
       }else{
           visibility = View.VISIBLE
           Picasso.with(context).load(it.url).error(R.drawable.ic_broken_image).into(this)
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

/**
 * This binding adapter displays the [PictureApiStatus] of the network request in an image view.  When
 * the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("pictureApiStatus")
fun ImageView.bindStatus(status: PotDApiStatus?) {
    when (status) {
        PotDApiStatus.LOADING -> {
            visibility = View.VISIBLE
            setImageResource(R.drawable.loading_animation)
        }
        PotDApiStatus.ERROR -> {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_connection_error)
        }
        PotDApiStatus.DONE -> {
           visibility = View.GONE
        }
    }
}
