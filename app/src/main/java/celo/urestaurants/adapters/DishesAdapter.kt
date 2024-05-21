package celo.urestaurants.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.databinding.LayoutAdapterDishBinding
import celo.urestaurants.models.DishModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DishesAdapter(
    private val onClickListener: (DishModel) -> Unit
) :
    ListAdapter<DishModel, DishesAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutAdapterDishBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        setEnterAnimation(holder.itemView)

    }

    inner class ItemViewHolder(private val binding: LayoutAdapterDishBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DishModel) {
            binding.title.text = item.title

            Glide.with(binding.root.context)
                .load(item.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.image)

            binding.card.setOnClickListener {
                onClickListener.invoke(item)
            }

            binding.executePendingBindings()
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<DishModel>() {
        override fun areItemsTheSame(oldItem: DishModel, newItem: DishModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: DishModel, newItem: DishModel): Boolean {
            return oldItem == newItem
        }
    }

    private fun setEnterAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_right)
        animation.duration = 500
        view.startAnimation(animation)
    }
}


