package com.focus.mob.network

import com.focus.mob.network.model.RadioStation
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserApi {
    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("tag") tag: String = "ambient",
        @Query("limit") limit: Int = 5,
        @Query("hidebroken") hideBroken: Boolean = true,
        @Query("order") order: String = "votes"
    ): List<RadioStation>
}
