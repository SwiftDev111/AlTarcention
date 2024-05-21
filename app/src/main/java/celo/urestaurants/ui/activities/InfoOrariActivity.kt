package celo.urestaurants.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import celo.urestaurants.databinding.ActivityInfoOrariBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import java.util.Calendar

class InfoOrariActivity : AppCompatActivity() {
    private var binding: ActivityInfoOrariBinding? = null
    var lunedi = 0
    var martedi = 0
    var mercoledi = 0
    var giovedi = 0
    var venerdi = 0
    var sabato = 0
    var domenica = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInfoOrariBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        var month = calendar[Calendar.MONTH]
        val nDay = calendar[Calendar.DAY_OF_MONTH]
        val giorniDaInizio = calendar[Calendar.DAY_OF_YEAR]
        month += 1 //perchè si conta da zero
        println("Minimum number of days in week: $nDay")
        Timber.d(
            "Prova Giorno" + day + "  Giorno  " + nDay + "   Mese " + month + "Giorno da inziio :" + giorniDaInizio
        )
        when (day) {
            Calendar.SUNDAY -> {
                binding!!.dom.setTypeface(null, Typeface.BOLD)
                binding!!.dom.textSize = 17f
                lunedi = giorniDaInizio - 6
                martedi = giorniDaInizio - 5
                mercoledi = giorniDaInizio - 4
                giovedi = giorniDaInizio - 3
                venerdi = giorniDaInizio - 2
                sabato = giorniDaInizio - 1
                domenica = giorniDaInizio
            }

            Calendar.MONDAY -> {
                binding!!.lun.setTypeface(null, Typeface.BOLD)
                binding!!.lun.textSize = 17f
                lunedi = giorniDaInizio
                martedi = giorniDaInizio + 1
                mercoledi = giorniDaInizio + 2
                giovedi = giorniDaInizio + 3
                venerdi = giorniDaInizio + 4
                sabato = giorniDaInizio + 5
                domenica = giorniDaInizio + 6
            }

            Calendar.TUESDAY -> {
                binding!!.mar.setTypeface(null, Typeface.BOLD)
                binding!!.mar.textSize = 17f
                lunedi = giorniDaInizio - 1
                martedi = giorniDaInizio
                mercoledi = giorniDaInizio + 1
                giovedi = giorniDaInizio + 2
                venerdi = giorniDaInizio + 3
                sabato = giorniDaInizio + 4
                domenica = giorniDaInizio + 5
            }

            Calendar.WEDNESDAY -> {
                binding!!.mer.setTypeface(null, Typeface.BOLD)
                binding!!.mer.textSize = 17f
                lunedi = giorniDaInizio - 2
                martedi = giorniDaInizio - 1
                mercoledi = giorniDaInizio
                giovedi = giorniDaInizio + 1
                venerdi = giorniDaInizio + 2
                sabato = giorniDaInizio + 3
                domenica = giorniDaInizio + 4
            }

            Calendar.THURSDAY -> {
                binding!!.gio.setTypeface(null, Typeface.BOLD)
                binding!!.gio.textSize = 17f
                lunedi = giorniDaInizio - 3
                martedi = giorniDaInizio - 2
                mercoledi = giorniDaInizio - 1
                giovedi = giorniDaInizio
                venerdi = giorniDaInizio + 1
                sabato = giorniDaInizio + 2
                domenica = giorniDaInizio + 3
            }

            Calendar.FRIDAY -> {
                binding!!.ven.setTypeface(null, Typeface.BOLD)
                binding!!.ven.textSize = 17f
                lunedi = giorniDaInizio - 4
                martedi = giorniDaInizio - 3
                mercoledi = giorniDaInizio - 2
                giovedi = giorniDaInizio - 1
                venerdi = giorniDaInizio
                sabato = giorniDaInizio + 1
                domenica = giorniDaInizio + 2
            }

            Calendar.SATURDAY -> {
                binding!!.sab.setTypeface(null, Typeface.BOLD)
                binding!!.sab.textSize = 17f
                lunedi = giorniDaInizio - 5
                martedi = giorniDaInizio - 4
                mercoledi = giorniDaInizio - 3
                giovedi = giorniDaInizio - 2
                venerdi = giorniDaInizio - 1
                sabato = giorniDaInizio
                domenica = giorniDaInizio + 1
            }
        }
        binding!!.Maps.setOnClickListener { v: View? ->
            val uri =
                "https://www.google.com/maps/place/Al+Tarcentino/@46.2139564,13.2167624,17z/data=!3m1!4b1!4m8!3m7!1s0x477a39e23e92b9b3:0x963581dfcf397eb3!5m2!4m1!1i2!8m2!3d46.2139527!4d13.2189511"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("000-Orari")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                val valueLun = dataSnapshot.child(lunedi.toString()).getValue(
                    String::class.java
                )
                val valueMar = dataSnapshot.child(martedi.toString()).getValue(
                    String::class.java
                )
                val valueMer = dataSnapshot.child(mercoledi.toString()).getValue(
                    String::class.java
                )
                val valueGio = dataSnapshot.child(giovedi.toString()).getValue(
                    String::class.java
                )
                val valueVen = dataSnapshot.child(venerdi.toString()).getValue(
                    String::class.java
                )
                val valueSab = dataSnapshot.child(sabato.toString()).getValue(
                    String::class.java
                )
                val valueDom = dataSnapshot.child(domenica.toString()).getValue(
                    String::class.java
                )
                if (valueLun != null) {
                    binding!!.lun.text = valueLun
                    binding!!.lun.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueLun)
                }
                if (valueMar != null) {
                    binding!!.mar.text = valueMar
                    binding!!.mar.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueMar)
                }
                if (valueMer != null) {
                    binding!!.mer.text = valueMer
                    binding!!.mer.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueMer)
                }
                if (valueGio != null) {
                    binding!!.gio.text = valueGio
                    binding!!.gio.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueGio)
                }
                if (valueVen != null) {
                    binding!!.ven.text = valueVen
                    binding!!.ven.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueVen)
                }
                if (valueSab != null) {
                    binding!!.sab.text = valueSab
                    binding!!.sab.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueSab)
                }
                if (valueDom != null) {
                    binding!!.dom.text = valueDom
                    binding!!.dom.setTextColor(Color.parseColor("#DA3B25"))
                    Timber.d("PROVA", valueDom)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w("Prova Giorno Failed to read value.", error.toException())
            }
        })
        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
    }
}