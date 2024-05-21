package celo.urestaurants.ui.informations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import celo.urestaurants.R
import celo.urestaurants.constants.Constants
import celo.urestaurants.constants.Constants.m_PlaceID
import celo.urestaurants.databinding.FragmentInformationBinding
import celo.urestaurants.models.CatInfoModel
import celo.urestaurants.models.CategoryModel
import celo.urestaurants.models.RestaurantState
import celo.urestaurants.models.RestaurantType
import celo.urestaurants.ui.dashboard.DashboardViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.play.integrity.internal.i
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class InformationFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentInformationBinding
    private val viewModel: InformationViewModel by viewModels()
    private val viewModelDash: DashboardViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private var mapUrl: String? = ""
    var mID: String = ""
    var mIdPlace: String = ""

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                if (Constants.m_category.catInfo.telefono.isNotBlank() || Constants.m_category.catInfo.telefono.isNotEmpty())
                    callNumber(Constants.m_category.catInfo.telefono)
        }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        try {
            val IdPlace = arguments?.getString("IdPlace")!!
            mIdPlace = IdPlace
        }catch (e:Exception){

        }

        binding = FragmentInformationBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@InformationFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d("ConstantID", "InfoFragment: " + Constants.m_PlaceID)
        if (false) {
            with(binding) {
                accountConstraintLayout.isVisible = true
                userLayout.setOnClickListener {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(requireContext(), "Log Out", Toast.LENGTH_SHORT).show()
                }
            }

        }


        sharedPref =
            requireActivity().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)


        initSharedPreferences()

        binding.txtTitle.text = Constants.m_category.catInfo.nome
        val version: String = Constants.m_category.catInfo.version

        val lastUpdate = if (version == "") getString(R.string.placeholder_update) else version
        binding.txtViaBtm.text = getString(R.string.dati_aggiornati_il, lastUpdate)
//        val mainImgUrl = Constants.m_category.catInfo.logoUrl

//        Glide.with(requireContext())
//            .load(mainImgUrl)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(binding.imgNotiMain)

