package celo.urestaurants.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.constants.Constants
import celo.urestaurants.databinding.LayoutAdapterLocationBinding
import celo.urestaurants.models.CategoryModel
import celo.urestaurants.models.Pictures
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.io.File


class LocationAdapter(
    val context: Context,
    val deviceId: String,
    private val onClickListener: (CategoryModel) -> Unit,
    private val favouriteClick: (CategoryModel, Boolean) -> Unit
) :
    ListAdapter<CategoryModel, LocationAdapter.ItemViewHolder>(DiffCallback()) {
    var city = ""
    var pictures = ArrayList<Pictures>()
    private var positionPictures: Pictures? = null

    var storage = FirebaseStorage.getInstance()
    private var mStorageRef: StorageReference? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.d("position", "onBindViewHolder:${viewType}")
        return ItemViewHolder(
            LayoutAdapterLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        try {
            positionPictures = pictures.getOrNull(position)
            Log.d("pictures_size", "pictures.size: " + pictures.size)
            Log.d("pictures_size", "positionPictures: " + positionPictures)
        } catch (e: Exception) {

        }

        // val imageUrl = item.catInfo.uriList[position]

//        Log.d("position", "onBindViewHolder:${position}")
//        if (item.catInfo.logoUrl[position] == null) "https://firebasestorage.googleapis.com/v0/b/urestaurants-ebb27.appspot.com/o/ImagePlaces%2F015.jpg?alt=media&token=dcab56c3-5024-4fc2-a032-ecc435a30a11"

        // Log.d("catINFOcatINFO", "onBindViewHolder: "+item.catInfo.logoUrl.get(holder.adapterPosition))

        holder.itemView.setOnClickListener {
            onClickListener.invoke(item)
        }
        holder.bind(item)

        val sharedPref =
            holder.itemView.context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        city = sharedPref.getString("selectedText", "")!!

        if (city!!.isNotEmpty()) {
            if (item.catInfo.city == city || city == "All") {
                holder.itemView.visibility = View.VISIBLE
                setEnterAnimation(holder.itemView.rootView, position)
                updateItems(currentList)
            } else {
                holder.itemView.visibility = View.GONE
                updateItems(currentList)
            }
        } else {
            setEnterAnimation(holder.itemView.rootView, position)
        }
    }

    inner class ItemViewHolder(private val binding: LayoutAdapterLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryModel) {
            Log.d("CATINFO", "bind: ${item.catInfo.nome}" + "//" + "${item.catInfo.isActive}")

            //var id = item.catInfo.catId
            // val originalUrl = "https://firebasestorage.googleapis.com/v0/b/urestaurants-ebb27.appspot.com/o/ImagePlaces%2FID.jpg?alt=media&token=7115d4f3-654e-49f1-8f9b-3f6e97bf1abf15"
            //val modifiedUrl = originalUrl.replace("ID", id)
            // Log.d("Modified", "bind: $modifiedUrl")

            binding.txtTitle.text = item.catInfo.nome
            val subtitle = "${item.catInfo.atr1} • ${item.catInfo.atr2}"
            binding.txtSubtitle.text = subtitle
            binding.txtStar.text = item.catInfo.star

            val favChk = getFavCheck(item)
            if (favChk) {
                binding.imgHart.setBackgroundResource(R.drawable.heart_filled)
            } else {
                binding.imgHart.setBackgroundResource(R.drawable.heart_empty)
            }

            binding.imgHart.setOnClickListener {
                favouriteClick.invoke(item, favChk)
                if (favChk) {
                    binding.imgHart.setBackgroundResource(R.drawable.heart_empty)
                } else {
                    binding.imgHart.setBackgroundResource(R.drawable.heart_filled)
                }
            }


            val imageUrl = Constants.urlImages.getOrNull(position)
            val id = imageUrl
                ?.substringAfterLast("/")  // Get the part after the last "/"
                ?.substringBefore(".")      // Get the part before the "."
                ?.replace("%2F", "")       // Remove the URL encoding "%2F"
                ?: ""

            val id1 = id // Example value of id
            val index = id1.substringAfter("ImagePlaces").toIntOrNull() // Extract numeric part as Int
            val indexString = index?.toString() ?: "" // Convert Int to String
            Log.d("Extracted_ID", "ID: $id")
            Log.d("Extracted_ID", "indexString ID: $indexString")

            var newID = "0$indexString"
            Log.d("Extracted_ID", "newID: $newID")

            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference.child("ImagePlaces/")

            val catID = item.catKey
            val extractedfile = ".jpg"

            val imagePath = storageReference.child(catID + extractedfile)
            //Log.d("imagePath", "bind: "+imagePath)

            imagePath.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString() // This will give you the URL in the format you mentioned
                // Use the imageUrl with Glide to load the image
                Log.d("imagePath", "imageUrl: "+imageUrl)
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.imgLogo)
            }.addOnFailureListener {
                // Handle any errors
            }


//            if (newID == item.catKey) {
//                Glide.with(binding.root.context)
//                    .load(Constants.urlImages.getOrNull(position) ?: item.catInfo.logoUrl)
//                    .into(binding.imgLogo)
//                Log.d("URLDATA", "bind: IFF")
//            } else {
//                Glide.with(binding.root.context)
//                    .load(item.catInfo.logoUrl)
//                    .into(binding.imgLogo)
//                Log.d("URLDATA", "bind: ELSE")
//            }

            binding.executePendingBindings()
        }

        private fun getFavCheck(item: CategoryModel): Boolean {
            return item.favorites.any { it.deviceId == deviceId }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.catKey == newItem.catKey
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }
    }

    private fun setEnterAnimation(view: View, position: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_right)
        animation.duration = 500
        view.startAnimation(animation)
    }

    fun updateItems(newItems: List<CategoryModel>) {
        val filteredItems = filterItems(newItems)
        submitList(filteredItems)
    }

    // Function to filter items based on the selected city
    private fun filterItems(items: List<CategoryModel>): List<CategoryModel> {
        val sharedPref = context.getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val city = sharedPref.getString("selectedText", "") ?: ""

        return if (city.isNotEmpty() && city != "All") {
            items.filter { it.catInfo.city == city }
        } else {
            items
        }
    }

}