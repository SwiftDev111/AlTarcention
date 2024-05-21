package celo.urestaurants.constants

import celo.urestaurants.models.CategoryModel

object Constants {
    const val prefName = "URestaurants_Prefs"
    const val PREF_KEY_IS_FIRST = "URestaurants_Is_First"
    const val PREF_KEY_MAIN_TITLE = "URestaurants_Main_Title"
    const val PREF_KEY_MAIN_KEY = "URestaurants_Main_KEY"
    const val PREF_KEY_IMAGE_URL = "URestaurants_Image_URL"

    const val TERMS_PREFERENCE = "TermsPreference"
    const val TERMS_AND_CONDITIONS = "termsAndConditions"
    const val GOOGLE_SIGN_IN = "SignInGoogle"
    const val FIREBASE_NODE_APP = "FirebaseNode"
    const val FIREBASE_NODE_Auth = "FirebaseNodeAuth"
    const val GOOGLE_MAIL = "email"
    const val GOOGLE_NAME = "displayName"
    const val GOOGLE_FAMILY_NAME = "familyName"
    const val GOOGLE_ACCOUNT = "account"
    const val GOOGLE_ACCOUNT_UID = "account_uid"

    var m_category = CategoryModel()
    var m_categories = mutableListOf<CategoryModel>()


    var m_PlaceID = ""
    var Title_set = ""
    var LT_LNG = ""
    var currentLAT = ""
    var currentLONG = ""
    var cityNames: MutableList<String> = mutableListOf()
    var Selected_city = ""
    var urlImages = mutableListOf<String>()
}