//        if (Constants.m_PlaceID.isNotEmpty()){
//            Log.d("PLACE_ID", "INFO: "+Constants.m_PlaceID)
//            val storage = FirebaseStorage.getInstance()
//            val storageReference = storage.reference.child("ImagePlaces/") // Make sure to include the trailing slash
//
//            val catID =  Constants.m_PlaceID
//            val extractedfile = ".jpg"
//
//            val imagePath = storageReference.child(catID + extractedfile)
//            Log.d("imagePathINFO", "imagePathINFO: DEEP_LINKING"+imagePath)
//
//            imagePath.downloadUrl.addOnSuccessListener { uri ->
//                val imageUrl = uri.toString() // This will give you the URL in the format you mentioned
//                // Use the imageUrl with Glide to load the image\
//                Glide.with(requireContext())
//                    .load(imageUrl)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(binding.imgNotiMain)
//            }.addOnFailureListener {
//                // Handle any errors
//            }
//        }


        val sharedPreferences = requireContext().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //editor.putString(Constants.PREF_KEY_IMAGE_URL, Constants.m_category.catInfo.logoUrl)
        editor.putString(Constants.PREF_KEY_IMAGE_URL, Constants.m_category.catKey)
        editor.apply()

        val mainImgUrl = sharedPreferences.getString(Constants.PREF_KEY_IMAGE_URL, "")
        /*if (!mainImgUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(mainImgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgNotiMain)
            Log.d("imagePathINFO", "imagePathINFO: ifff")
        }*/
        if(mainImgUrl!!.isEmpty()){
            Glide.with(requireContext())
                .load(mainImgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgNotiMain)
            Log.d("imagePathINFO", "imagePathINFO: ifff")
            Log.d("imagePathINFO", "mainImgUrl: "+mainImgUrl)
        }
        else{
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference.child("ImagePlaces/") // Make sure to include the trailing slash

            var catID = Constants.m_category.catKey!!
            val extractedfile = ".jpg"

            val imagePath = storageReference.child(catID + extractedfile)
            Log.d("imagePathINFO", "imagePathINFO: else"+imagePath)

            imagePath.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString() // This will give you the URL in the format you mentioned
                // Use the imageUrl with Glide to load the image\
                Glide.with(requireContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgNotiMain)
            }.addOnFailureListener {
                // Handle any errors
            }
        }

        val strVia = Constants.m_category.catInfo.urlMap
        binding.txtVia.text = extractAddressFromAppleMapsLink(strVia)

        val telephone = getString(R.string.tel) + Constants.m_category.catInfo.telefono
        binding.txtTel.text = telephone

        mapUrl = Constants.m_category.catInfo.urlGMap

        val newMapFragment = SupportMapFragment.newInstance()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment, newMapFragment)
            .commit()

        newMapFragment.getMapAsync { googleMap ->
            mapUrl?.let { addPinFromGoogleMapsLink(it, googleMap) }
        }

        binding.clickLayer.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                startActivity(browserIntent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.mappa_momentaneamente_non_disponibile),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.apply {

            telephoneNumber.setOnClickListener {
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            txtMon.text = Constants.m_category.catInfo.m_monday
            txtTue.text = Constants.m_category.catInfo.m_tuesday
            txtWed.text = Constants.m_category.catInfo.m_wednesday
            txtThu.text = Constants.m_category.catInfo.m_thursday
            txtFri.text = Constants.m_category.catInfo.m_friday
            txtSat.text = Constants.m_category.catInfo.m_saturday
            txtSun.text = Constants.m_category.catInfo.m_sunday

            val calendar = Calendar.getInstance()

            when (calendar[Calendar.DAY_OF_WEEK]) {
                Calendar.SUNDAY -> setupDayLayout(sunLayout, Constants.m_category.catInfo.m_sunday)
                Calendar.MONDAY -> setupDayLayout(monLayout, Constants.m_category.catInfo.m_monday)
                Calendar.TUESDAY -> setupDayLayout(
                    tueLayout,
                    Constants.m_category.catInfo.m_tuesday
                )

                Calendar.WEDNESDAY -> setupDayLayout(
                    wedLayout,
                    Constants.m_category.catInfo.m_wednesday
                )

                Calendar.THURSDAY -> setupDayLayout(
                    thuLayout,
                    Constants.m_category.catInfo.m_thursday
                )

                Calendar.FRIDAY -> setupDayLayout(friLayout, Constants.m_category.catInfo.m_friday)
                Calendar.SATURDAY -> setupDayLayout(
                    satLayout,
                    Constants.m_category.catInfo.m_saturday
                )
            }

            imgArrowUpDown.setOnClickListener {
                if (lltWeeks.visibility == View.GONE) {
                    lltWeeks.visibility = View.VISIBLE
                    viewMiddle.visibility = View.VISIBLE
                    imgArrowUpDown.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ico_arrow_up,
                            null
                        )
                    )
                } else {
                    lltWeeks.visibility = View.GONE
                    viewMiddle.visibility = View.GONE
                    imgArrowUpDown.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            requireActivity().resources,
                            R.drawable.ico_arrow_down,
                            null
                        )
                    )
                }
            }

            linearLayoutAddBusiness.setOnClickListener {
                val bottomSheetFragment = SendEmailBottomSheet()
                bottomSheetFragment.subject = getString(R.string.entra_a_far_parte_di_urestaurant)
                bottomSheetFragment.text =
                    getString(R.string.mi_piacerebbe_entrare_nel_business_cosa_mi_serve)
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            linearLayoutNoteLegali.setOnClickListener {
                val bottomSheetFragment = PrivacyPolicyBottomSheet()
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }

            linearLayoutSegnalaUnProblema.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse(getString(R.string.mailto_business_urestaurants_app))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.segnala_un_problema))
                startActivity(intent)
            }
        }

    }

    private fun setupDayLayout(
        layout: View,
        times: String
    ) {
        layout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.greyLight
            )
        )
        binding.apply {
            txtAperto.text = times
            val state = checkHoursOpenClosed(times)

            if (state == RestaurantState.CLOSED) {
                txtApertoStatus.text = getString(R.string.chiuso)
                txtApertoStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            } else {
                txtApertoStatus.text = getString(R.string.aperto)
                txtApertoStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
            }
        }
    }

    private fun extractAddressFromAppleMapsLink(appleMapsLink: String): String? {
        val uri = Uri.parse(appleMapsLink)

        return uri.getQueryParameter("address")
    }

    private fun addPinFromGoogleMapsLink(googleMapsLink: String, googleMap: GoogleMap) {
        val (latitude, longitude, zoom) = extractCoordinatesAndZoom(googleMapsLink)

        binding.apply {
            if (latitude == 0.0 || longitude == 0.0) {
                imgNotiMap.visibility = View.VISIBLE
                mapFragment.visibility = View.GONE
            } else {
                imgNotiMap.visibility = View.GONE
                mapFragment.visibility = View.VISIBLE
            }
        }

        val target = LatLng(latitude, longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, zoom))
    }

    private fun extractCoordinatesAndZoom(googleMapsLink: String): Triple<Double, Double, Float> {
        val regex = Regex("@([-0-9.]+),([-0-9.]+),([0-9.]+)z")

        val matchResult = regex.find(googleMapsLink)

        return if (matchResult != null && matchResult.groupValues.size == 4) {
            val latitude = matchResult.groupValues[1].toDouble()
            val longitude = matchResult.groupValues[2].toDouble()
            val zoom = 15f
            Triple(latitude, longitude, zoom)
        } else {
            Triple(0.0, 0.0, 10.0f)
        }
    }

    private fun checkHoursOpenClosed(openingHoursString: String): RestaurantState {

        if (openingHoursString.equals("Chiuso", ignoreCase = true)) {
            return RestaurantState.CLOSED
        }

        val intervals = openingHoursString.split(" | ")

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentHour = sdf.format(Date())

        for (interval in intervals) {
            try {
                val timeRange = interval.split(" - ")

                val startTime = timeRange[0]
                val endTime = timeRange[1]

                val calStart = Calendar.getInstance()
                calStart.time = sdf.parse(startTime) ?: Date()

                val calEnd = Calendar.getInstance()
                calEnd.time = sdf.parse(endTime) ?: Date()

                val calCurrent = Calendar.getInstance()
                calCurrent.time = sdf.parse(currentHour) ?: Date()

                if ((calCurrent.after(calStart) || calCurrent == calStart) && calCurrent.before(
                        calEnd
                    )
                ) {
                    return RestaurantState.OPENED
                } else if (calStart.after(calEnd) && (calCurrent.after(calStart) || calCurrent.before(
                        calEnd
                    ))
                ) {
                    return RestaurantState.OPENED
                }
            } catch (e: Exception) {

            }

        }
        return RestaurantState.CLOSED
    }

    private fun initSharedPreferences() {

        var catInfoModel: List<CatInfoModel> = listOf()
        var nome: String? = ""
        for (i in 0 until catInfoModel.size) {
            val catInfo = catInfoModel[i]
            // Access CatInfoModel data, for example:
            nome = catInfo.nome
            //Log.d("nomenome", "nome: " + nome)
            // Process the data as needed
        }

        //Log.d("catInfoModel", "nome: " + nome)
        val cache = viewModel.getCachedData()
        var key = sharedPref.getString(Constants.PREF_KEY_MAIN_KEY, "")
        try {
            mID = Constants.m_PlaceID
            if (Constants.m_PlaceID.isNotEmpty()) {
                    if (mID == "01") {
                        key = "01"
                        binding.txtTitle.text = "Al Tarcentino"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "02") {
                        key = "02"
                        binding.txtTitle.text = "Pronto Pizza G. Bassa"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "03") {
                        key = "03"
                        binding.txtTitle.text = "Albergo Riviera"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "04") {
                        key = "04"
                        binding.txtTitle.text = "Pronto Pizza G. Alta"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "05") {
                        key = "05"
                        binding.txtTitle.text = "Helios Pizza"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "06") {
                        key = "06"
                        binding.txtTitle.text = "Da Turi"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "07") {
                        key = "07"
                        binding.txtTitle.text = "Pizzeria Capriccio"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "08") {
                        key = "08"
                        binding.txtTitle.text = "Lendar"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "09") {
                        key = "09"
                        binding.txtTitle.text = "Tellme Pizza"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "10") {
                        key = "10"
                        binding.txtTitle.text = "Sofias's Bakery"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "011") {
                        key = "011"
                        binding.txtTitle.text = "Caffe Pittini"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "012") {
                        key = "012"
                        binding.txtTitle.text = "Tellme Pizza"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "013") {
                        key = "013"
                        binding.txtTitle.text = "Pizze e Delizie"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "014") {
                        key = "014"
                        binding.txtTitle.text = "Ostaria Boccadoro"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "015") {
                        key = "015"
                        binding.txtTitle.text = "Antico Gatoleto"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "016") {
                        key = "016"
                        binding.txtTitle.text = "Osteria Da Alberto"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "017") {
                        key = "017"
                        binding.txtTitle.text = "Osteria Al Ponte"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "018") {
                        key = "018"
                        binding.txtTitle.text = "Trattoria Bandierette"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "019") {
                        key = "019"
                        binding.txtTitle.text = "Trattoria Agli Artisti Pizzeria"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "020") {
                        key = "020"
                        binding.txtTitle.text = "Osteria Alla Staffa"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "021") {
                        key = "021"
                        binding.txtTitle.text = "6342 Alla Corte Spaghetteria"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "022") {
                        key = "022"
                        binding.txtTitle.text = "Al Giardinetto Da Severino"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "023") {
                        key = "023"
                        binding.txtTitle.text = "Trattoria Da Remigio"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "024") {
                        key = "024"
                        binding.txtTitle.text = "Osteria Oliva Nera"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "025") {
                        key = "025"
                        binding.txtTitle.text = "Hostaria Castello"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "026") {
                        key = "026"
                        binding.txtTitle.text = "Trattoria Da Jonny"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "027") {
                        key = "027"
                        binding.txtTitle.text = "Taverna Scalinetto"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "028") {
                        key = "028"
                        binding.txtTitle.text = "Bacaretto Cicchetto"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "029") {
                        key = "029"
                        binding.txtTitle.text = "Ristorante Carpaccio"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "030") {
                        key = "030"
                        binding.txtTitle.text = "Trattoria Storica"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "031") {
                        key = "031"
                        binding.txtTitle.text = "Osteria Giorgione Da Masa"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "032") {
                        key = "032"
                        binding.txtTitle.text = "Hostaria Bacanera"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    } else if (mID == "033") {
                        key = "033"
                        binding.txtTitle.text = "Corte Sconta"
                        val selected = cache?.first { it.catKey == key }
                        if (selected != null) {
                            Constants.m_category = selected
                        }
                    }
                }
               else{
                   val selected = cache?.first { it.catKey == key }
                   if (selected != null) {
                       Constants.m_category = selected
                   }
               }

        } catch (e: Exception) {

        }

    }

    private fun callNumber(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }
}
