package celo.urestaurants.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.models.CatFavoriteModel
import celo.urestaurants.models.CategoryModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SearchCatAdapter(
    private val deviceId: String,
    var searchCatClickListener: (position: Int, category: CategoryModel)->Unit
) : RecyclerView.Adapter<SearchCatAdapter.ViewHolder>() {
    private val cats: MutableList<CategoryModel> = mutableListOf()
    private val mDatabase: DatabaseReference

    init {
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val catView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_adapter_search_cat, parent, false)
        return ViewHolder(catView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = cats[position].catInfo.nome
        holder.txtSubTitle.text = cats[position].catInfo.atr1 + " • " + cats[position].catInfo.atr2
        holder.txtStar.text = cats[position].catInfo.star
        val favChk = getFavCheck(position)
        if (favChk) {
            holder.imgHart.setBackgroundResource(R.drawable.heart_filled)
        } else {
            holder.imgHart.setBackgroundResource(R.drawable.heart_empty)
        }
        holder.imgHart.setOnClickListener { v: View? ->
            val favChk1 = getFavCheck(position)
            val catFav = CatFavoriteModel()
            catFav.deviceId = deviceId
            if (favChk1) {
                holder.imgHart.setBackgroundResource(R.drawable.heart_empty)
                mDatabase.child(cats[position].catKey!!).child("Favorites").child(deviceId)
                    .removeValue()
                var indx = -1
                for (i in cats[position].favorites.indices) {
                    if (cats[position].favorites[i].deviceId == deviceId) {
                        indx = i
                    }
                }
                if (indx != -1) {
                    cats[position].favorites.removeAt(indx)
                }
            } else {
                catFav.favorite = "true"
                cats[position].favorites.add(catFav)
                holder.imgHart.setBackgroundResource(R.drawable.heart_filled)
                mDatabase.child(cats[position].catKey!!).child("Favorites").child(deviceId)
                    .setValue(catFav)
            }
        }
        holder.cardView.setOnClickListener { v: View? ->
            searchCatClickListener.invoke(
                position,
                cats[position]
            )
        }
        Glide.with(holder.itemView.context)
            .load(cats[position].catInfo.logoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imgRestoLogo)
    }

    private fun getFavCheck(_position: Int): Boolean {
        var favChk = false
        for (i in cats[_position].favorites.indices) {
            if (cats[_position].favorites[i].deviceId == deviceId) {
                favChk = true
            }
        }
        return favChk
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTitle: TextView
        var txtSubTitle: TextView
        var txtStar: TextView
        var imgRestoLogo: ImageView
        var imgHart: ImageView
        var cardView: CardView

        init {
            txtTitle = itemView.findViewById(R.id.txt_searchcat_title)
            txtSubTitle = itemView.findViewById(R.id.txt_searchcat_subtitle)
            txtStar = itemView.findViewById(R.id.txt_searchcat_star)
            imgRestoLogo = itemView.findViewById(R.id.img_searchcat_logo)
            imgHart = itemView.findViewById(R.id.img_searchcat_hart)
            cardView = itemView.findViewById(R.id.card_searchcat)
        }
    }


}
