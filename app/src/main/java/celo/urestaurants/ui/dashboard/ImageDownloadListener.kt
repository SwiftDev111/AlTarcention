package celo.urestaurants.ui.dashboard

import celo.urestaurants.adapters.LocationAdapter

interface ImageDownloadListener {
    fun onImageDownloaded(uri: String)
    abstract fun LocationAdapter(): LocationAdapter
}
