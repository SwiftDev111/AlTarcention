package celo.urestaurants.ui.ordinazioni

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface GoogleSignInBottomSheetListener {
    fun onSignInSuccessful(account: GoogleSignInAccount)
}