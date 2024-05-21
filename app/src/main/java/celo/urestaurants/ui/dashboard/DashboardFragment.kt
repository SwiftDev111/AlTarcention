package celo.urestaurants.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import celo.urestaurants.MainTitleNavigationBar
import celo.urestaurants.Utils.hideKeyboard
import celo.urestaurants.adapters.CategoryAdapter
import celo.urestaurants.adapters.LocationAdapter
import celo.urestaurants.adapters.SearchCategoryAdapter
import celo.urestaurants.animation.ItemsAnimation.addEnterAnimation
import celo.urestaurants.animation.ItemsAnimation.setFilterVisibilityWithAnimation
import celo.urestaurants.constants.Constants
import celo.urestaurants.databinding.FragmentDashboardBinding
import celo.urestaurants.models.CatFavoriteModel
import celo.urestaurants.models.CatInfoModel
import celo.urestaurants.models.CategoryModel
import celo.urestaurants.models.Pictures
import celo.urestaurants.models.RestaurantType
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class DashboardFragment @Inject constructor() : Fragment() {
    companion object {
        private lateinit var cateClickListener: MainTitleNavigationBar
        fun newInstance(listener: MainTitleNavigationBar): DashboardFragment {
            cateClickListener = listener
            return DashboardFragment()
        }
    }


    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: FragmentDashboardBinding

    private var categoryAdapter: CategoryAdapter? = null
    private lateinit var restaurantsAdapter: LocationAdapter
    private lateinit var pizzerieAdapter: LocationAdapter
    private lateinit var barsAdapter: LocationAdapter
    private var searchAdapter: SearchCategoryAdapter? = null
    private var filterKeys = mutableListOf<String>()
    private var deviceId: String = ""
    private lateinit var sharedPref: SharedPreferences
    private var editor: SharedPreferences.Editor? = null
    private var lastSelectedText: String? = null

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null

    @SuppressLint("NotifyDataSetChanged", "HardwareIds")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        deviceId =
            "" + Settings.Secure.getString(
                requireActivity().contentResolver,
                Settings.Secure.ANDROID_ID
            )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater).apply {
            lifecycleOwner = this@DashboardFragment

            //allLocationData(CategoryModel())
            //if(Constants.LT_LNG.isNotEmpty()){
            //Toast.makeText(requireContext(), Constants.LT_LNG, Toast.LENGTH_SHORT).show()

            //binding.textMainTitle.setText(R.string.str_tl_urestaurants)
            //binding.CityMainConstraint.visibility = View.VISIBLE
            //binding.PlacesMainConstraint.visibility = View.VISIBLE
            //}
            //RealtimeDataBaseData()

            sharedPref =
                requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
            editor = sharedPref.edit()

            firebaseDatabase = FirebaseDatabase.getInstance()
            databaseReference = firebaseDatabase!!.getReference("000-City")

            getdata()

            restaurantsAdapter =
                LocationAdapter(requireContext(), deviceId, { categoryModel: CategoryModel ->
                    processClickedCategory(
                        categoryModel
                    )
                }, { item, filled ->
                    manageHeart(item, filled)
                })

            listDashRestoranti.adapter = restaurantsAdapter

            addEnterAnimation(listDashRestoranti, requireContext())

            pizzerieAdapter =
                LocationAdapter(requireContext(), deviceId, { categoryModel: CategoryModel ->
                    processClickedCategory(
                        categoryModel
                    )
                }, { item, filled ->
                    manageHeart(item, filled)
                })
            listDashPizzerie.adapter = pizzerieAdapter

            addEnterAnimation(listDashPizzerie, requireContext())

            barsAdapter =
                LocationAdapter(requireContext(), deviceId, { categoryModel: CategoryModel ->
                    processClickedCategory(
                        categoryModel
                    )
                }, { item, filled ->
                    manageHeart(item, filled)
                })
            listDashBars.adapter = barsAdapter

            searchAdapter = SearchCategoryAdapter(deviceId, { categoryModel: CategoryModel ->
                processClickedCategory(
                    categoryModel
                )
            }, { item, filled ->
                manageHeart(item, filled)
            }
            )
            listDashSearch.adapter = searchAdapter

            addEnterAnimation(listDashSearch, requireContext())

        }
        return binding.root
    }

    /*private fun allLocationData(categoryModel: CategoryModel) {
        Log.d("loactionDATa", "allLocationData: "+categoryModel.catInfo.urlMap)

        val categoryModelArray: Array<CategoryModel> = arrayOf(CategoryModel())
            // Loop through the array
            for (model in categoryModelArray) {
                // Access each CategoryModel object in the array
                val catInfo = model.catInfo
                val urlMap = catInfo.urlMap

                // Use catInfo data or perform other operations here
                Log.d("loactionDATa", "URL Map: $urlMap")
            }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.placeFilter.setOnClickListener {
            showDropdown()
        }

        getImage_FromStorage()
        //ImageGetFromStorage()

        sharedPref =
            requireActivity().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        editor = sharedPref?.edit()


        val CitySelected = sharedPref.getString("selectedText", "")
        if (CitySelected?.isEmpty() == true) {
            binding.listCityTxt.text = "All"
        } else {
            binding.listCityTxt.text = CitySelected
        }

        requireActivity().baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        viewModel.loadDataFromCacheOrNetwork()

        //val isLocked = sharedPref.getBoolean("locked", false)

        setupObservers()
        requestPhoneStatePermission()
        setMainTitle()

        Constants.m_categories = mutableListOf()

        binding.searchView.searchInputText.hint = getString(celo.urestaurants.R.string.tarcentino)
        binding.searchView.onOpenSearchClicked = {
            binding.scrollView.visibility = View.GONE
            binding.listDashSearch.visibility = View.VISIBLE
            loadAllSearchCats()
        }

        binding.searchView.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.searchView.searchInputText.text.toString() != "") {
                    binding.listDashSearch.visibility = View.VISIBLE
                    searchText()
                } else {
                    loadAllSearchCats()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        binding.searchView.onCloseSearchClicked = {

            hideKeyboard(requireActivity())

            binding.scrollView.visibility = View.VISIBLE
            binding.listDashSearch.visibility = View.GONE
        }

        categoryAdapter =
            CategoryAdapter(mActivity = requireActivity()) { position: Int, filters: List<String>? ->
                filterKeys.clear()
                filterKeys.addAll(filters!!)
                loadData(filterButton = true)
                /*if (isLocked){
                    loadDataFromSharedPreferences()
                    Log.d("LOAD_DATA", "onViewCreated: LOAD_SHAREDPREF_DATA")
                }else{
                    loadData(filterButton = true)
                    Log.d("LOAD_DATA", "onViewCreated: LOAD_DATA")
                }*/
            }

        binding.listDashCategories.adapter = categoryAdapter
        addEnterAnimation(binding.listDashCategories, requireContext())
    }

    private fun setMainTitle() {
        val mainTitle = sharedPref?.getString(Constants.PREF_KEY_MAIN_TITLE, "")
        if (mainTitle != "" && mainTitle != null) {

            if (Constants.Title_set.isNotEmpty()) {
                binding.searchView.txtDashTitle.setText(celo.urestaurants.R.string.str_tl_urestaurants)
            } else {
                if (mainTitle.length > 20) {
                    binding.searchView.txtDashTitle.textSize = 24f
                } else {
                    binding.searchView?.txtDashTitle?.textSize = 26f
                }
                binding.searchView?.txtDashTitle?.text = mainTitle
            }

        } else {
            binding.searchView.txtDashTitle.setText(celo.urestaurants.R.string.str_tl_urestaurants)
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categories?.let {
                Constants.m_categories.addAll(it)
                filterKeys.clear()
                categoryAdapter?.refreshData(Constants.m_categories)
                categoryAdapter?.notifyDataSetChanged()
                binding.categoriesShimmer.shimmer.visibility = View.GONE
                binding.listDashCategories.visibility = View.VISIBLE
                loadData()
                try {
                    loadDataFromSharedPreferences()
                } catch (e: Exception) {

                }

            } ?: run {
                Timber.e("ERROR LOADING INFO")
            }
        }

        viewModel.updateItem.observe(viewLifecycleOwner) { location ->
            // optimize using single update. Now is not used

            restaurantsAdapter.notifyDataSetChanged()
            pizzerieAdapter.notifyDataSetChanged()
            barsAdapter.notifyDataSetChanged()
            searchAdapter?.notifyDataSetChanged()
        }

//        viewModel.networkChanges.observe(viewLifecycleOwner) { isAvailable ->
//            binding.noInternet.visibility = if(isAvailable) View.GONE else View.VISIBLE
//        }
    }

    private fun requestPhoneStatePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                "android.permission.READ_PRIVILEGED_PHONE_STATE"
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.READ_PRIVILEGED_PHONE_STATE"),
                123
            )
        }
    }

    private fun processClickedCategory(category: CategoryModel) {
        Constants.m_category = category

        binding.searchView.txtDashTitle.text = Constants.m_category.catInfo.nome
        if (Constants.m_category.catInfo.nome.length > 20) {
            binding.searchView.txtDashTitle.textSize = 24f
        } else {
            binding.searchView.txtDashTitle.textSize = 26f
        }
        editor?.putString(Constants.PREF_KEY_MAIN_TITLE, Constants.m_category.catInfo.nome)
        editor?.apply()
        editor?.putString(Constants.PREF_KEY_MAIN_KEY, Constants.m_category.catKey)
        editor?.apply()

        cateClickListener.changeTitle(Constants.m_category.catInfo.nome)
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(celo.urestaurants.R.id.nav_view)

        bottomNavigationView.selectedItemId = celo.urestaurants.R.id.navigation_home

        //urlMapExtractLat_Long(Constants.m_category.catInfo.urlMap,Constants.m_category.catKey)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { it ->
            if (it.isSuccessful) {
                /*Log.d("FCM Token", "processClickedCategory: " + it.result.toString())*/
                val token = it.result.toString()
                // Upload the token to Firebase Realtime Database or Firestore
                uploadTokenToFirebase(token, Constants.m_category.catKey)
            } else {
                /*Log.e("FCM Token", "Failed to get token: ${it.exception}")*/
            }
        }


    }

    private fun uploadTokenToFirebase(token: String, catKey: String?) {
        // Check if token is not null
        if (token != null) {
            // Assuming you have a "tokens" collection in Firestore
            val database =
                FirebaseDatabase.getInstance("https://urestaurants-notifications.europe-west1.firebasedatabase.app/")
            val tokensRef = database.getReference("notification")

            // Create a new document with the token as the document ID
            if (catKey != null) {
                val sdf = SimpleDateFormat("dd-M-yyyy")
                val currentDate = sdf.format(Date())

                database.getReference().child(catKey + "FCM").child(token).setValue(currentDate)
                    .addOnSuccessListener {
                        /*Log.d("FCM Token", "Token uploaded successfully: $token")*/
                    }
                    .addOnFailureListener { e ->
                        /*Log.e("FCM Token", "Failed to upload token: $token, ${e.message}")*/
                    }
            }
        } else {
            Log.e("FCM Token", "FCM token is null")
        }
    }

    /*private fun urlMapExtractLat_Long(urlMap: String, catKey: String?) {
        val llIndex = urlMap.indexOf("ll=")
        val commaIndex = urlMap.indexOf(',', startIndex = llIndex)

        val latitude = urlMap.substring(llIndex + 3, commaIndex).toDouble()
        val longitude = urlMap.substring(commaIndex + 1, urlMap.indexOf('&', startIndex = commaIndex)).toDouble()

        println("Latitude: $latitude::$longitude"+"::KEY: $catKey")
        //val distance = calculateDistance(Constants.currentLAT, Constants.currentLONG, latitude, longitude)
    }*/

    /*private fun calculateDistance(
        lat1: String, lon1: String,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371 // Radius of the earth in km
        val lat1Double = lat1.toDouble()
        val lon1Double = lon1.toDouble()

        val dLat = Math.toRadians(lat2 - lat1Double)
        val dLon = Math.toRadians(lon2 - lon1Double)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1Double)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c * 1000 // convert to meters
        val distanceKilometers = R * c

        return distance
    }*/


    @SuppressLint("NotifyDataSetChanged")
    private fun searchText() {
        val searchTxt = binding.searchView.searchInputText.text.toString()
        val resulList = searchCategories(Constants.m_categories, searchTxt)
        searchAdapter?.submitList(resulList)
        binding.listDashSearch.scrollToPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadAllSearchCats() {
        val resulList = searchCategories(Constants.m_categories)
        searchAdapter?.submitList(resulList)
        binding.listDashSearch.scrollToPosition(0)
    }

    private fun loadData(filterButton: Boolean = false) {
        binding.listDashRestoranti.scrollToPosition(0)
        binding.listDashPizzerie.scrollToPosition(0)
        binding.listDashBars.scrollToPosition(0)

        val restaurants =
            viewModel.getCategories(filterKeys, RestaurantType.RESTAURANT)
        val pizzas =
            viewModel.getCategories(filterKeys, RestaurantType.PIZZA)
        val bar = viewModel.getCategories(filterKeys, RestaurantType.BAR)

        val mainTitle = sharedPref?.getString(Constants.PREF_KEY_MAIN_TITLE, "") ?: ""

        val filteredRestaurants = restaurants.filter { it.catInfo.nome != mainTitle }
        val filteredPizza = pizzas.filter { it.catInfo.nome != mainTitle }
        val filteredBar = bar.filter { it.catInfo.nome != mainTitle }

        val mainTitleRestaurants = restaurants.find { it.catInfo.nome == mainTitle }
        val reorderedRestaurants = if (mainTitleRestaurants != null) {
            listOf(mainTitleRestaurants) + filteredRestaurants
        } else {
            filteredRestaurants
        }

        val mainTitlePizza = pizzas.find { it.catInfo.nome == mainTitle }
        val reorderedPizza = if (mainTitlePizza != null) {
            listOf(mainTitlePizza) + filteredPizza
        } else {
            filteredPizza
        }

        val mainTitleBar = bar.find { it.catInfo.nome == mainTitle }
        val reorderedBar = if (mainTitleBar != null) {
            listOf(mainTitleBar) + filteredBar
        } else {
            filteredBar
        }

        restaurantsAdapter.submitList(reorderedRestaurants)
        pizzerieAdapter.submitList(reorderedPizza)
        barsAdapter.submitList(reorderedBar)

        if (reorderedRestaurants.isEmpty()) {
            binding.txtDashRestoranti.visibility = View.GONE
        }
        if (reorderedPizza.isEmpty()) {
            binding.txtDashPizzerie.visibility = View.GONE
        }
        if (reorderedBar.isEmpty()) {
            binding.txtDashBar.visibility = View.GONE
        }

        if (filterButton) {
            setFilterVisibility(restaurants, pizzas, bar)
        } else {
            setShimmerVisibility(restaurants, pizzas, bar)
        }

        saveDataToSharedPreferences(reorderedRestaurants, reorderedPizza, reorderedBar, mainTitle)
    }

    private fun setShimmerVisibility(
        restaurants: List<CategoryModel>,
        pizzas: List<CategoryModel>,
        bar: List<CategoryModel>
    ) {
        if (restaurants.isNotEmpty()) {
            binding.listDashRestoranti.visibility = View.VISIBLE
            binding.restaurantShimmer.shimmer.visibility = View.GONE
        } else {
            binding.listDashRestoranti.visibility = View.GONE
            binding.restaurantShimmer.shimmer.visibility = View.GONE
        }

        if (pizzas.isNotEmpty()) {
            binding.listDashPizzerie.visibility = View.VISIBLE
            binding.pizzerieShimmer.shimmer.visibility = View.GONE
        } else {
            binding.listDashPizzerie.visibility = View.GONE
            binding.pizzerieShimmer.shimmer.visibility = View.GONE
        }

        if (bar.isNotEmpty()) {
            binding.listDashBars.visibility = View.VISIBLE
            binding.barShimmer.shimmer.visibility = View.GONE
        } else {
            binding.listDashBars.visibility = View.GONE
            binding.barShimmer.shimmer.visibility = View.GONE
        }
    }

    private fun setFilterVisibility(
        restaurants: List<CategoryModel>,
        pizzas: List<CategoryModel>,
        bar: List<CategoryModel>
    ) {

        setFilterVisibilityWithAnimation(
            binding.listDashRestoranti,
            binding.txtDashRestoranti,
            restaurants.isNotEmpty()
        )

        setFilterVisibilityWithAnimation(
            binding.listDashPizzerie,
            binding.txtDashPizzerie,
            pizzas.isNotEmpty()
        )

        setFilterVisibilityWithAnimation(
            binding.listDashBars,
            binding.txtDashBar,
            bar.isNotEmpty()
        )
    }

    private fun manageHeart(item: CategoryModel, filled: Boolean) {
        val catFav = CatFavoriteModel()
        catFav.deviceId = deviceId
        if (filled) {
            viewModel.mDatabase.child(item.catKey!!).child("Favorites")
                .child(deviceId)
                .removeValue()
            var indx = -1
            for (i in item.favorites.indices) {
                if (item.favorites[i].deviceId == deviceId) {
                    indx = i
                }
            }
            if (indx != -1) {
                item.favorites.removeAt(indx)
            }
        } else {
            catFav.favorite = "true"
            item.favorites.add(catFav)
            viewModel.mDatabase.child(item.catKey!!).child("Favorites")
                .child(deviceId)
                .setValue(catFav)
        }
        viewModel.updateDataCache(item)
    }

    private fun searchCategories(
        categoryModels: List<CategoryModel>,
        filterKey: String? = ""
    ): List<CategoryModel> {
        val searchCats = mutableListOf<CategoryModel>()
        for (i in categoryModels.indices) {
            val name = categoryModels[i].catInfo.nome
            val atr1 = categoryModels[i].catInfo.atr1
            val atr2 = categoryModels[i].catInfo.atr2
            if (filterKey?.lowercase(Locale.getDefault())?.let {
                    name.lowercase(Locale.getDefault())
                        .contains(it)
                } == true
            ) {
                if (atr1 != "") {
                    if (atr2 != "") {
                        searchCats.add(categoryModels[i])
                    }
                }
            }
        }
        return searchCats
    }


    private fun saveDataToSharedPreferences(
        filteredRestaurants: List<CategoryModel>,
        filteredPizza: List<CategoryModel>,
        filteredBar: List<CategoryModel>,
        mainTitle: String
    ) {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(Constants.PREF_KEY_MAIN_TITLE, mainTitle)
        editor.putString("filteredRestaurants", Gson().toJson(filteredRestaurants))
        editor.putString("filteredPizza", Gson().toJson(filteredPizza))
        editor.putString("filteredBar", Gson().toJson(filteredBar))
        editor.apply()
    }

    inline fun <reified T> Gson.fromJson(json: String): T {
        return this.fromJson(json, object : TypeToken<T>() {}.type)
    }

    private fun loadDataFromSharedPreferences() {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val mainTitle = sharedPreferences.getString(Constants.PREF_KEY_MAIN_TITLE, "") ?: ""
        val filteredRestaurantsJson = sharedPreferences.getString("filteredRestaurants", null)
        val filteredPizzaJson = sharedPreferences.getString("filteredPizza", null)
        val filteredBarJson = sharedPreferences.getString("filteredBar", null)

        val gson = Gson()

        val filteredRestaurants: List<CategoryModel>? = filteredRestaurantsJson?.let {
            gson.fromJson(
                it
            )
        }
        val filteredPizza: List<CategoryModel>? = filteredPizzaJson?.let { gson.fromJson(it) }
        val filteredBar: List<CategoryModel>? = filteredBarJson?.let { gson.fromJson(it) }

        // Update UI with loaded data
        restaurantsAdapter.submitList(filteredRestaurants)
        pizzerieAdapter.submitList(filteredPizza)
        barsAdapter.submitList(filteredBar)

        if (filteredRestaurants.isNullOrEmpty()) {
            binding.txtDashRestoranti.visibility = View.GONE
        }
        if (filteredPizza.isNullOrEmpty()) {
            binding.txtDashPizzerie.visibility = View.GONE
        }
        if (filteredBar.isNullOrEmpty()) {
            binding.txtDashBar.visibility = View.GONE
        }

        val sharedPref =
            requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val city = sharedPref.getString("selectedText", "")!!
        if (city.equals("All")) {
            // Set visibility to VISIBLE for all categories if city is "All"
            binding.txtDashPizzerie.visibility = View.VISIBLE
            binding.txtDashRestoranti.visibility = View.VISIBLE
            binding.txtDashBar.visibility = View.VISIBLE
        } else if (city.isNotEmpty()) {
            // If city is not empty, proceed with filtering and city matching logic
            val mainTitle = sharedPref?.getString(Constants.PREF_KEY_MAIN_TITLE, "") ?: ""

            val pizzas = viewModel.getCategories(filterKeys, RestaurantType.PIZZA)
            val filteredPizza = pizzas.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedPizza = filteredPizza.any { it.catInfo.city == city }
            if (isCityMatchedPizza) {
                binding.txtDashPizzerie.visibility = View.VISIBLE
            } else binding.txtDashPizzerie.visibility = View.GONE

            val restaurants = viewModel.getCategories(filterKeys, RestaurantType.RESTAURANT)
            val filteredRestaurants = restaurants.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedRestaurants = filteredRestaurants.any { it.catInfo.city == city }
            if (isCityMatchedRestaurants) {
                binding.txtDashRestoranti.visibility = View.VISIBLE
            } else binding.txtDashRestoranti.visibility = View.GONE

            val bars = viewModel.getCategories(filterKeys, RestaurantType.BAR)
            val filteredBars = bars.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedBars = filteredBars.any { it.catInfo.city == city }
            if (isCityMatchedBars) {
                binding.txtDashBar.visibility = View.VISIBLE
            } else binding.txtDashBar.visibility = View.GONE
        }
    }


    private fun getdata() {
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (citySnapshot in dataSnapshot.children) {
                    val cityName = citySnapshot.key // Get the city name
                    val cityDataMap =
                        citySnapshot.value as? Map<String, Any> // Get the city data as a Map

                    if (cityDataMap != null) {
                        val latitude = cityDataMap["x"] as? Double
                        val longitude = cityDataMap["y"] as? Double

                        if (latitude != null && longitude != null) {
                            Constants.cityNames.add(cityName.toString())
                            //Log.d("Firebase", "City: $cityName, Lat: $latitude, Long: $longitude")
                            //val distance = calculateDistance(Constants.currentLAT, Constants.currentLONG, latitude, longitude)
                            /*val distance = calculateDistance("30.707600", "76.715126", latitude, longitude)
                            if (distance <= 5700) {
                                //Log.d("Firebase121211", "City: $cityName, Lat: $latitude, Long: $longitude, Distance: $distance km")
                                // Here you can do whatever you want with the cities that meet the distance criteria
                            }*/
                        } else {
                            //Log.e("Firebase", "Latitude or longitude is null for city: $cityName")
                        }
                    } else {
                        //Log.e("Firebase", "City data map is null for city: $cityName")
                    }
                }
            }

            override fun onCancelled(@SuppressLint("KotlinNullnessAnnotation") error: DatabaseError) {
                Toast.makeText(requireContext(), "Fail to get data.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun showDropdown() {
        val items =
            mutableListOf<String>("All") // Initialize a mutable list to hold items with "All" already added

        items.clear()

        items.add("All")

        for (cityName in Constants.cityNames.distinct()) {
            items.add(cityName)
        }

        val itemsArray = items.toTypedArray() // Convert the list to an array

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            itemsArray
        )


        val spinner = Spinner(requireContext())
        spinner.adapter = adapter

        lastSelectedText = sharedPref.getString("selectedText", null)
        val lastSelectedIndex = items.indexOf(lastSelectedText)
        if (lastSelectedIndex != -1) {
            spinner.setSelection(lastSelectedIndex)
        }

        // Create a PopupWindow with the spinner
        val popupWindow = PopupWindow(
            spinner,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set background drawable to make it dismiss when touched outside
        popupWindow.setBackgroundDrawable(resources.getDrawable(android.R.color.white))

        // Calculate x and y coordinates for the PopupWindow
        val location = IntArray(2)
        binding.placeFilter.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1] + binding.placeFilter.height

        // Show PopupWindow
        popupWindow.showAtLocation(binding.placeFilter, Gravity.NO_GRAVITY, x, y)

        // Update listCityTxt when an item is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedText = parent?.getItemAtPosition(position).toString()
                binding.listCityTxt.text = selectedText
                binding.listCityTxt.gravity = Gravity.START
                //binding.listCityTxt.gravity = if (selectedText == "All") Gravity.START else Gravity.CENTER
                lastSelectedText = selectedText // Save the last selected text

                refreshScreen()

                editor?.putString("selectedText", selectedText)
                editor?.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged", "UseRequireInsteadOfGet")
    fun refreshScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            loadDataFromSharedPreferences()
            //Do something after 100ms
        }, 500)
    }


    fun showTilte() {
        val sharedPref =
            requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val city = sharedPref.getString("selectedText", "")!!

        if (city == "All") {
            // Set visibility to VISIBLE for all categories if city is "All"
            binding.txtDashPizzerie.visibility = View.VISIBLE
            binding.txtDashRestoranti.visibility = View.VISIBLE
            binding.txtDashBar.visibility = View.VISIBLE
        } else if (city.isNotEmpty()) {
            // If city is not empty, proceed with filtering and city matching logic
            val mainTitle = sharedPref?.getString(Constants.PREF_KEY_MAIN_TITLE, "") ?: ""

            val pizzas = viewModel.getCategories(filterKeys, RestaurantType.PIZZA)
            val filteredPizza = pizzas.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedPizza = filteredPizza.any { it.catInfo.city == city }
            if (isCityMatchedPizza) {
                binding.txtDashPizzerie.visibility = View.VISIBLE
            } else binding.txtDashPizzerie.visibility = View.GONE

            val restaurants = viewModel.getCategories(filterKeys, RestaurantType.RESTAURANT)
            val filteredRestaurants = restaurants.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedRestaurants = filteredRestaurants.any { it.catInfo.city == city }
            if (isCityMatchedRestaurants) {
                binding.txtDashRestoranti.visibility = View.VISIBLE
            } else binding.txtDashRestoranti.visibility = View.GONE

            val bars = viewModel.getCategories(filterKeys, RestaurantType.BAR)
            val filteredBars = bars.filter { it.catInfo.nome != mainTitle }
            val isCityMatchedBars = filteredBars.any { it.catInfo.city == city }
            if (isCityMatchedBars) {
                binding.txtDashBar.visibility = View.VISIBLE
            } else binding.txtDashBar.visibility = View.GONE
        }
    }


    private fun getImage_FromStorage() {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.getReference("ImagePlaces")

        storageReference.listAll()
            .addOnSuccessListener(OnSuccessListener<ListResult> { listResult ->
                // Iterate through each item in the result
                for (item in listResult.items) {
                    // Display the image
                    val catInfoModel = CatInfoModel()
                    displayImage(item,catInfoModel)
                    Log.d("displayImg", "getImage_FromStorage: " + listResult.items.toString())
                }
            })
            .addOnFailureListener(OnFailureListener {
                // Handle any errors
            })
    }

    private fun displayImage(imageRef: StorageReference,catInfoModel: CatInfoModel) {
        val uriList = ArrayList<String>()

        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // TODO: Display the image on the UI
                uriList.add(uri.toString())
                catInfoModel.logoUrl = uri.toString()

                val pic: ArrayList<Pictures> = ArrayList() // Initialize pic as an ArrayList
                val picture = Pictures(uri.toString()) // Create a new Picture object with the URI
                pic.add(picture) // Add the Picture object to the list

                if (Constants.urlImages == null) {
                    Constants.urlImages = mutableListOf()
                }

                for (i in pic){
                    //Log.d("picpicpic", "displayImage: " + i.images)
                    Constants.urlImages.add(i.images)
                    //Log.d("length", "displayImage: "+pic)
                }

                //Log.d("URL_LIST", "catInfoModel.logoUrl: "+catInfoModel.logoUrl)
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }

}