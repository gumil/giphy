package com.gumil.giphy.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GiphyListResponse(
    @Json(name = "data")
    val data: List<Giphy>
)

@JsonClass(generateAdapter = true)
data class GiphyResponse(
    @Json(name = "data")
    val data: Giphy
)

@JsonClass(generateAdapter = true)
data class Giphy(
    @Json(name = "user") val user: User?,
    @Json(name = "images") val images: Images,
    @Json(name = "title") val title: String
)

@JsonClass(generateAdapter = true)
data class Images(
    @Json(name = "original") val original: Original,
    @Json(name = "fixed_width_downsampled") val fixedWidthDownsampled: FixedWidthDownsampled,
    @Json(name = "downsized") val downsized: Downsized
)

@JsonClass(generateAdapter = true)
data class Downsized(
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class Original(
    @Json(name = "url") val url: String,
    @Json(name = "width") val width: String,
    @Json(name = "height") val height: String
)

@JsonClass(generateAdapter = true)
data class FixedWidthDownsampled(
    @Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "profile_url") val profileUrl: String,
    @Json(name = "display_name") val displayName: String
)