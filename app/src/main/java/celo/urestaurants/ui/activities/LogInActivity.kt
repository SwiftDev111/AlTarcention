package celo.urestaurants.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import celo.urestaurants.R
import celo.urestaurants.databinding.ActivityLogInBinding
import celo.urestaurants.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber

class LogInActivity : AppCompatActivity() {
    private var binding: ActivityLogInBinding? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = FirebaseAuth.getInstance()
        binding!!.SignInButton.setOnClickListener { v: View? ->
            Timber.d( "Google Sign In")
            val intent = googleSignInClient!!.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception) {
                Timber.d( "Activyt Result" + e.message)
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        Timber.d( "Begin Firebase auth")
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnSuccessListener { authResult: AuthResult ->
                Timber.d( "Succes, Log in ")
                val firebaseUser = firebaseAuth!!.currentUser

                // User Info
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email
                val nome = firebaseUser.displayName
                Timber.d( "UUID CODE : $uid")
                Timber.d( "Email $email")
                Timber.d( "Nome$nome")
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Timber.d( "Account created ")
                    Toast.makeText(
                        this@LogInActivity,
                        "Account creato con successo $nome",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Timber.d( "Account già presente  ")
                    Toast.makeText(
                        this@LogInActivity,
                        "Log in Avvenuto con successo $nome",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@LogInActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e: Exception -> Timber.d( "Failure LOG IN " + e.message) }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}