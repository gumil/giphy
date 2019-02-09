package com.gumil.giphy

import android.annotation.SuppressLint
import android.os.Parcelable
import com.gumil.giphy.network.Giphy
import com.gumil.giphy.network.Images
import com.gumil.giphy.network.User
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@SuppressLint("ParcelCreator")
@Parcelize
internal data class GiphyItem(
        val title: String,
        val user: UserItem?,
        val image: ImageItem
) : Parcelable, Serializable

@SuppressLint("ParcelCreator")
@Parcelize
data class ImageItem(
        val original: String,
        val resized: String,
        val width: Int,
        val height: Int
) : Parcelable, Serializable

@SuppressLint("ParcelCreator")
@Parcelize
data class UserItem(
        val avatarUrl: String,
        val profileUrl: String,
        val displayName: String
) : Parcelable, Serializable

internal fun Giphy.mapToItem(): GiphyItem {
    return GiphyItem(title, user?.mapToItem(), images.mapToItem())
}

internal fun User.mapToItem(): UserItem {
    return UserItem(avatarUrl, profileUrl, displayName)
}

internal fun Images.mapToItem(): ImageItem {
    return ImageItem(
            downsized.url,
            fixedWidthDownsampled.url,
            original.width.toInt(),
            original.height.toInt()
    )
}