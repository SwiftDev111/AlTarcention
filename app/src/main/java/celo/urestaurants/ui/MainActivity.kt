package celo.urestaurants.ui

//import kotlinx.coroutines.internal.ClassValueCtorCache.cache

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import celo.urestaurants.BuildConfig
import celo.urestaurants.MainTitleNavigationBar
import celo.urestaurants.R
import celo.urestaurants.constants.Constants
import celo.urestaurants.databinding.ActivityMainBinding
import celo.urestaurants.models.InsightModel
import celo.urestaurants.ui.dashboard.DashboardFragment
import celo.urestaurants.ui.home.HomeFragment
import celo.urestaurants.ui.informations.InformationFragment
import celo.urestaurants.ui.ordinazioni.OrdersFragment
import com.google.android.gms.instantapps.InstantApps
import com.google.android.gms.instantapps.InstantApps.getActivityCompat
import com.google.android.gms.instantapps.InstantApps.getLauncher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Dash
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainTitleNavigationBar {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isFirst: Boolean = true
    private var URLisFirst: Boolean = true
    private var uri: Uri? = null
    private var REQUEST_CODE_INSTANT_APP_INSTALL: Int = 123
    private var param = ""
    private var updatedTitle = ""
    private var Location_LT_LNG = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return // Check for null
            for (location in p0.locations) {
                // Handle location updates
                val latitude = location.latitude
                val longitude = location.longitude

                Location_LT_LNG = Pair(latitude, longitude).toString()
                Constants.LT_LNG = Location_LT_LNG

                Constants.currentLAT = latitude.toString()
                Constants.currentLONG = longitude.toString()
                Log.d("lt_lng", "onLocationResult: "+latitude +"::"+longitude)
                // Do something with latitude and longitude
            }
        }
    }

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //getActivityCompat(this)
        //getLauncher(applicationContext)
        //showInstallPrompt()

        uri = intent.data

        sharedPref = getSharedPreferences(Constants.prefName, MODE_PRIVATE)
        editor = sharedPref.edit()

        try {
            val mainTitle = sharedPref.getString(Constants.PREF_KEY_MAIN_TITLE, "") ?: ""
            binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                mainTitle.ifEmpty { "Home" }
        } catch (e: Exception) {
            Timber.d("Errorrrrr : ${e.cause}")
        }

        isFirst = sharedPref.getBoolean(Constants.PREF_KEY_IS_FIRST, true)

        binding.navView.setOnItemSelectedListener { item ->
            handleNavigationItemSelection(item)
        }

        //Log.d("URLFirst", "onCreate: OUTTT")
        when (viewModel.selectedTab) {
            0 -> {
                goToDashBoard()
            }

            1 -> {
                goToMenu()
            }

            2 -> {
                goToReservation()
            }

            3 -> {
                goToInfo()
            }
        }


        if (URLisFirst) {
            if (uri != null) {
                //Log.d("URLFirst", "onCreate: INNNNN")
                val parameters = uri!!.pathSegments
                try {
                    param = parameters[parameters.size - 1]
                }catch (e:Exception){

                }
                Constants.m_PlaceID = param
                Log.d("PLACE_ID", "MAin: "+Constants.m_PlaceID)
                binding.navView.setSelectedItemId(R.id.navigation_home)
                setTitleOfNav()
                goToMenu()

                URLisFirst = false

                sharedPref = getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE); // getActivity() for Fragment
                sharedPref.edit().putBoolean("locked", URLisFirst).apply()

                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Home"

                //val sharedPref = getSharedPreferences(PREF_KEY_PARAM_KEY, Context.MODE_PRIVATE)
                //sharedPref.edit().putString("TIMEOUT", URLisFirst.toString()).apply()
                //Log.d("sharedPref_Boolean", "MAIN: "+URLisFirst.toString())
                //param = ""

                uri = null
            }
        }




        if (isFirst) {
            //binding.navView.visibility = View.GONE

            if (param.isNotEmpty()) {
                binding.navView.menu.findItem(R.id.navigation_dashboard).isVisible = false
                binding.navView.menu.findItem(R.id.navigation_orders).isVisible = false
                binding.navView.menu.findItem(R.id.navigation_home).isVisible = true
                binding.navView.menu.findItem(R.id.navigation_notifications).isVisible = true
                binding.navView.visibility = View.VISIBLE
            } else {
                binding.navView.visibility = View.GONE
            }
        }

        viewModel.getInsight()

        viewModel.insightModelMutableLiveData.observe(this) { insightModel ->
            insightModel?.let { _ ->

                val sharedPrefer = getSharedPreferences(Constants.GOOGLE_SIGN_IN, MODE_PRIVATE)
                val email = sharedPrefer.getString(Constants.GOOGLE_MAIL, "")
                val name = sharedPrefer.getString(Constants.GOOGLE_NAME, "")
                val surname = sharedPrefer.getString(Constants.GOOGLE_FAMILY_NAME, "")

                val nodeId = sharedPref.getString(Constants.FIREBASE_NODE_Auth, "")
                val authUUID = sharedPrefer.getString(Constants.GOOGLE_ACCOUNT_UID, "")

                val insightAuth = surname?.let {
                    name?.let {
                        email?.let {
                            authUUID?.let { uuid ->
                                InsightModel.InsightAuth(
                                    surname = surname,
                                    name = name,
                                    email = email,
                                    uuid = uuid
                                )
                            }
                        }
                    }
                }

                if (authUUID != null) {
                    insightAuth?.id = nodeId.toString()
                }

                if (email.isNullOrEmpty() || email.isBlank()) {
                    val insightAPp = getDeviceInfo()
                    viewModel.updateRecordInsightApp(nodeId, insightAPp)
                } else {
                    viewModel.updateRecordInsightAuth(authUUID, insightAuth)
                }

            }
        }

        viewModel.storeNodeId.observe(this) { newId ->
            newId?.let { id ->
                editor.putString(Constants.FIREBASE_NODE_APP, id)
                editor.apply()
            }
        }
        viewModel.storeAuthNodeId.observe(this) { newId ->
            newId?.let { id ->
                editor.putString(Constants.FIREBASE_NODE_Auth, id)
                editor.apply()
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction = intent?.action
        val appLinkData: Uri? = intent?.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { recipeId ->
                Uri.parse("content://urestaurants.altervista.org")
                    .buildUpon()
                    .appendPath(recipeId)
                    .build().also { appData ->
                        val bundle = Bundle().apply {
                            putString("IdPlace", recipeId)
//                            putString("UpdatedTitle", updatedTitle)
                        }

                        val homeFragment = HomeFragment().apply {
                            arguments = bundle  // Set the bundle as arguments for the fragment
                        }
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                            .commit()
                    }
            }
        }
    }

    private fun setTitleOfNav() {
        if (param.isNotEmpty()) {
            if (param == "01") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Al Tarcentino"
            } else if (param == "02") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Pronto Pizza G. Bassa"
            } else if (param == "03") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Albergo Riviera"
            } else if (param == "04") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Pronto Pizza G. Alta"
            } else if (param == "05") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Helios Pizza"
            } else if (param == "06") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Da Turi"
            } else if (param == "07") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Pizzeria Capriccio"
            } else if (param == "08") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Lendar"
            } else if (param == "09") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Tellme Pizza"
            } else if (param == "010") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Sofias's Bakery"
            } else if (param == "011") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Caffe Pittini"
            } else if (param == "012") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Tellme Pizza"
            } else if (param == "013") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Pizze e Delizie"
            } else if (param == "014") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Ostaria Boccadoro"
            } else if (param == "015") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Antico Gatoleto"
            } else if (param == "016") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Osteria Da Alberto"
            } else if (param == "017") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Osteria Al Ponte"
            } else if (param == "018") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Trattoria Bandierette"
            } else if (param == "019") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Trattoria Agli Artisti Pizzeria"
            } else if (param == "020") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Osteria Alla Staffa"
            } else if (param == "021") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "6342 Alla Corte Spaghetteria"
            } else if (param == "022") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Al Giardinetto Da Severino"
            } else if (param == "023") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Trattoria Da Remigio"
            } else if (param == "024") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Osteria Oliva Nera"
            } else if (param == "025") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Hostaria Castello"
            } else if (param == "026") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Trattoria Da Jonny"
            } else if (param == "027") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Taverna Scalinetto"
            } else if (param == "028") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Bacaretto Cicchetto"
            } else if (param == "029") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Ristorante Carpaccio"
            } else if (param == "030") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Trattoria Storica"
            } else if (param == "031") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title =
                    "Osteria Giorgione Da Masa"
            } else if (param == "032") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Hostaria Bacanera"
            } else if (param == "033") {
                binding.navView.menu.findItem(R.id.navigation_dashboard).title = "Corte Sconta"
            }
        }

    }

    private fun handleNavigationItemSelection(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                viewModel.selectedTab = 0
                goToDashBoard()
                return true
            }

            R.id.navigation_home -> {
                viewModel.selectedTab = 1
                goToMenu()
                return true
            }

            R.id.navigation_orders -> {
                viewModel.selectedTab = 2
                goToReservation()
                return true
            }

            R.id.navigation_notifications -> {
                viewModel.selectedTab = 3
                goToInfo()
                return true
            }
        }
        return false
    }

    private fun goToInfo() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, InformationFragment())
            .commit()
    }

    private fun goToReservation() {
        val ordFrag = OrdersFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.nav_host_fragment_activity_main,
                ordFrag
            )
            .commit()
    }

    private fun goToMenu() {
        if (uri != null) {
            if (param.isNotEmpty()) {
                //binding.navView.menu.findItem(R.id.navigation_dashboard).isVisible = false
                //binding.navView.menu.findItem(R.id.navigation_orders).isVisible = false

                val bundle = Bundle().apply {
                    putString("IdPlace", param)
                    putString("UpdatedTitle", updatedTitle)
                }

                val homeFragment = HomeFragment().apply {
                    arguments = bundle  // Set the bundle as arguments for the fragment
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, homeFragment)
                    .commit()
            }
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                .commit()

            Constants.m_PlaceID = ""
            Constants.Title_set = ""
        }

    }

    private fun goToDashBoard() {
        val dashboardFragment = DashboardFragment.newInstance(this)

        supportFragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment_activity_main,
            dashboardFragment
        ).commit()
    }

    private fun updateTitleAndVisibility(title: String) {
        isFirst = false
        binding.navView.menu.findItem(R.id.navigation_dashboard).title = title
        editor.putBoolean(Constants.PREF_KEY_IS_FIRST, isFirst)
        editor.apply()
        binding.navView.visibility = View.VISIBLE

        //updatedTitle = title
    }

    override fun changeTitle(title: String) {
        updateTitleAndVisibility(title)
    }

    private fun getDeviceInfo(): InsightModel.InsightApp {
        val telephonyManager =
            baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val batteryManager = baseContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        return InsightModel.InsightApp(
            lastAccess = listOf(getCurrentDateTime()),
            language = Locale.getDefault().language,
            telephoneType = getTelephoneType(telephonyManager),
            appVersion = BuildConfig.VERSION_NAME,
            battery = getBatteryLevel(batteryManager),
            osVersion = "Android ${Build.VERSION.RELEASE}",
            model = Build.MODEL
        )
    }

    private fun getTelephoneType(telephonyManager: TelephonyManager): String {
        return when (telephonyManager.phoneType) {
            TelephonyManager.PHONE_TYPE_GSM -> "GSM"
            TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
            else -> "Unknown"
        }
    }

    private fun getBatteryLevel(batteryManager: BatteryManager): String {
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return "$batteryLevel%"
    }

    private fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return currentDateTime.format(formatter)
    }

    private fun showInstallPrompt() {
        val postInstall = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage("celo.urestaurants.ui")


        InstantApps.showInstallPrompt(
            this,
            postInstall, REQUEST_CODE_INSTANT_APP_INSTALL, /* referrer= */ null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_INSTANT_APP_INSTALL) {
            // Check if the installation was successful
            if (resultCode == Activity.RESULT_OK) {
                // The instant app is now installed, you can navigate to your fragment here
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                // Installation was not successful
                // Handle the failure or provide feedback to the user
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            // Permission granted, start requesting location updates
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            // Handle case where permissions are not granted
            // You may request permissions here or inform the user
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start requesting location updates
            startLocationUpdates()
        } else {
            // Permission denied, show a message or handle accordingly
            //Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}