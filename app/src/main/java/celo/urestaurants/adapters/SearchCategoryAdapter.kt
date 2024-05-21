package celo.urestaurants.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.databinding.LayoutAdapterSearchCatBinding
import celo.urestaurants.models.CategoryModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage

class SearchCategoryAdapter(val deviceId: String, private val onClickListener: (CategoryModel) -> Unit, private val favouriteClick: (CategoryModel, Boolean) -> Unit) :
    ListAdapter<CategoryModel, SearchCategoryAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutAdapterSearchCatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.invoke(item)
        }
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: LayoutAdapterSearchCatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryModel) {
            binding.txtSearchcatTitle.text = item.catInfo.nome
            val subtitle = "${item.catInfo.atr1} • ${item.catInfo.atr2}"
            binding.txtSearchcatSubtitle.text = subtitle
            binding.txtSearchcatStar.text = item.catInfo.star

            val favChk = getFavCheck(item)
            if (favChk) {
                binding.imgSearchcatHart.setBackgroundResource(R.drawable.heart_filled)
            } else {
                binding.imgSearchcatHart.setBackgroundResource(R.drawable.heart_empty)
            }

            binding.imgSearchcatHart.setOnClickListener {
                favouriteClick.invoke(item,favChk)
                if (favChk) {
                    binding.imgSearchcatHart.setBackgroundResource(R.drawable.heart_empty)
                } else {
                    binding.imgSearchcatHart.setBackgroundResource(R.drawable.heart_filled)
                }
            }

//            Glide.with(binding.root.context)
//                .load(item.catInfo.logoUrl)
//                .diskCacheStrategy(DiskCacheStrategy.ALL) // Usa una cache su disco
//                .into(binding.imgSearchcatLogo)

            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference.child("ImagePlaces/") // Make sure to include the trailing slash

            val catID = item.catKey
            val extractedfile = ".jpg"

            val imagePath = storageReference.child(catID + extractedfile)
            //Log.d("imagePath", "bind: "+imagePath)

            imagePath.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString() // This will give you the URL in the format you mentioned
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.imgSearchcatLogo)
            }.addOnFailureListener {
                // Handle any errors
            }

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
}


