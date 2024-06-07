package celo.urestaurants.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.input.key.Key.Companion.G
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import celo.urestaurants.R
import celo.urestaurants.Utils.hideKeyboard
import celo.urestaurants.adapters.DishesAdapter
import celo.urestaurants.adapters.MenuListAdapter
import celo.urestaurants.constants.Constants
import celo.urestaurants.databinding.FragmentHomeBinding
import celo.urestaurants.models.CategoryModel
import celo.urestaurants.models.DishModel
import celo.urestaurants.models.MenuAdapterItem
import celo.urestaurants.models.ProductType
import com.google.android.play.integrity.internal.i
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject
import kotlin.math.log


@AndroidEntryPoint
class HomeFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var dishesAdapter: DishesAdapter
    private lateinit var menuListAdapter: MenuListAdapter
    var mIdPlace: String = ""
    var m_UpdatedTitle: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            val IdPlace = arguments?.getString("IdPlace")!!
            mIdPlace = IdPlace
            //Log.d("NAMENAME", "onCreateView: " + mIdPlace)
            val Title = arguments?.getString("UpdatedTitle")!!
            m_UpdatedTitle = Title
        } catch (e: Exception) {

        }


        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@HomeFragment

//            try{
                dishesAdapter = DishesAdapter { item ->
                    viewModel.selected = item
                    searchView.txtDashTitle.text = item.title
                    showMenu(item)
                }
                listDash.adapter = dishesAdapter
