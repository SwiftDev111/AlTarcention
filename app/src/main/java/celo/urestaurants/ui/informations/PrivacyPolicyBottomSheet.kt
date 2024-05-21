package celo.urestaurants.ui.informations

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import celo.urestaurants.R
import celo.urestaurants.constants.Constants.TERMS_AND_CONDITIONS
import celo.urestaurants.constants.Constants.TERMS_PREFERENCE
import celo.urestaurants.databinding.FragmentBottomSheetTermsAndConditionsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PrivacyPolicyBottomSheet @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetTermsAndConditionsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetTermsAndConditionsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@PrivacyPolicyBottomSheet
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(TERMS_PREFERENCE, Context.MODE_PRIVATE)

        binding.apply {
            val webSettings = custmWebView.settings
            webSettings.javaScriptEnabled = true

            custmWebView.loadUrl("file:///android_asset/privacypolicy.html")

            custmWebView.webViewClient = WebViewClient()

            val switchState = sharedPreferences.getBoolean(TERMS_AND_CONDITIONS, false)
            switchAcceptTerms.isChecked = switchState
            btnConfirm.isEnabled = switchState
            updateButtonAppearance(switchState)

            switchAcceptTerms.setOnCheckedChangeListener { _, isChecked ->
                btnConfirm.isEnabled = isChecked
                updateButtonAppearance(isChecked)

                sharedPreferences.edit().putBoolean(TERMS_AND_CONDITIONS, isChecked).apply()
            }

            btnConfirm.setOnClickListener {
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

    private fun updateButtonAppearance(isChecked: Boolean) {
        binding.apply {
            if (isChecked) {
                btnConfirm.isClickable = true
                btnConfirm.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            } else {
                btnConfirm.isClickable = false
                btnConfirm.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        }
    }
}
