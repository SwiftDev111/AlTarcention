package celo.urestaurants.ui.ordinazioni

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import celo.urestaurants.constants.Constants
import celo.urestaurants.constants.Constants.GOOGLE_SIGN_IN
import celo.urestaurants.databinding.FragmentBottomSheetReservationPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReservationPreviewBottomSheet @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetReservationPreviewBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var callbackListener: ReservationPreviewBottomSheetListener

    var date: String = "N/A"
    var timeToOrder: String = "N/A"
    var specialRequests: String = "N/A"
    var seggiolini: Int? = 0
    var people: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentBottomSheetReservationPreviewBinding.inflate(inflater, container, false).apply {
                lifecycleOwner = this@ReservationPreviewBottomSheet

            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(GOOGLE_SIGN_IN, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(Constants.GOOGLE_MAIL, "N/A")
        val name = sharedPreferences.getString(Constants.GOOGLE_NAME, "N/A")

        binding.apply {
            nameText.text = name
            emailText.text = email
            dayText.text = date
            hourText.text = timeToOrder
            peopleText.text = "${people} Adulti"
            specialRequestTextValue.text = specialRequests
            numberOfSeggiolini.text = seggiolini.toString()

            btnConfirm.setOnClickListener {
                callbackListener.onConfirmReservation()
                dismiss()
            }

        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.setCanceledOnTouchOutside(false)
        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    fun setCallbackListener(listener: ReservationPreviewBottomSheetListener) {
        callbackListener = listener
    }
}
