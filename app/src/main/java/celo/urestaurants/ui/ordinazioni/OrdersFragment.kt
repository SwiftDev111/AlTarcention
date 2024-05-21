package celo.urestaurants.ui.ordinazioni

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import celo.urestaurants.R
import celo.urestaurants.Utils
import celo.urestaurants.adapters.SimpleTextAdapter
import celo.urestaurants.constants.Constants
import celo.urestaurants.constants.Constants.GOOGLE_SIGN_IN
import celo.urestaurants.databinding.FragmentOrdinazioniBinding
import celo.urestaurants.models.Reservation
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.Exception

@AndroidEntryPoint
class OrdersFragment @Inject constructor() : Fragment(), GoogleSignInBottomSheetListener,
    ReservationPreviewBottomSheetListener {
    private lateinit var binding: FragmentOrdinazioniBinding
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var googleSharedPref: SharedPreferences
    private lateinit var simpleTextAdapterNumbersPeople: SimpleTextAdapter
    private lateinit var simpleTextAdapterTime: SimpleTextAdapter

    companion object {
        fun newInstance(): OrdersFragment {
            return OrdersFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdinazioniBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            lifecycleOwner = this@OrdersFragment

            simpleTextAdapterNumbersPeople = SimpleTextAdapter { item ->
                viewModel.numbersOfPeople = item.titleText.toInt()

            }
            ordEnabled.apply {
                listPeopleNumbers.adapter = simpleTextAdapterNumbersPeople

                simpleTextAdapterTime = SimpleTextAdapter { item ->
                    viewModel.timeToOrder = item.titleText
                }
                timesList.adapter = simpleTextAdapterTime
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedPref = requireActivity().getSharedPreferences(Constants.prefName, MODE_PRIVATE)
        googleSharedPref = requireActivity().getSharedPreferences(GOOGLE_SIGN_IN, MODE_PRIVATE)

        initSharedPreferences()

        binding.txtTitle.text = Constants.m_category.catInfo.nome

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            openBottomSheetSIgnIn()
            binding.layerLogIn.isVisible = true
        } else {
            binding.layerLogIn.isVisible = false
        }

        canDoReservation()

        binding.btnLogInLayer.setOnClickListener {
            openBottomSheetSIgnIn()
        }

        binding.ordEnabled.btnNext.setOnClickListener {
            if (viewModel.numbersOfPeople < 1) {
                Toast.makeText(
                    requireContext(),
                    "Scegli il numero di persone",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (viewModel.timeToOrder.isNullOrEmpty() || viewModel.timeToOrder.isNullOrBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Scegli un'orario",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            showReservationPreview()
        }

        simpleTextAdapterNumbersPeople.submitList(viewModel.generateNumberOfPeopleList())
        setCalendar()
    }

    private fun openBottomSheetSIgnIn() {
        val bottomSheetFragment = GoogleSignInBottomSheet()
        bottomSheetFragment.setCallbackListener(this)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun setCalendar() {
        binding.apply {
            ordEnabled.apply {
                val currentDate = Calendar.getInstance()
                calendarView.date = currentDate.timeInMillis

                val maxDate = Calendar.getInstance()
                maxDate.add(Calendar.MONTH, +1)
                calendarView.maxDate = maxDate.timeInMillis

                val minDate = Calendar.getInstance()

                // Verifica se è oltre le 22:00
                if (currentDate.get(Calendar.HOUR_OF_DAY) >= 22) {
                    minDate.add(Calendar.DAY_OF_MONTH, 1) // Imposta la data minima al giorno successivo
                    simpleTextAdapterTime.submitList(viewModel.generateTimesList(minDate))
                } else {
                    simpleTextAdapterTime.submitList(viewModel.generateTimesList(Calendar.getInstance()))

                }

                calendarView.minDate = minDate.timeInMillis

                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    viewModel.selectedDate = formattedDate
                    simpleTextAdapterTime.submitList(viewModel.generateTimesList(selectedDate))
                    if (selectedDate.before(currentDate)) {
                        calendarView.dateTextAppearance = R.style.PreviousDayTextStyle
                    } else {
                        calendarView.dateTextAppearance = R.style.NormalDayTextStyle
                    }
                }
            }
        }
    }


    private fun showReservationPreview() {
        val bottomSheetFragment = ReservationPreviewBottomSheet()

        val formattedDate = if (viewModel.selectedDate == null) {
            val timeStamp = binding.ordEnabled.calendarView.date
            val localDate =
                Instant.ofEpochMilli(timeStamp).atZone(ZoneId.systemDefault()).toLocalDate()

            viewModel.formatDate(localDate)
        } else viewModel.selectedDate
        formattedDate?.let {
            bottomSheetFragment.date = it
        }

        viewModel.selectedDate = formattedDate
        bottomSheetFragment.people = viewModel.numbersOfPeople
        viewModel.timeToOrder?.let {
            bottomSheetFragment.timeToOrder = it
        }
        viewModel.seggiolini = binding.ordEnabled.editSeggiolini.text.toString()
        bottomSheetFragment.seggiolini =
            if (
                viewModel.seggiolini.isNullOrBlank() ||
                viewModel.seggiolini.isNullOrEmpty()
            )
                0
            else
                viewModel.seggiolini?.toInt()

        viewModel.specialRequest = binding.ordEnabled.specialRequestEdit.text.toString()
        bottomSheetFragment.specialRequests = viewModel.specialRequest ?: "N/A"

        bottomSheetFragment.setCallbackListener(this)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun initSharedPreferences() {
        val cache = viewModel.getCachedData()
        val key = sharedPref.getString(Constants.PREF_KEY_MAIN_KEY, "")
        try {
            val selected = cache?.first { it.catKey == key }
            if (selected != null) {
                Constants.m_category = selected
            }
        }catch (e:Exception){

        }

    }

    override fun onSignInSuccessful(account: GoogleSignInAccount) {
        Toast.makeText(requireContext(), "Bentornato/a ${account.givenName}", Toast.LENGTH_SHORT)
            .show()
        firebaseAuthWithGoogle(account)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Timber.d("Firebase authentication success: $user")
                    saveGoogleSignInInfo(account, user?.uid)
                    canDoReservation()
                } else {
                    Timber.d("Firebase authentication failed", task.exception)
                }
            }
    }

    private fun canDoReservation() {
        viewModel.restaurantCanMakeReservation { canShowReservation ->

            binding.apply {
                if (FirebaseAuth.getInstance().currentUser != null)
                    layerNoReservation.isVisible = canShowReservation == "nope"
                ordEnabled.ordLayout.isVisible = canShowReservation != "nope"
            }
        }
    }

    private fun saveGoogleSignInInfo(account: GoogleSignInAccount, uid: String?) {
        val editor = googleSharedPref.edit()

        editor.putString(Constants.GOOGLE_MAIL, account.email)
        editor.putString(Constants.GOOGLE_NAME, account.givenName)
        editor.putString(Constants.GOOGLE_FAMILY_NAME, account.familyName)
        editor.putString(Constants.GOOGLE_ACCOUNT, account.account.toString())
        editor.putString(Constants.GOOGLE_ACCOUNT_UID, uid)

        editor.apply()

        binding.layerLogIn.isVisible = false

        Timber.d("Google Sign In info saved to SharedPreferences.")
    }

    override fun onConfirmReservation() {
        try {
            val email = googleSharedPref.getString(Constants.GOOGLE_MAIL, "N/A")
            val name = googleSharedPref.getString(Constants.GOOGLE_NAME, "N/A")
            val surname = googleSharedPref.getString(Constants.GOOGLE_FAMILY_NAME, "N/A")

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = viewModel.selectedDate?.let { dateFormat.parse(it) }
            val calendar = Calendar.getInstance()
            if (date != null) {
                calendar.time = date
            }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val formattedMonth = if (month < 10) "0$month" else month.toString()
            val formattedDay = if (day < 10) "0$day" else day.toString()


            val reservation = Reservation(
                nameSurname = "$name $surname",
                surname = surname.toString(),
                email = email ?: "N/A",
                year = year.toString(),
                month = formattedMonth,
                day = formattedDay,
                hour = viewModel.timeToOrder.toString(),
                numberOfPeople = viewModel.numbersOfPeople.toString() + " Adulti",
                request = viewModel.specialRequest ?: "N/A",
                seggiolini = viewModel.seggiolini ?: "N/A"
            )
            val userID = sharedPref.getString(Constants.FIREBASE_NODE_Auth, "AAA") ?: "AAA"

            viewModel.confirmReservation(reservation, userID) {

                Toast.makeText(
                    requireContext(),
                    "Confermato!",
                    Toast.LENGTH_SHORT
                ).show()

                clearAllData(calendar)
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Qualcosa è andato storto, riprova più tardi",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun clearAllData(calendar: Calendar) {
        Utils.hideKeyboard(requireActivity())
        simpleTextAdapterNumbersPeople.clearSelection()
        simpleTextAdapterTime.clearSelection()
        binding.apply {
            ordEnabled.apply {
                calendarView.date = calendar.timeInMillis
                specialRequestEdit.setText("")
                specialRequestEdit.hint =
                    ContextCompat.getString(requireContext(), R.string.scrivi)
                editSeggiolini.setText("0")
                editSeggiolini.hint = "0"
            }
        }

        viewModel.clearData()
    }
}