package com.masjoel.githubuserapi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var username: String = "",
    var name: String = "",
    var avatar: String = "",
    var uFollowersUrl: String = "",
    var uFollowingUrl: String = "",
    var uCompany: String = "",
    var uLocation: String = "",
    var uRepository: String = "",
    var uFollowers: String = "",
    var uFollowing: String = ""
) : Parcelable