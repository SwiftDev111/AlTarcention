package celo.urestaurants.adapters

import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import celo.urestaurants.R
import celo.urestaurants.models.CategoryModel


class CategoryAdapter(
    val mActivity: Activity,
    var catClickListener: (position: Int, _filters: MutableList<String>?) -> Unit
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val categories: MutableList<CategoryModel>
    private var attrModels: MutableList<AttrModel>
    private var filters: MutableList<String>

    init {
        filters = mutableListOf()
        categories = mutableListOf()
        attrModels = mutableListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val catView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_adapter_category, parent, false)
        return ViewHolder(catView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.txtAdapterCat.text = attrModels[position].title
            val chk = checkFilter(holder.txtAdapterCat, holder.lltCatAdapter, position)
            holder.txtAdapterCat.setOnClickListener { v: View? ->
                val chk1 = checkFilter(holder.txtAdapterCat, holder.lltCatAdapter, position)
                if (chk1) {
                    filters.remove(attrModels[position].title)
                    val chk2 = checkFilter(holder.txtAdapterCat, holder.lltCatAdapter, position)
                    catClickListener.invoke(position, filters)
                } else {
                    filters.add(attrModels[position].title)
                    val chk2 = checkFilter(holder.txtAdapterCat, holder.lltCatAdapter, position)
                    catClickListener.invoke(position, filters)
                }
            }
        } catch (e: Exception) {

        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkFilter(txtView: TextView, llt: LinearLayout, _position: Int): Boolean {
        val chk = filters.contains(attrModels[_position].title)
        if (chk) {
//            txtView.setTextColor(mActivity.getResources().getColor(R.color.blue));
            llt.background = mActivity.resources.getDrawable(R.drawable.rounded_secondary_50)
        } else {
//            txtView.setTextColor(mActivity.getResources().getColor(R.color.white));
            llt.background = mActivity.resources.getDrawable(R.drawable.rounded_primary_50)
        }
        return chk
    }

    override fun getItemCount(): Int {
        return attrModels.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtAdapterCat: TextView
        var lltCatAdapter: LinearLayout

        init {
            txtAdapterCat = itemView.findViewById(R.id.txt_catadapter_cat)
            lltCatAdapter = itemView.findViewById(R.id.llt_catadapter)
        }
    }

    fun refreshData(_categories: MutableList<CategoryModel>?) {
        categories.clear()
        categories.addAll(_categories!!)
        filters = mutableListOf()
        attrModels = mutableListOf()
        val atrs = mutableListOf<String>()
        for (i in categories.indices) {
            val category = categories[i]
            val atr1Val = category.catInfo.atr1
            val atr2Val = category.catInfo.atr2
            val isActive = category.catInfo.isActive
            if (isActive) {
                if (atr1Val != "") {
                    if (atrs.size == 0) {
                        atrs.add(atr1Val)
                        val attrModel = AttrModel()
                        attrModel.setTitleAndCategory(atr1Val, category)
                        attrModels.add(attrModel)
                    } else {
                        if (!atrs.contains(atr1Val)) {
                            atrs.add(atr1Val)
                            val attrModel = AttrModel()
                            attrModel.setTitleAndCategory(atr1Val, category)
                            attrModels.add(attrModel)
                        }
                    }
                }
                if (atr2Val != "") {
                    if (atrs.size == 0) {
                        atrs.add(atr2Val)
                        val attrModel = AttrModel()
                        attrModel.setTitleAndCategory(atr2Val, category)
                        attrModels.add(attrModel)
                    } else {
                        if (!atrs.contains(atr2Val)) {
                            atrs.add(atr2Val)
                            val attrModel = AttrModel()
                            attrModel.setTitleAndCategory(atr2Val, category)
                            attrModels.add(attrModel)
                        }
                    }
                }
            }
        }
    }

    interface CatClickListener {
        fun onCategoryClicked(position: Int, _filters: MutableList<String>?)
    }
}

internal class AttrModel {
    var title = ""
    var category: CategoryModel

    init {
        category = CategoryModel()
    }

    fun setTitleAndCategory(_title: String, cat: CategoryModel) {
        title = _title
        category = cat
    }
}