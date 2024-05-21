package celo.urestaurants.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.databinding.ItemNumberBinding
import celo.urestaurants.models.SimpleTextModel

class SimpleTextAdapter(
    private val onClickListener: (SimpleTextModel) -> Unit
) :
    ListAdapter<SimpleTextModel, SimpleTextAdapter.ItemViewHolder>(DiffCallback()) {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            handleItemClick(position, item)
        }
        holder.bind(item, position, selectedPosition)
    }

    inner class ItemViewHolder(private val binding: ItemNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SimpleTextModel, position: Int, selectedPosition: Int) {
            binding.textValue.text = item.titleText

            if (position == selectedPosition) {
                binding.layout.setBackgroundColor(itemView.context.getColor(R.color.blue))
                binding.textValue.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                binding.layout.setBackgroundColor(itemView.context.getColor(R.color.white))
                binding.textValue.setTextColor(itemView.context.getColor(R.color.blue))
            }

            binding.executePendingBindings()
        }
    }

    private fun handleItemClick(position: Int, item: SimpleTextModel) {
        val previousSelected = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedPosition)

        onClickListener.invoke(item)
    }

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION

        notifyItemChanged(previousSelected)
    }

    class DiffCallback : DiffUtil.ItemCallback<SimpleTextModel>() {
        override fun areItemsTheSame(oldItem: SimpleTextModel, newItem: SimpleTextModel): Boolean {
            return oldItem.titleText == newItem.titleText
        }

        override fun areContentsTheSame(
            oldItem: SimpleTextModel,
            newItem: SimpleTextModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
