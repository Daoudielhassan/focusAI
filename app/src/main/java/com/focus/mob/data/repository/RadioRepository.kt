package com.focus.mob.data.repository

import com.focus.mob.network.RadioBrowserApi
import com.focus.mob.network.model.RadioStation
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioRepository @Inject constructor(
    private val api: RadioBrowserApi
) {
    suspend fun fetchStation(tag: String, limit: Int = 5): RadioStation? {
        return try {
            val stations = api.searchStations(tag = tag, limit = limit)
            stations.firstOrNull()
        } catch (e: Exception) {
            Timber.e(e, "RadioRepository: failed to fetch stations for tag=$tag")
            null
        }
    }
}
