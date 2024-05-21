package celo.urestaurants.ui.informations

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import celo.urestaurants.R
import celo.urestaurants.databinding.FragmentBottomSheetBusinessBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendEmailBottomSheet @Inject constructor() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBusinessBinding

    var subject: String = ""
    var text: String = ""
    var title: String? = null
    var description: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBusinessBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@SendEmailBottomSheet

            editEmail.addTextChangedListener(emailTextWatcher)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            title?.let {
                txtTitle.text = it
            }
            description?.let {
                txtDescription.text = it
            }

            btnSend.setOnClickListener {
                val emailAddress = editEmail.text.toString()
                sendEmail(emailAddress)
                dismiss()
            }
        }
    }

    private val emailTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val isValidEmail = isValidEmail(s.toString())
            updateButtonAppearance(isValidEmail)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun updateButtonAppearance(isValidEmail: Boolean) {
        binding.apply {
            if (isValidEmail) {
                btnSend.isClickable = true
                btnSend.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            } else {
                btnSend.isClickable = false
                btnSend.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey
                    )
                )
            }
        }
    }

    private fun sendEmail(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse(getString(R.string.mailto_business_urestaurants_app))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.entra_a_far_parte_di_urestaurant))
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(intent)
    }
}