//            }catch (e:Exception){
//
//            }


            val language = viewModel.getLanguage()

            menuListAdapter = MenuListAdapter(language) { _ ->

            }

            menuListRecycler.adapter = menuListAdapter
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.item_separator)
                ?.let { divider.setDrawable(it) }
            menuListRecycler.addItemDecoration(divider)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.searchInputText.hint = getString(R.string.hint_search_menu)

        //sharedPref = requireActivity().getSharedPreferences(Constants.PREF_KEY_PARAM_KEY, Context.MODE_PRIVATE)

        initSharedPreferences()
        loadSectionTitles()
        createDishModelsAndSelectFirst()
        editTextObserver()
        setClickListeners()

    }

    private fun showMenu(item: DishModel) {
        val list = item.getList()
        list.forEach { it.language = viewModel.getLanguage() }
        val menuList = groupBySubCat(list).toList().flatMap { (subCat, items) ->
            listOf(MenuAdapterItem.Section(subCat)) + items
        }
        val newList = viewModel.assignFirstLastFlags(menuList)

        viewModel.menuList.clear()
        viewModel.menuList.addAll(newList)
        viewModel.startingList.clear()
        viewModel.startingList.addAll(newList)

        menuListAdapter.submitList(newList)
        binding.menuListRecycler.scrollToPosition(0)
        menuListAdapter.notifyDataSetChanged()
    }

    private fun initSharedPreferences() {
        sharedPref =
            requireActivity().getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE)
        val cache = viewModel.getCachedData()
        var mainTitle:String
        try {
             mainTitle = sharedPref.getString(Constants.PREF_KEY_MAIN_TITLE, "").toString()
            if (mIdPlace.isNotEmpty()) {
                if (mIdPlace == "01") {
                    mainTitle = "Al Tarcentino"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                } else if (mIdPlace == "02") {
                    mainTitle = "Pronto Pizza G. Bassa"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                } else if (mIdPlace == "03") {
                    mainTitle = "Albergo Riviera"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "04") {
                    mainTitle = "Pronto Pizza G. Alta"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "05") {
                    mainTitle = "Helios Pizza"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "06") {
                    mainTitle = "Da Turi"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "07") {
                    mainTitle = "Pizzeria Capriccio"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "08") {
                    mainTitle = "Lendar"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "09") {
                    mainTitle = "Tellme Pizza"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "010") {
                    mainTitle = "Sofias's Bakery"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "011") {
                    mainTitle = "Caffe Pittini"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "012") {
                    mainTitle = "Tellme Pizza"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }

                else if (mIdPlace == "013") {
                    mainTitle = "Pizze e Delizie"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "014") {
                    mainTitle = "Ostaria Boccadoro"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }

                else if (mIdPlace == "015") {
                    mainTitle = "Antico Gatoleto"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "016") {
                    mainTitle = "Osteria Da Alberto"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "017") {
                    mainTitle = "Osteria Al Ponte"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "018") {
                    mainTitle = "Trattoria Bandierette"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }

                else if (mIdPlace == "019") {
                    mainTitle = "Trattoria Agli Artisti Pizzeria"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "020") {
                    mainTitle = "Osteria Alla Staffa"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "021") {
                    mainTitle = "6342 Alla Corte Spaghetteria"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "022") {
                    mainTitle = "Al Giardinetto Da Severino"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "023") {
                    mainTitle = "Trattoria Da Remigio"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "024") {
                    mainTitle = "Osteria Oliva Nera"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "025") {
                    mainTitle = "Hostaria Castello"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "026") {
                    mainTitle = "Trattoria Da Jonny"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "027") {
                    mainTitle = "Taverna Scalinetto"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "028") {
                    mainTitle = "Bacaretto Cicchetto"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "029") {
                    mainTitle = "Ristorante Carpaccio"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "030") {
                    mainTitle = "Trattoria Storica"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "031") {
                    mainTitle = "Osteria Giorgione Da Masa"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "032") {
                    mainTitle = "Hostaria Bacanera"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
                else if (mIdPlace == "033") {
                    mainTitle = "Corte Sconta"
                    Constants.Title_set = mainTitle
                    val selected = cache?.first { it.catInfo.nome == mainTitle }
                    if (selected != null) {
                        Constants.m_category = selected
                    }
                }
            }
            else {
                val selected = cache?.first { it.catInfo.nome == mainTitle }
                if (selected != null) {
                    Constants.m_category = selected
                }
            }

        } catch (e: Exception) {

        }

    }

    private fun loadSectionTitles() {
        loadTitles()
    }

    private fun loadTitles() {
        val titles = Constants.m_category.catConfig.sections.map { it.name ?: "N/A" }
        binding.progressBar.visibility = View.VISIBLE
        if (titles.isNotEmpty()){
            binding.progressBar.visibility = View.GONE
            binding.homeMainConstraint.visibility = View.VISIBLE
            viewModel.sectionTitles.addAll(titles)
        }else{
            Toast.makeText(activity?.applicationContext, "something went wrong!!", Toast.LENGTH_LONG).show()
            if (titles.isNotEmpty()){
                binding.progressBar.visibility = View.GONE
                binding.homeMainConstraint.visibility = View.VISIBLE
                viewModel.sectionTitles.addAll(titles)
            }else{
                binding.progressBar.visibility = View.VISIBLE
                binding.homeMainConstraint.visibility = View.GONE
            }
        }
    }

    private fun createDishModelsAndSelectFirst() {
        val items = Constants.m_category.catItems

        for ((_, title) in viewModel.sectionTitles.withIndex()) {
            val dishType =
                enumValues<ProductType>().find { it.toString().equals(title, ignoreCase = true) }
            val catItemsForDishType = items.filter { it.sezione == dishType.toString() }
            dishType?.let {
                val dishModel =
                    DishModel(
                        title = dishType.toString(),
                        dishType = it,
                        image = dishType.getImage(),
                        catItems = catItemsForDishType
                    )
                viewModel.dishModels.add(dishModel)
            }
        }
        dishesAdapter.submitList(viewModel.dishModels)

        val showMenu = viewModel.selected
        if (showMenu == null) {
            try {
                val selectedFirst = viewModel.dishModels.first()
                viewModel.selected = selectedFirst
                binding.searchView.txtDashTitle.text = selectedFirst.title
                showMenu(selectedFirst)
            } catch (e: Exception) {

            }

        } else {
            binding.searchView.txtDashTitle.text = viewModel.selected?.title
            showMenu(showMenu)
        }
    }

    private fun editTextObserver() {
        binding.searchView?.searchInputText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchList(binding.searchView.searchInputText.text.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setClickListeners() {
        binding.apply {
            searchView.onOpenSearchClicked = {
            }
            searchView.onCloseSearchClicked = {
                hideKeyboard(requireActivity())
                viewModel.selected?.let { showMenu(it) }
                menuListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun getLocalizedText(item: MenuAdapterItem.CatItem?, language: String?): String? {
        return when (language) {
            "ru" -> item?.nomeRu
            "de" -> item?.nomeDe
            "en" -> item?.nomeEn
            "es" -> item?.nomeEs
            "fr" -> item?.nomeFr
            "zh" -> item?.nomeZh
            else -> item?.nome
        }
    }

    private fun getLocalizedIngredient(item: MenuAdapterItem.CatItem?, language: String?): String? {
        return when (language) {
            "ru" -> item?.ingredientiRu
            "de" -> item?.ingredientiDe
            "en" -> item?.ingredientiEn
            "es" -> item?.ingredientiEs
            "fr" -> item?.ingredientiFr
            "zh" -> item?.ingredientiZh
            else -> item?.ingredienti
        }
    }

    private fun searchList(searchString: String) {
        if (searchString.isEmpty() || searchString.isBlank()) {
            viewModel.menuList.clear()
            viewModel.menuList.addAll(viewModel.startingList)
            menuListAdapter.notifyDataSetChanged()
        } else {
            val tmpArrayList = mutableListOf<MenuAdapterItem.CatItem>()
            for (i in viewModel.menuList.indices) {
                val currentItem = viewModel.menuList[i]

                for (lang in listOf("ru", "de", "en", "es", "fr", "zh", "")) {
                    if (currentItem is MenuAdapterItem.CatItem) {
                        val name = getLocalizedText(currentItem, lang)
                        if (name?.lowercase(Locale.getDefault())
                                ?.contains(searchString.lowercase(Locale.getDefault())) == true
                        ) {
                            tmpArrayList.add(currentItem)
                            break
                        }
                    }

                }

                for (lang in listOf("ru", "de", "en", "es", "fr", "zh", "")) {
                    if (currentItem is MenuAdapterItem.CatItem) {
                        val ingredient = getLocalizedIngredient(currentItem, lang)
                        if (ingredient?.lowercase(Locale.getDefault())
                                ?.contains(searchString.lowercase(Locale.getDefault())) == true
                        ) {
                            tmpArrayList.add(currentItem)
                            break
                        }
                    }
                }
            }
            val menuList = groupBySubCat(tmpArrayList).toList().flatMap { (subCat, items) ->
                listOf(MenuAdapterItem.Section(subCat)) + items
            }

            val newList = viewModel.assignFirstLastFlags(menuList)
            viewModel.menuList.clear()
            viewModel.menuList.addAll(newList)
            menuListAdapter.submitList(newList)

            binding.menuListRecycler.scrollToPosition(0)
            menuListAdapter.notifyDataSetChanged()
        }
    }

    private fun groupBySubCat(items: List<MenuAdapterItem.CatItem>): Map<String, List<MenuAdapterItem.CatItem>> {
        val groupedBySubCat = items.groupBy { it.subCat }
        return groupedBySubCat.toSortedMap(compareBy { it?.lowercase() })
    }
}