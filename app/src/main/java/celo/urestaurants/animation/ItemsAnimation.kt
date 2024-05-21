package celo.urestaurants.animation

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

object ItemsAnimation {

    fun addEnterAnimation(recycler: RecyclerView, context: Context) {
        val animationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_slide_from_right)
        recycler.layoutAnimation = animationController

        recycler.scheduleLayoutAnimation()
    }
    fun setFilterVisibilityWithAnimation(
        recyclerView: RecyclerView,
        textView: TextView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            recyclerView.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE

            recyclerView.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(160)
                .start()

            textView.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(160)
                .start()
        } else {

            recyclerView.scaleY = 0.6f
            textView.scaleY = 0.6f

            recyclerView.animate()
                .alpha(0f)
                .scaleY(0.8f)
                .setDuration(250)
                .withEndAction {
                    recyclerView.visibility = View.GONE
                }
                .start()

            textView.animate()
                .alpha(0f)
                .scaleY(0.8f)
                .setDuration(250)
                .withEndAction {
                    textView.visibility = View.GONE
                }
                .start()
        }
    }
}