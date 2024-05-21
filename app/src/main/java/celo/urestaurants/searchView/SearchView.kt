package celo.urestaurants.searchView

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import celo.urestaurants.R

class SearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var openSearchButton: View
    private var closeSearchButton: View
    var searchOpenView: View
    var searchInputText: TextView
    var txtDashTitle: TextView

    var onOpenSearchClicked: (() -> Unit)? = null
    var onCloseSearchClicked: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        searchOpenView = findViewById(R.id.search_open_view)
        searchInputText = findViewById(R.id.search_input_text)
        openSearchButton = findViewById(R.id.open_search_button)
        closeSearchButton = findViewById(R.id.close_search_button)
        txtDashTitle = findViewById(R.id.txt_dash_title)

        openSearchButton.setOnClickListener {
            openSearch()
            onOpenSearchClicked?.invoke()
        }
        closeSearchButton.setOnClickListener {
            closeSearch()
            onCloseSearchClicked?.invoke()
        }
    }

    private fun openSearch() {
        searchInputText.text = ""
        searchOpenView.visibility = View.VISIBLE
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            searchOpenView,
            (openSearchButton.right + openSearchButton.left) / 2,
            (openSearchButton.top + openSearchButton.bottom) / 2,
            0f, width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()
    }

    private fun closeSearch() {
        val circularConceal = ViewAnimationUtils.createCircularReveal(
            searchOpenView,
            (openSearchButton.right + openSearchButton.left) / 2,
            (openSearchButton.top + openSearchButton.bottom) / 2,
            width.toFloat(), 0f
        )

        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) = Unit
            override fun onAnimationCancel(animation: Animator) = Unit
            override fun onAnimationStart(animation: Animator) = Unit
            override fun onAnimationEnd(animation: Animator) {
                searchOpenView.visibility = View.INVISIBLE
                searchInputText.text = ""
                circularConceal.removeAllListeners()
            }
        })
    }

}