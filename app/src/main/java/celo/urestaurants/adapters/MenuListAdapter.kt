package celo.urestaurants.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.databinding.ListRowBinding
import celo.urestaurants.databinding.ListRowSectionBinding
import celo.urestaurants.models.MenuAdapterItem
import timber.log.Timber
import java.util.Locale

class MenuListAdapter(
    private val language: String,
    private val onClickListener: (MenuAdapterItem.CatItem) -> Unit
) :
    ListAdapter<MenuAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SECTION -> SectionViewHolder(
                ListRowSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_ITEM -> ItemViewHolder(
                ListRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SECTION -> {
                val section = getItem(position) as MenuAdapterItem.Section
                (holder as SectionViewHolder).bind(section)
            }

            VIEW_TYPE_ITEM -> {
                val item = (getItem(position) as MenuAdapterItem.CatItem)
                (holder as ItemViewHolder).bind(item)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuAdapterItem.CatItem) {
            binding.apply {
                val dishTitle = newDishTitle(item)
                titolo.text = dishTitle

                val ingredient = newIngredientTitle(item)
                ingredienti.text = ingredient

                prezzo.text = itemView.context.getString(R.string.euro_format, item.priceD)
                if (item.priceM?.isNotEmpty() == true || item.priceM?.isNotBlank() == true) {
                    prezzoM.text = itemView.context.getString(R.string.euro_format, item.priceM)
                } else {
                    prezzoM.isVisible = false
                }

                if (ingredient == "") {
                    ingredienti.visibility = View.GONE
                } else
                    ingredienti.visibility = View.VISIBLE


                if (item.isFirst && item.isLast) {
                    val gradientDrawable = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.rounded_white_topbottom_12
                    )
                    layout.setImageDrawable(gradientDrawable)

                } else if (item.isLast) {
                    val gradientDrawable = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.rounded_white_bottom_12
                    )
                    layout.setImageDrawable(gradientDrawable)
                } else if (item.isFirst) {
                    val gradientDrawable =
                        ContextCompat.getDrawable(itemView.context, R.drawable.rounded_white_top_12)
                    layout.setImageDrawable(gradientDrawable)
                } else {
                    val gradientDrawable =
                        ContextCompat.getDrawable(itemView.context, R.drawable.no_rounded_white)
                    layout.setImageDrawable(gradientDrawable)
                }

                listLayout.setOnClickListener {
                    Timber.d("XXXX Click language: ${item.language}")

                    val deviceLanguage = Locale.getDefault().language

                    if (item.language == deviceLanguage) {
                        item.language = "it"
                        titolo.text = item.nome
                        ingredienti.text = item.ingredienti
                    } else {
                        item.language = deviceLanguage
                        val newDishTitle = newDishTitle(item)
                        titolo.text = newDishTitle

                        val newIngredient = newIngredientTitle(item)
                        ingredienti.text = newIngredient
                    }
                    onClickListener.invoke(item)
                }
                executePendingBindings()
            }

        }

    }

    inner class SectionViewHolder(private val binding: ListRowSectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuAdapterItem.Section) {
            binding.apply {
                if (item.title.isEmpty() || item.title.isBlank()) {
                    sectionLayout.visibility = View.GONE
                } else {
                    sectionLayout.visibility = View.VISIBLE
                    titolo.text = item.title
                }
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MenuAdapterItem.Section -> VIEW_TYPE_SECTION
            is MenuAdapterItem.CatItem -> VIEW_TYPE_ITEM
        }
    }

    companion object {
        private const val VIEW_TYPE_SECTION = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private fun newDishTitle(item: MenuAdapterItem.CatItem) = when (language) {
        "ru" -> if (item.nomeRu?.isNotEmpty() == true) item.nomeRu else item.nome
        "de" -> if (item.nomeDe?.isNotEmpty() == true) item.nomeDe else item.nome
        "en" -> if (item.nomeEn?.isNotEmpty() == true) item.nomeEn else item.nome
        "es" -> if (item.nomeEs?.isNotEmpty() == true) item.nomeEs else item.nome
        "fr" -> if (item.nomeFr?.isNotEmpty() == true) item.nomeFr else item.nome
        "zh" -> if (item.nomeZh?.isNotEmpty() == true) item.nomeZh else item.nome
        else -> item.nome
    }

    private fun newIngredientTitle(item: MenuAdapterItem.CatItem) = when (language) {
        "ru" -> if (item.ingredientiRu?.isNotEmpty() == true) item.ingredientiRu else item.ingredienti
        "de" -> if (item.ingredientiDe?.isNotEmpty() == true) item.ingredientiDe else item.ingredienti
        "en" -> if (item.ingredientiEn?.isNotEmpty() == true) item.ingredientiEn else item.ingredienti
        "es" -> if (item.ingredientiEs?.isNotEmpty() == true) item.ingredientiEs else item.ingredienti
        "fr" -> if (item.ingredientiFr?.isNotEmpty() == true) item.ingredientiFr else item.ingredienti
        "zh" -> if (item.ingredientiZh?.isNotEmpty() == true) item.ingredientiZh else item.ingredienti
        else -> item.ingredienti
    }

    class DiffCallback : DiffUtil.ItemCallback<MenuAdapterItem>() {
        override fun areItemsTheSame(oldItem: MenuAdapterItem, newItem: MenuAdapterItem): Boolean {
            return when {
//                oldItem is MenuAdapterItem.Section && newItem is MenuAdapterItem.Section ->
//                    oldItem.title == newItem.title

                oldItem is MenuAdapterItem.CatItem && newItem is MenuAdapterItem.CatItem ->
                    oldItem.key == newItem.key

                else ->
                    false
            }
        }

        override fun areContentsTheSame(
            oldItem: MenuAdapterItem,
            newItem: MenuAdapterItem
        ): Boolean {
            return when {
//                oldItem is MenuAdapterItem.Section && newItem is MenuAdapterItem.Section ->
//                    oldItem.title == newItem.title

                oldItem is MenuAdapterItem.CatItem && newItem is MenuAdapterItem.CatItem ->
                    oldItem == newItem

                else ->
                    false
            }
        }
    }

}


