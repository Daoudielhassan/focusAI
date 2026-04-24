package com.focus.mob.network.model

import com.google.gson.annotations.SerializedName

data class RadioStation(
    @SerializedName("stationuuid") val uuid: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("url_resolved") val streamUrl: String = "",
    @SerializedName("tags") val tags: String = "",
    @SerializedName("codec") val codec: String = "",
    @SerializedName("bitrate") val bitrate: Int = 0,
    @SerializedName("votes") val votes: Int = 0
)
