package celo.urestaurants

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object Utils {

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val view = activity.currentFocus

        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}